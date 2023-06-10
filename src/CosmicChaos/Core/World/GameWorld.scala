package CosmicChaos.Core.World

import CosmicChaos.Core.Items.RollbackHealthItem
import CosmicChaos.Core.World.TeleporterEventState.TeleporterEventState
import CosmicChaos.Core.{Collideable, GameObject, Spatial}
import CosmicChaos.Entities.Enemies.ShadowBossEntity
import CosmicChaos.Entities.{CreatureEntity, PlayerEntity, Teleporter}
import com.badlogic.gdx.math._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object TeleporterEventState extends Enumeration {
  type TeleporterEventState = Value
  val notStarted, charging, charged = Value
}

class GameWorld {

  val gameObjects: ArrayBuffer[GameObject] = new ArrayBuffer[GameObject]
  val Music = new MusicPlayer("data/music/mainMusic.mp3")
  Music.setVolume(0.5f)
  Music.loop()

  var playerEntity: PlayerEntity = _
  var teleporter: Teleporter = _
  var currentBoss: Option[CreatureEntity] = None
  var teleporterEventState: TeleporterEventState = TeleporterEventState.notStarted

  var MyAlgo = new CellularAutomata(width = 6000, height = 6000, seed = 1234)
  MyAlgo.worldCreation()

  def update(dt: Float): Unit = {
    if (currentBoss.isDefined && currentBoss.get.isDead) {
      currentBoss = None
    }

    if(teleporterEventState == TeleporterEventState.charging && teleporter.charged && currentBoss.isEmpty){
      teleporterEventState = TeleporterEventState.charged
    }
  }

  def addGameObject(gameObject: GameObject): Unit= {
    gameObject match {
      case entity: PlayerEntity => playerEntity = entity
      case tp: Teleporter => teleporter = tp
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

  def startTeleporterEvent(): Unit = {
    // Spawn boss
    val shadow = new ShadowBossEntity
    shadow.addItemToInventory(new RollbackHealthItem, 1)

    val newPos: Vector2 = new Vector2(1, 1).rotate(Random.between(0, 360)).nor().scl(Random.between(50, 200))
    shadow.position = new Vector3(playerEntity.position.x, playerEntity.position.y, 0).add(new Vector3(newPos.x, newPos.y, 0))

    currentBoss = Some(shadow)
    addGameObject(shadow)

    teleporterEventState = TeleporterEventState.charging
  }

  def isTeleporterEventActive: Boolean =
    teleporterEventState == TeleporterEventState.charging

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
