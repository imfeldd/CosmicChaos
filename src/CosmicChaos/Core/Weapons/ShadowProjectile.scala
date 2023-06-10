package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3

class ShadowProjectile(damage: Float, holder: CreatureEntity) extends Projectile(damage, holder) {
  override val name: String = "Rocket"

  private var initialPosition: Vector3 = _
  private var freezeTimer: Float = 0.2f

  collisionLayer = CollisionLayers.bullet
  collisionMask = CollisionLayers.player + CollisionLayers.world + CollisionLayers.props

  override def onEnterGameWorld(): Unit = {
    super.onEnterGameWorld()
    initialPosition = new Vector3(position.x, position.y, 0)
  }

  override def onUpdate(dt: Float): Unit = {
    freezeTimer -= dt

    if(freezeTimer >= 0.0f)
      return

    position.x += velocity.x * dt
    position.y += velocity.y * dt

    // Despawn the projectile after 3 seconds
    if(timer >= 3) {
      parentGameWorld.removeGameObject(this)
    }
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.drawFilledCircle(position.x, position.y, 40, Color.DARK_GRAY)
  }

  override def copy: Projectile = new ShadowProjectile(damage, parent)
}
