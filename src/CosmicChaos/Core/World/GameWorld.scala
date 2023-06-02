package CosmicChaos.Core.World

import CosmicChaos.Core.GameObject
import CosmicChaos.Entities.PlayerEntity

import scala.collection.mutable.ArrayBuffer

class GameWorld {
  val gameObjects: ArrayBuffer[GameObject] = new ArrayBuffer[GameObject]

  var playerEntity: PlayerEntity = _

  def addGameObject(gameObject: GameObject): Unit= {
    gameObject match {
      case entity: PlayerEntity => playerEntity = entity
      case _ =>
    }

    gameObject.parentGameWorld = this
    gameObjects.addOne(gameObject)
    gameObject.onEnterGameWorld()
  }

  def removeGameObject(gameObject: GameObject): Unit = {
    gameObject.onLeaveGameWorld
    gameObjects -= gameObject
  }
}
