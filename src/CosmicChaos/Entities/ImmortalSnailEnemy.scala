package CosmicChaos.Entities

import CosmicChaos.Core.Stats
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.math.Vector3

class ImmortalSnailEnemy extends Entity {
  override val name: String = "Immortal Snail"
  override val baseStats: Stats = Stats(100, 12, 10)
  override var stats: Stats = baseStats

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.drawRectangle(position.x - 16, position.y - 16, 32, 32, 0)
  }

  override def onUpdate(dt: Float): Unit = {
    val (a, b) = (parentGameWorld.playerEntity.position, position)
    val vecToPlayer = new Vector3(a.x - b.x, a.y - b.y, a.z- b.z)

    // Move slowly towards the player
    val speed = stats.maxSpeed
    position = position.add(vecToPlayer.nor.scl(speed*dt, speed*dt, speed*dt))

    // If within player reach, kill them
    if(parentGameWorld.playerEntity.position.dst(position) < 25)
      dealDamageTo(99999, parentGameWorld.playerEntity)
  }

  override def onReceiveDamage(amount: Int, source: Entity): Unit = {}
}
