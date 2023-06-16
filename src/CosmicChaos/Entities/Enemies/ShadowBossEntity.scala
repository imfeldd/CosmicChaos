package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.Stats.{EntityStats, EntityStatsScaling}
import CosmicChaos.Core.Weapons.{ShadowProjectile, Weapon}
import CosmicChaos.Entities.CreatureEntity
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}

import scala.util.Random

class ShadowBossEntity extends CreatureEntity {
  private val weapon: Weapon = new Weapon(new ShadowProjectile(3, this), true, 999, this, inaccuracy = 1.5f) {}
  override val baseStats: EntityStats = new EntityStats(
    maxHealth = 1000,
    maxSpeed = 12,
    acceleration = 10,
    damage = 30,
    criticalChance = 0,
    attackSpeed = 1
  )
  override var stats: EntityStats = baseStats
  override var statsScaling: EntityStatsScaling = EntityStatsScaling(
    maxHealthPerLevel = 75.0f,
    damagePerLevel = 0.5f,
    healthRegenPerItem = 0.0f
  )
  override val name: String = "Shadow Figure"
  override val nameSubtitle: String = "Emissary of Darkness"

  collisionLayer = CollisionLayers.enemy
  collisionMask = CollisionLayers.none
  cash = 777

  private val spriteScale = 5.5f

  private var teleportingTimer = 0.0f

  private val appearSpritesheet: Texture = new Texture("data/images/boss/shadow/shadow_appear.png")
  appearSpritesheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (appearFrameW, appearFrameH) = (appearSpritesheet.getWidth/18, appearSpritesheet.getHeight)
  private val appearFrames: Array[Array[TextureRegion]] = TextureRegion.split(appearSpritesheet, appearFrameW, appearFrameH)
  private val appearAnimation = new Animation(0.09f, appearFrames(0).tail, loop = false)

  private val collBoxSize: Vector2 = new Vector2(50 * spriteScale, 70 * spriteScale)
  override val collisionBox: Rectangle = new Rectangle((-appearFrameW*spriteScale + collBoxSize.x)/2, (-appearFrameH*spriteScale + collBoxSize.y)/2, collBoxSize.x, collBoxSize.y)

  override def onEnterGameWorld(): Unit = {
    setLevel(30)
    super.onEnterGameWorld()
  }

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)
    var shoot: Boolean = false

    val (a, b) = (parentGameWorld.playerEntity.position, position)
    val vecToPlayer = new Vector2(a.x - b.x, a.y - b.y)

    aimVector = vecToPlayer

    if(teleportingTimer >= 0.0f)
      teleportingTimer -= dt

    if (appearAnimation.isCurrentlyOver) {
      if (appearAnimation.reverse) {
        // Descending animation done, start teleporting
        teleportingTimer = 3.0f

        // Teleport somewhere around the player
        val newPos: Vector2 = new Vector2(1, 1).rotate(Random.between(0, 360)).nor().scl(Random.between(250, 400))
        position = new Vector3(parentGameWorld.playerEntity.position.x, parentGameWorld.playerEntity.position.y, 0).add(new Vector3(newPos.x, newPos.y, 0))

        // Spawn a few tentacles around the player
        for(i <- 0 until Random.between(16, 28)) {
          val tentacle = new ShadowTentacleEntity(this, appearDelay = Random.between(0.5f, 1.5f))
          val aroundPos: Vector2 = new Vector2(1, 1).rotate(Random.between(0, 360)).nor().scl(Random.between(120, 900))
          tentacle.position = new Vector3(parentGameWorld.playerEntity.position.x, parentGameWorld.playerEntity.position.y, 0)
          tentacle.position.add(new Vector3(aroundPos.x, aroundPos.y, 0))
          parentGameWorld.addGameObject(tentacle)
        }
      }
      else {
        // Ascending animation done, shoot our projectile
        shoot = true
      }

      weapon.update(shoot, dt)

      // Switch to the other animation direction and go back to the first frame
      appearAnimation.reverse = !appearAnimation.reverse
      appearAnimation.reset()
    }

    // Disable collisions if we're teleporting
    collisionLayer = if(teleportingTimer >= 0.0f) CollisionLayers.none else CollisionLayers.enemy
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    if(teleportingTimer >= 0.0f)
      return

    if(isDead) {
      drawDeathExplosionAnimation(g)
      return
    }

    appearAnimation.update(Gdx.graphics.getDeltaTime)
    drawSprite(appearAnimation.getCurrentFrame, g, spriteScale)
  }
}
