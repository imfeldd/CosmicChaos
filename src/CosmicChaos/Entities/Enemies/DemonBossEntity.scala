package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.Stats.EntityStats
import CosmicChaos.Entities.CreatureEntity
import CosmicChaos.Screens.GameScreen
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}

import scala.collection.mutable.ArrayBuffer

class DemonBossEntity extends CreatureEntity {
  override val baseStats: EntityStats = new EntityStats(
    maxHealth = 1000,
    maxSpeed = 22,
    acceleration = 10,
    damage = 70,
    criticalChance = 0,
    attackSpeed = 1
  )
  override var stats: EntityStats = baseStats

  override val name: String = "Hellfire Overlord"
  override val nameSubtitle: String = "Inferno's Wrath"

  private val spriteScale = 3.5f

  private val walkSpritesheet: Texture = new Texture("data/images/boss/demon/demon_walk.png")
  walkSpritesheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (frameW, framwH) = (walkSpritesheet.getWidth/12, walkSpritesheet.getHeight)
  private val walkFrames: Array[Array[TextureRegion]] = TextureRegion.split(walkSpritesheet, frameW, framwH)
  private val walkAnimation = new Animation(0.13f, walkFrames(0), loop = true)

  private val attackSpritesheet: Texture = new Texture("data/images/boss/demon/demon_attack.png")
  attackSpritesheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val attackFrames: Array[Array[TextureRegion]] = TextureRegion.split(attackSpritesheet, frameW, framwH)
  private val attackAnimation = new Animation(0.13f, attackFrames(0), loop = false)

  private val fireSpritesheet: Texture = new Texture("data/images/fire_circle.png")
  fireSpritesheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (fireFrameW, fireFramwH) = (fireSpritesheet.getWidth/60, fireSpritesheet.getHeight)
  private val fireFrames: Array[Array[TextureRegion]] = TextureRegion.split(fireSpritesheet, fireFrameW, fireFramwH)
  private val fireAnimation = new Animation(0.01f, fireFrames(0), loop = true)

  private val collBoxSize: Vector2 = new Vector2(80 * spriteScale, 100 * spriteScale)
  override val collisionBox: Rectangle = new Rectangle((-frameW*spriteScale + collBoxSize.x)/2 + 45*spriteScale, (-framwH*spriteScale + collBoxSize.y)/2 + 20*spriteScale, collBoxSize.x, collBoxSize.y)

  private var damageTakenThisPhase: Float = 0.0f
  private var ringOfFireRadius: Float = 0.0f
  private val scorchRadius = 1024
  private var lifetimeTimer: Float = 0.0f
  private var ringOfFireDamageTimer: Float = 0.0f
  private var phaseTimer: Float = 0.0f
  private var isAttacking: Boolean = false
  private var attackTimer: Float = 0.0f
  private var attackPoint: Vector2 = new Vector2(0, 0)

  collisionLayer = CollisionLayers.enemy
  collisionMask = CollisionLayers.none
  cash = 777

