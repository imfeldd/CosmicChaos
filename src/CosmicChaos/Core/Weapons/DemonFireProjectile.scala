package CosmicChaos.Core.Weapons

import CosmicChaos.Entities.{CreatureEntity, Explosion, Fire}
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3

class DemonFireProjectile(damage: Float, holder: CreatureEntity) extends Rocket(damage, holder) {
  override val name: String = "Rocket"

  private var initialPosition: Vector3 = _
  private var freezeTimer: Float = 0.2f

  collisionLayer = CollisionLayers.bullet
  collisionMask = CollisionLayers.player + CollisionLayers.worldSolid + CollisionLayers.props

  override def onEnterGameWorld(): Unit = {
    super.onEnterGameWorld()
    initialPosition = new Vector3(position.x, position.y, 0)
  }

  override def onUpdate(dt: Float): Unit = {
    freezeTimer -= dt

    if(freezeTimer >= 0.0f)
      return

    super.onUpdate(dt)
  }

  override def explode(): Unit = {
    val explosion = new Explosion(damage * parent.stats.damage, 96, parent)
    explosion.position = new Vector3(position.x, position.y, 0)
    parentGameWorld.addGameObject(explosion)

    // Spawn the fire
    val fire = new Fire(damage * parent.stats.damage, 96*3, parent)
    fire.position = new Vector3(position.x, position.y, 0)
    parentGameWorld.addGameObject(fire)

    parentGameWorld.removeGameObject(this)
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    super.onGraphicRender(g)
    g.drawFilledCircle(position.x, position.y, 40, Color.YELLOW)
  }

  override def copy: Projectile = new DemonFireProjectile(damage, parent)
}
