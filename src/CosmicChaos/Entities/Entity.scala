package CosmicChaos.Entities

import CosmicChaos.Core.{Collideable, GameObject, Renderable, Spatial}
import com.badlogic.gdx.math.Vector2

abstract class Entity extends GameObject with Renderable with Spatial with Collideable {
  val name: String
  var velocity: Vector2 = new Vector2(0, 0)
}
