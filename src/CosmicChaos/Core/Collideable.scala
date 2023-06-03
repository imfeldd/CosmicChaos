package CosmicChaos.Core

import com.badlogic.gdx.math.Rectangle

trait Collideable {
  val collisionBox: Rectangle
  val collisionLayer: Int = 0
  val collisionMask: Int = 0

  def onCollideWith(other: Collideable): Unit = { }
}