  override def onEnterGameWorld(): Unit = {
    setLevel(30)
    super.onEnterGameWorld()
  }

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)
    lifetimeTimer += dt
    ringOfFireDamageTimer -= dt
    phaseTimer += dt
    attackTimer -= dt

    // Increase the radius of the ring of fire
    if(ringOfFireRadius < scorchRadius) {
      ringOfFireRadius += math.min(1800*dt, scorchRadius - ringOfFireRadius)

      val scorchRadiusTiles = ringOfFireRadius.toInt / parentGameWorld.cellularAutomata.tileSize + 1
      val scorchedTiles = new ArrayBuffer[(Int, Int)]()
      val (tpX, tpY) = (parentGameWorld.teleporter.position.x / parentGameWorld.cellularAutomata.tileSize - 1, parentGameWorld.teleporter.position.y / parentGameWorld.cellularAutomata.tileSize - 1)
      for (x <- -scorchRadiusTiles to scorchRadiusTiles;
           y <- -scorchRadiusTiles to scorchRadiusTiles
           if math.sqrt(x * x + y * y) <= scorchRadiusTiles) {
        scorchedTiles.addOne(((tpX + x).toInt, (tpY + y).toInt))
      }
      parentGameWorld.cellularAutomata.scorchedTiles = scorchedTiles
    }

    if(!isAttacking) {
      // Move towards the player
      val (a, b) = (parentGameWorld.playerEntity.position, position)
      val vecToPlayer = new Vector2(a.x - b.x, a.y - b.y)
      aimVector = vecToPlayer.nor()
      position.add(aimVector.x * stats.maxSpeed * dt, aimVector.y * stats.maxSpeed * dt, 0)

      // Shake the screen on foot stomping frames
      if (walkAnimation.getCurrentFrameIndex == 1 || walkAnimation.getCurrentFrameIndex == 6) {
        GameScreen.cameraShake = 0.5f
      }

      if(phaseTimer > 3.0) {
        // Start attacking
        isAttacking = true
        phaseTimer = 0
        walkAnimation.reset()
      }
    }
    else {
      if(attackAnimation.getCurrentFrameIndex == 6) {
        attackPoint = new Vector2(parentGameWorld.playerEntity.position.x, parentGameWorld.playerEntity.position.y)
      }
      else if(attackAnimation.getCurrentFrameIndex == 7) {
        // Cook the attack for a short time when the cleaver is raised above the head
        if(phaseTimer <= 1.3f)
          attackAnimation.pause()
        else
          attackAnimation.resume()
      }
      else if (attackAnimation.getCurrentFrameIndex == 8) {
        // Slide towards the player
        if (position.dst(new Vector3(attackPoint.x, attackPoint.y, 0)) > 250) {
          val (a, b) = (attackPoint, position)
          val vecToPlayer = new Vector2(a.x - b.x, a.y - b.y)
          aimVector = vecToPlayer.nor()
          position.add(aimVector.x * stats.maxSpeed * 150 * dt, aimVector.y * stats.maxSpeed * 150 * dt, 0)
          attackAnimation.pause()
        }
        else {
          attackAnimation.resume()
        }
      }
      else if(attackAnimation.getCurrentFrameIndex == 10) {
        GameScreen.cameraShake = 0.7f

        // Check if we should inflict damage to the player
        // TODO: Should also check the angle between the player and the aimVector. Can't figure out the vector magic tho
        if (position.dst(parentGameWorld.playerEntity.position) < 400 && attackTimer <= 0.0f) {
          dealDamageTo(stats.damage, parentGameWorld.playerEntity)
          attackTimer = 2.0f
        }
      }
      else if (attackAnimation.isCurrentlyOver) {
        isAttacking = false
        phaseTimer = 0
        attackAnimation.reset()
      }
    }

    // Deal damage to player if outside of ring of fire
    if(ringOfFireDamageTimer <= 0.0f && parentGameWorld.playerEntity.position.dst(parentGameWorld.teleporter.position) > scorchRadius + 100) {
      this.dealDamageTo(15.0f, parentGameWorld.playerEntity)
      ringOfFireDamageTimer = 0.5f
    }
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    if(isDead) {
      drawDeathExplosionAnimation(g)
      return
    }

    if(ringOfFireRadius < scorchRadius)
      return

    // Draw the ring of fire fire circles
    val fireCirclesCount = 100
    val delta = 1.0f/fireCirclesCount
    for(i <- 0 until fireCirclesCount) {
      val a = math.Pi * 2 * i/fireCirclesCount + lifetimeTimer/8
      val pp = new Vector2(math.sin(a).toFloat, math.cos(a).toFloat).scl(ringOfFireRadius)
      val p = new Vector2(parentGameWorld.teleporter.position.x, parentGameWorld.teleporter.position.y).add(pp.x, pp.y)
      val f = fireAnimation.getFrame(lifetimeTimer + i)
      g.draw(f, p.x + fireFrameW/2, p.y + fireFramwH/2, fireFrameW * 2, fireFramwH * 2)
    }

    if(!isAttacking) {
      walkAnimation.update(Gdx.graphics.getDeltaTime)
      drawSprite(walkAnimation.getCurrentFrame, g, spriteScale, offset = new Vector2(0, 250))
    }
    else {
      attackAnimation.update(Gdx.graphics.getDeltaTime)
      drawSprite(attackAnimation.getCurrentFrame, g, spriteScale, offset = new Vector2(0, 250))
    }
  }

  override def onReceiveDamage(amount: Float, source: CreatureEntity, wasCrit: Boolean): Unit = {
    super.onReceiveDamage(amount, source, wasCrit)
    damageTakenThisPhase += amount
  }
}
