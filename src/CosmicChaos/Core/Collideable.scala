package CosmicChaos.Core

import com.badlogic.gdx.math.Rectangle

trait Collideable {
  val collisionBox: Rectangle
  var collisionLayer: Int = 0   // The layer the Collideable lives on
  var collisionMask: Int = 0    // The layer the Collideable interacts with

  def onCollideWith(other: Collideable): Unit = { }

  def shouldCollideWith(other: Collideable): Boolean =
    (this.collisionMask & other.collisionLayer) != 0

  object CollisionLayers extends Enumeration {
    type CollisionLayers = Value
    val none: Int         = 0
    val player: Int       = 1 << 0
    val enemy: Int        = 1 << 1
    val bullet: Int       = 1 << 2
    val world: Int        = 1 << 3
    val props: Int        = 1 << 4
    val interactable: Int = 1 << 5
    val all: Int    = Int.MaxValue
  }
}
