package CosmicChaos.Core.Weapons

import CosmicChaos.Core.Collideable
import CosmicChaos.Entities.{CreatureEntity, Explosion}
import com.badlogic.gdx.math.Vector3

class Rocket(_damage: Float, _parent: CreatureEntity) extends Projectile(_damage, _parent) {
  override val name: String = "Rocket"

  private var initialPosition: Vector3 = _
  private val distanceToTravel = parent.aimVector.len()

  override def onEnterGameWorld(): Unit = {
    super.onEnterGameWorld()
    initialPosition = new Vector3(position.x, position.y, 0)
  }

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)

    if(initialPosition.dst(position) >= distanceToTravel) {
      explode()
    }
  }

  private def explode(): Unit = {
    val explosion = new Explosion(damage * parent.stats.damage, 96, parent)
    explosion.position = new Vector3(position.x, position.y, 0)
    parentGameWorld.addGameObject(explosion)
    parentGameWorld.removeGameObject(this)
  }

  override def onCollideWith(other: Collideable): Unit = {
    explode()
  }


  override def copy: Projectile = new Rocket(damage, parent)
}
