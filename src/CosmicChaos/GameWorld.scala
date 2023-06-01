package CosmicChaos

import scala.collection.mutable.ArrayBuffer

class GameWorld {
  val gameObjects: ArrayBuffer[GameObject] = new ArrayBuffer[GameObject]

  var playerEntity: PlayerEntity = null
  def getPlayer: PlayerEntity = {
    playerEntity = playerEntity match {
      case null => gameObjects.find(_.isInstanceOf[PlayerEntity]).get.asInstanceOf[PlayerEntity]  // There should always be a player in our world
      case _ => playerEntity
    }
    playerEntity
  }
}
