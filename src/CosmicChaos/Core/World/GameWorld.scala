package CosmicChaos.Core.World

import CosmicChaos.Core.{Collideable, GameObject, Spatial}
import CosmicChaos.Entities.{CreatureEntity, PlayerEntity}
import ch.hevs.gdx2d.components.audio.MusicPlayer
import com.badlogic.gdx.math.{Circle, Intersector, Rectangle}

import scala.collection.mutable.ArrayBuffer

class GameWorld {
  val gameObjects: ArrayBuffer[GameObject] = new ArrayBuffer[GameObject]
  val Music = new MusicPlayer("data/music/mainMusic.mp3")
  Music.setVolume(0.5f)
  Music.loop()

  var playerEntity: PlayerEntity = _
  var currentBoss: Option[CreatureEntity] = None

  var MyAlgo = new CellularAutomata(width = 6000, height = 6000, seed = 1234)
  MyAlgo.worldCreation()

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
    gameObject.onLeaveGameWorld()
    gameObjects -= gameObject
  }

  def getCollideablesWithinCircle(circle: Circle): Array[Collideable] = {
    val collideables = gameObjects.filter(_.isInstanceOf[Collideable with Spatial]).map(_.asInstanceOf[Collideable with Spatial])
    val out: ArrayBuffer[Collideable] = new ArrayBuffer[Collideable]()
    for (col <- collideables) {
      val rec = new Rectangle(col.collisionBox.x + col.position.x, col.collisionBox.y + col.position.y, col.collisionBox.getWidth, col.collisionBox.getWidth)
      if(Intersector.overlaps(circle, rec))
        out.append(col)
    }
    out.toArray
  }
}
