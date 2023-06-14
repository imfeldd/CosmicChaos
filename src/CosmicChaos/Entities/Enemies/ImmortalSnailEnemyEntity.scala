package CosmicChaos.Entities.Enemies

import CosmicChaos.Core.Stats.EntityStats
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.math.{Rectangle, Vector2, Vector3}

class ImmortalSnailEnemyEntity extends CreatureEntity {
  private val scale = 3
  private val snailTexture = new BitmapImage("data/images/entities/snail.png").getImage
  snailTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override val name: String = "Immortal Snail"
  override val baseStats: EntityStats = new EntityStats(
    maxHealth = 100,
    maxSpeed =  12,
    acceleration = 10,
    damage = 9999999,
    criticalChance = 1.0f,
    attackSpeed = 1.0f
  )
  override var stats: EntityStats = baseStats
  override val collisionBox: Rectangle = new Rectangle(-snailTexture.getWidth*scale/2, -snailTexture.getHeight/2, snailTexture.getWidth*scale, snailTexture.getHeight*scale)

  collisionLayer = CollisionLayers.enemy
  collisionMask = CollisionLayers.worldSolid + CollisionLayers.props

  override def onGraphicRender(g: GdxGraphics): Unit = {
    drawSprite(snailTexture, g, scale)
  }

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)

    val (a, b) = (parentGameWorld.playerEntity.position, position)
    val vecToPlayer = new Vector3(a.x - b.x, a.y - b.y, a.z- b.z)

    aimVector = new Vector2(vecToPlayer.x, vecToPlayer.y)


    if(parentGameWorld.playerEntity.position.dst(position) < 50) {
      // If within player reach, kill them
      dealDamageTo(stats.damage, parentGameWorld.playerEntity)
    }
    else {
      // Move slowly towards the player
      val speed = stats.maxSpeed
      position = position.add(vecToPlayer.nor.scl(speed*dt))

    }
  }

  protected override def onReceiveDamage(amount: Float, source: CreatureEntity, wasCrit: Boolean): Unit = {}
}
