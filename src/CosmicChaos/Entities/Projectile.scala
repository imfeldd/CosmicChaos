package CosmicChaos.Entities
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.{Vector2, Vector3}

class Projectile(damage: Float, velocity: Vector2, parent: Entity) extends Entity {
  override val name: String = "Bullet"
  override val baseStats: EntityStats = EntityStats(0, 0, 0, damage)
  override var stats: EntityStats = baseStats

  position = new Vector3(parent.position.x, parent.position.y, 0)

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
