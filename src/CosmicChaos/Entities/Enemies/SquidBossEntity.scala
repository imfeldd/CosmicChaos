package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.Stats.{EntityStats, EntityStatsScaling}
import CosmicChaos.Entities.CreatureEntity
import CosmicChaos.Entities.Enemies.SquidBossPhase.SquidBossPhase
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}

import scala.collection.mutable.ArrayBuffer

object SquidBossPhase extends Enumeration {
  type SquidBossPhase = Value
  val spawnMinion, defenseless = Value
}

class SquidBossEntity extends CreatureEntity {
  override val baseStats: EntityStats = new EntityStats(
    maxHealth = 1000,
    maxSpeed = 12,
    acceleration = 10,
    damage = 30,
    criticalChance = 0,
    attackSpeed = 1
  )
  override var statsScaling: EntityStatsScaling = EntityStatsScaling(
    maxHealthPerLevel = 99.0f,
    damagePerLevel = 0.4f,
    healthRegenPerItem = 0.0f
  )
  override var stats: EntityStats = baseStats

  override val name: String = "Medusa's Head"
  override val nameSubtitle: String = "Severed Abomination"

  private val spriteScale = 3.5f

  private val spritesheet: Texture = new Texture("data/images/boss/squid/squid.png")
  spritesheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (frameW, framwH) = (spritesheet.getWidth/8, spritesheet.getHeight)
  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(spritesheet, frameW, framwH)
  private val animation = new Animation(0.09f, frames(0), loop = true)

  private val shieldSpritesheet: Texture = new Texture("data/images/barrier.png")
  shieldSpritesheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (shieldFrameW, shieldFrameH) = (shieldSpritesheet.getWidth / 4, shieldSpritesheet.getHeight)
  private val shieldFrames: Array[Array[TextureRegion]] = TextureRegion.split(shieldSpritesheet, shieldFrameW, shieldFrameH)
  private val shieldAnimation = new Animation(0.09f, shieldFrames(0), loop = true)

  private val collBoxSize: Vector2 = new Vector2(50 * spriteScale, 70 * spriteScale)
  override val collisionBox: Rectangle = new Rectangle((-frameW*spriteScale + collBoxSize.x)/2, (-framwH*spriteScale + collBoxSize.y)/2, collBoxSize.x, collBoxSize.y)

  private var floatTimer: Float = 0.0f
  private var damageTakenThisPhase: Float = 0.0f
  private var currentPhase: SquidBossPhase = SquidBossPhase.defenseless
  private var phaseTimer: Float = 0.0f
  private var minionSpawnTimer: Float = 0.0f
  private var spawnedMinions: ArrayBuffer[SquidMinionEnemyEntity] = new ArrayBuffer[SquidMinionEnemyEntity]()

  collisionLayer = CollisionLayers.enemy
  collisionMask = CollisionLayers.none
  cash = 777

  override def onEnterGameWorld(): Unit = {
    setLevel(30)
    super.onEnterGameWorld()
  }

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)
    phaseTimer += dt

    val (a, b) = (parentGameWorld.playerEntity.position, position)
    val vecToPlayer = new Vector2(a.x - b.x, a.y - b.y)

    aimVector = vecToPlayer

    if(currentPhase == SquidBossPhase.spawnMinion) {
      minionSpawnTimer -= dt
      // If we're in the minion-spawning phase, we're invulnerable
      collisionLayer = CollisionLayers.none

      // If 10 seconds have passed or all the minions are dead, go to the defenseless phase
      if(phaseTimer > 10.0f && !spawnedMinions.exists(!_.isDead)) {
        currentPhase = SquidBossPhase.defenseless
        damageTakenThisPhase = 0
        phaseTimer = 0
        spawnedMinions.clear()
      }
      else {
        // Spawn minions if we haven't spawned one too recently or there are too many
        if(minionSpawnTimer <= 0.0f && spawnedMinions.length < 10) {
          val minion = new SquidMinionEnemyEntity
          minion.position = new Vector3(position.x, position.y, 0)
          parentGameWorld.addGameObject(minion)
          spawnedMinions.addOne(minion)
          minionSpawnTimer = 1.0f
        }
      }
    }
    else {
      collisionLayer = CollisionLayers.enemy

      // Go to the minion spawning phase if 10 seconds have passed or we've taken too much damage
      if(phaseTimer >= 10.0f || damageTakenThisPhase > stats.maxHealth/3.0f) {
        currentPhase = SquidBossPhase.spawnMinion
        damageTakenThisPhase = 0
        phaseTimer = 0
      }
    }
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    if(isDead) {
      drawDeathExplosionAnimation(g)
      return
    }

    // Animate up and down to make it look like we're floating
    floatTimer += Gdx.graphics.getDeltaTime
    position.y += math.sin(floatTimer*3.0f).toFloat*0.5f

    animation.update(Gdx.graphics.getDeltaTime)
    drawSprite(animation.getCurrentFrame, g, spriteScale)

    // Draw the shield during the minion spawning phase
    if(currentPhase == SquidBossPhase.spawnMinion) {
      shieldAnimation.update(Gdx.graphics.getDeltaTime)
      drawSprite(shieldAnimation.getCurrentFrame, g, spriteScale)
    }
  }

  override def onReceiveDamage(amount: Float, source: CreatureEntity, wasCrit: Boolean): Unit = {
    super.onReceiveDamage(amount, source, wasCrit)
    damageTakenThisPhase += amount
  }
}
