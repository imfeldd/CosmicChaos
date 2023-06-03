package CosmicChaos.Entities

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.math.{Vector2, Vector3}

class ImmortalSnailEnemy extends Entity {
  private val snailTexture = new BitmapImage("data/images/entities/snail.png").getImage
  snailTexture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override val name: String = "Immortal Snail"
  override val baseStats: EntityStats = EntityStats(100, 12, 10, 9999999)
  override var stats: EntityStats = baseStats

  override def onGraphicRender(g: GdxGraphics): Unit = {
    drawSprite(snailTexture, g, 3)
  }

  override def onUpdate(dt: Float): Unit = {
    val (a, b) = (parentGameWorld.playerEntity.position, position)
    val vecToPlayer = new Vector3(a.x - b.x, a.y - b.y, a.z- b.z)

    aimVector = new Vector2(vecToPlayer.x, vecToPlayer.y)


    if(parentGameWorld.playerEntity.position.dst(position) < 50) {
      // If within player reach, kill them
      dealDamageTo(stats.baseDamage, parentGameWorld.playerEntity)
    }
    else {
      // Move slowly towards the player
      val speed = stats.maxSpeed
      position = position.add(vecToPlayer.nor.scl(speed * dt, speed * dt, speed * dt))

    }
  }

  override def onReceiveDamage(amount: Float, source: Entity): Unit = {}
}
