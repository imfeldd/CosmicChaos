package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.Stats.{EntityStats, EntityStatsScaling}
import CosmicChaos.Entities.CreatureEntity
import CosmicChaos.Utils.Animation
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}

class SquidMinionEnemyEntity extends CreatureEntity {
  private val spriteScale = 2.5f
  private val spritesheet: Texture = new Texture("data/images/boss/squid/squid_minion.png")
  spritesheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)
  private val (frameW, framwH) = (spritesheet.getWidth / 3, spritesheet.getHeight)
  private val frames: Array[Array[TextureRegion]] = TextureRegion.split(spritesheet, frameW, framwH)
  private val animation = new Animation(0.09f, frames(0), loop = true)

  private var floatTimer: Float = 0.0f
  private var attackTimer: Float = 0.0f

  override val name: String = "Medusa's Minion"
  override val baseStats: EntityStats = new EntityStats(
    maxHealth = 100,
    maxSpeed = 350,
    acceleration = 10,
    damage = 10,
    criticalChance = 1.0f,
    attackSpeed = 1.0f
  )
  override var stats: EntityStats = baseStats
  override var statsScaling: EntityStatsScaling = EntityStatsScaling(
    maxHealthPerLevel = 1.0f,
    damagePerLevel = 0.2f,
    healthRegenPerItem = 0.0f
  )

  private val collBoxSize: Vector2 = new Vector2(20 * spriteScale, 40 * spriteScale)
  override val collisionBox: Rectangle = new Rectangle((-frameW*spriteScale + collBoxSize.x)/2, (-framwH*spriteScale + collBoxSize.y)/2, collBoxSize.x, collBoxSize.y)

  collisionLayer = CollisionLayers.enemy
  collisionMask = CollisionLayers.worldSolid + CollisionLayers.props

  cash = 20

  override def onGraphicRender(g: GdxGraphics): Unit = {
    animation.update(Gdx.graphics.getDeltaTime)
    drawSprite(animation.getCurrentFrame, g, spriteScale)
  }

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)
    floatTimer += dt
    attackTimer -= dt

    val (a, b) = (parentGameWorld.playerEntity.position, position)
    val vecToPlayer = new Vector3(a.x - b.x, a.y - b.y, a.z - b.z)

    aimVector = new Vector2(vecToPlayer.x, vecToPlayer.y)


    if (parentGameWorld.playerEntity.position.dst(position) < 50) {
      // If within player reach, hurt them
      if(attackTimer <= 0.0f) {
        dealDamageTo(stats.damage, parentGameWorld.playerEntity)
        attackTimer = 2.0f // attack every 2 sec
      }
    }
    else {
      // Move slowly towards the player
      val speed = stats.maxSpeed
      position = position.add(vecToPlayer.nor.scl(speed * dt))
      position.y += math.sin(floatTimer * 3.0f).toFloat * 5.5f
      position.x += math.cos(floatTimer * 3.0f).toFloat * 5.5f
    }
  }
}
