package CosmicChaos.Core.World

import CosmicChaos.Core.Items.{FreeItemsItem, RollbackHealthItem}
import CosmicChaos.Core.World.TeleporterEventState.TeleporterEventState
import CosmicChaos.Core.{Collideable, GameObject, Spatial}
import CosmicChaos.Entities.Enemies.{FlyingAlienEnemyEntity, SquidBossEntity}
import CosmicChaos.Entities._
import com.badlogic.gdx.math._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object TeleporterEventState extends Enumeration {
  type TeleporterEventState = Value
  val notStarted, charging, charged = Value
}

class GameWorld {

  val gameObjects: ArrayBuffer[GameObject] = new ArrayBuffer[GameObject]

  var playerEntity: PlayerEntity = _
  var teleporter: Teleporter = _
  var currentBoss: Option[CreatureEntity] = None
  var teleporterEventState: TeleporterEventState = TeleporterEventState.notStarted

  var monsterSpawnTimer: Float = 0.0f
  var monsterSpawnBudget: Float = 0.0f

  var cellularAutomata = new CellularAutomata(width = 6000, height = 6000)

  def initializeWorld(): Unit = {
    addGameObject(new PlayerEntity)
    playerEntity.addItemToInventory(new FreeItemsItem, 1)
    teleportToNextLevel()
  }

  def generateLevel(): Unit = {
    teleporterEventState = TeleporterEventState.notStarted
    cellularAutomata.worldCreation()

    val newPlayerPos = cellularAutomata.getRandomClearPosition(2)
    playerEntity.position = new Vector3(newPlayerPos.x, newPlayerPos.y, 0)

    val newTelePos = cellularAutomata.getRandomClearPosition(4)
    val tp = new Teleporter
    tp.position = new Vector3(newTelePos.x, newTelePos.y, 0)
    addGameObject(tp)

    var chestBudget: Float = 20000.0f
    while(chestBudget > 0.0f) {
      val roll: Float = Random.nextFloat()
      val (chest, cost) =
        if(roll <= 0.7)
          (new NormalChest, 800)
        else if(roll <= 0.98)
          (new RareChest, 1600)
        else
          (new LegendaryChest, 3000)

      chestBudget -= cost
      val chestPos = cellularAutomata.getRandomClearPosition(1)
      chest.position = new Vector3(chestPos.x, chestPos.y, 0)
      addGameObject(chest)
    }
  }

  def update(dt: Float): Unit = {
    if (currentBoss.isDefined && currentBoss.get.isDead) {
      currentBoss = None
    }

    monsterSpawnBudget += 30.0f * dt * (if(isTeleporterEventActive) 2.0f else 1.0f)
    monsterSpawnTimer -= dt

    if(monsterSpawnTimer <= 0.0f) {
      trySpawnMonsters()
      monsterSpawnTimer = 10.0f
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

  def trySpawnMonsters(): Unit = {
    val monsters = Array[(Float, CreatureEntity)](
      (100, new FlyingAlienEnemyEntity)
    )

    while(monsterSpawnBudget >= 0.0f) {
      val roll = Random.nextFloat()
      if(roll <= 0.1f)
        return
      else {
        val possibleMonsters = monsters.filter(_._1 <= monsterSpawnBudget)
        if(possibleMonsters.isEmpty) {
          return
        }

        val monster = possibleMonsters(Random.nextInt(possibleMonsters.length))
        val (cost, monsterInstance) = (monster._1, monster._2.getClass.getDeclaredConstructor().newInstance())

        // Find a position not too close or too far from the player
        // TODO: Definitely not the best way to do this
        var pos: Vector2 = null
        var dist: Float = 0
        do {
          pos = cellularAutomata.getRandomClearPosition(1)
          dist = pos.dst(playerEntity.position.x, playerEntity.position.y)
        } while (dist >= 900.0f && dist <= 400.0f)

        monsterInstance.position = new Vector3(pos.x, pos.y, 0)
        monsterSpawnBudget -= cost
        addGameObject(monsterInstance)
      }

    }
  }

  def startTeleporterEvent(): Unit = {
    // Spawn boss
    val shadow = new SquidBossEntity
    shadow.addItemToInventory(new RollbackHealthItem, 1)

    val newPos: Vector2 = new Vector2(1, 1).rotate(Random.between(0, 360)).nor().scl(Random.between(50, 200))
    shadow.position = new Vector3(playerEntity.position.x, playerEntity.position.y, 0).add(new Vector3(newPos.x, newPos.y, 0))

    currentBoss = Some(shadow)
    addGameObject(shadow)

    teleporterEventState = TeleporterEventState.charging
  }

  def isTeleporterEventActive: Boolean =
    teleporterEventState == TeleporterEventState.charging

  def teleportToNextLevel(): Unit = {
    // Convert leftover money to xp
    playerEntity.experience += playerEntity.cash/10.0f
    playerEntity.cash = 0

    teleporter = null
    gameObjects.clear()
    addGameObject(playerEntity)

    generateLevel()
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
