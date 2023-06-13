package CosmicChaos.Core.Weapons

import CosmicChaos.Core.Collideable
import CosmicChaos.Entities.{CreatureEntity, Entity}
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.{Rectangle, Vector3}

class Projectile(var damage: Float, val parent: CreatureEntity) extends Entity {
  override val name: String = "Bullet"
  override val collisionBox: Rectangle = new Rectangle(-4, -4, 8, 8)
  collisionLayer = CollisionLayers.bullet
  collisionMask = CollisionLayers.enemy + CollisionLayers.player + CollisionLayers.world + CollisionLayers.props

  protected var timer: Float = 0

  renderLayer = 1

  // Spawn at the parent's position, plus a little bit outwards to look like it goes out of the gun barrel
  // TODO: The distance from the center shouldn't be hard-coded. Maybe this shouldn't even be here
  position = new Vector3(parent.position.x, parent.position.y, 0).add(new Vector3(parent.aimVector.x, parent.aimVector.y, 0).nor.scl(110))

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)

    timer += dt
    position.x += velocity.x * dt
    position.y += velocity.y * dt

    // Despawn the projectile after 3 seconds
    if(timer >= 3) {
      parentGameWorld.removeGameObject(this)
    }
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.drawFilledCircle(position.x - 5, position.y - 5, 10, if(parent.team == 1) Color.BLUE else Color.RED)
  }

  override def onCollideWith(other: Collideable): Unit = {
    other match {
      case c: CreatureEntity =>
        parentGameWorld.removeGameObject(this)
        parent.dealDamageTo(damage, c)
      case _ => parentGameWorld.removeGameObject(this)
    }
  }

  def copy: Projectile = new Projectile(damage, parent)
}
