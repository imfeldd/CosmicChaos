package CosmicChaos.Entities
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.{Vector2, Vector3}

case class Projectile(damage: Float, parent: Entity) extends Entity {
  override val name: String = "Bullet"
  override val baseStats: EntityStats = EntityStats(0, 0, 0, damage)
  override var stats: EntityStats = baseStats

  // Spawn at the parent's position, plus a little bit outwards to look like it goes out of the gun barrel
  // TODO: The distance from the center shouldn't be hard-coded. Maybe this shouldn't even be here
  position = new Vector3(parent.position.x, parent.position.y, 0).add(new Vector3(parent.aimVector.x, parent.aimVector.y, 0).nor.scl(110))

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)
    position.x += velocity.x * dt
    position.y += velocity.y * dt
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    super.onGraphicRender(g)

    g.drawFilledCircle(position.x - 5, position.y - 5, 10, Color.RED)
  }
}
