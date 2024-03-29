package CosmicChaos.Core.World

import CosmicChaos.Core.Items.{Item, ItemRarity, RollbackHealthItem}
import CosmicChaos.Core.World.TeleporterEventState.TeleporterEventState
import CosmicChaos.Core.{Collideable, GameObject, Renderable, Spatial}
import CosmicChaos.Entities.Enemies._
import CosmicChaos.Entities._
import ch.hevs.gdx2d.components.audio.MusicPlayer
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
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
  if(System.getProperty("user.name") != "Dimitri")  // ...
    Music.loop()

  var playerEntity: PlayerEntity = _
  var teleporter: Teleporter = _
  var currentBoss: Option[CreatureEntity] = None
  var teleporterEventState: TeleporterEventState = TeleporterEventState.notStarted

  var monsterSpawnTimer: Float = 0.0f
  var monsterSpawnBudget: Float = 0.0f

  var difficultyScale: Float = 1.0f

  var cellularAutomata = new CellularAutomata(width = 6000, height = 6000)

  def initializeWorld(): Unit = {
    addGameObject(new PlayerEntity)
    teleportToNextLevel()
  }

  def generateLevel(): Unit = {
    teleporterEventState = TeleporterEventState.notStarted
    cellularAutomata.worldCreation()

    val tileSize = cellularAutomata.tileSize

    // Generate collision boxes for walls
    for (row <- 0 until cellularAutomata.numRows) {
      for (column <- 0 until cellularAutomata.numColumns) {
        if(!cellularAutomata.grid(column)(row) &&                 // only wall tiles
          cellularAutomata.countAliveNeighbors(column, row) != 0  // ignore tiles completely surrounded by other tiles
        ) {
          val posX = column * tileSize
          val posY = row * tileSize

          val collisionBox = new GameObject with Collideable with Spatial {
            // TODO: Why is the collision box shifted by one tile to the bottom ?
            override val collisionBox: Rectangle = new Rectangle(0, -tileSize, 128, 128)
            position = new Vector3(posX + tileSize/2, posY + tileSize/2, 0)
            collisionLayer = CollisionLayers.worldEmpty + CollisionLayers.world
            collisionMask = CollisionLayers.none
          }

          addGameObject(collisionBox)
        }
      }
    }

    // Spawn the player in a clear zone
    val newPlayerPos = cellularAutomata.getRandomClearPosition(2)
    playerEntity.position = new Vector3(newPlayerPos.x, newPlayerPos.y, 0)

    // Spawn the teleporter in a clear zone
    val newTelePos = cellularAutomata.getRandomClearPosition(4)
    val tp = new Teleporter
    tp.position = new Vector3(newTelePos.x, newTelePos.y, 0)
    addGameObject(tp)

    // Spawn chests over the level
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
    // Clear the current boss if it just died
    if (currentBoss.isDefined && currentBoss.get.isDead) {
      currentBoss = None
    }

    difficultyScale += dt/60.0f

    monsterSpawnBudget += 30.0f * dt * (if(isTeleporterEventActive) 1.66f else 1.0f)
    monsterSpawnTimer -= dt

    // Disable spawning monsters once the teleporter has been fully charged
    if(teleporterEventState == TeleporterEventState.charged) {
      monsterSpawnTimer = 0
      monsterSpawnBudget = 0
    }

    // Try to spawn a group of monsters if we're allowed to
    if(monsterSpawnTimer <= 0.0f) {
      trySpawnMonsters()
      monsterSpawnTimer = 10.0f
    }

    // Teleporter is fully charged and boss has been defeated
    if(teleporterEventState == TeleporterEventState.charging && teleporter.charged && currentBoss.isEmpty){
      teleporterEventState = TeleporterEventState.charged

      // Give a random rare item to the player
      playerEntity.addItemToInventory(Item.getRandomItemOfRarity(ItemRarity.rare))
    }

    checkCollisions(dt)

    // Update all of our GameObjects
    for (gameObject <- gameObjects.toArray) {
      gameObject.onUpdate(Gdx.graphics.getDeltaTime)
    }
  }

  def draw(g: GdxGraphics): Unit = {
    cellularAutomata.draw(g)

    val renderables = gameObjects.filter(_.isInstanceOf[Renderable with Spatial]).map(_.asInstanceOf[Renderable with Spatial])
    for (renderable <- renderables.sortBy(x => (x.renderLayer, -x.position.y))) { // Sort by render layer descending, then by y pos ascending
      renderable.onGraphicRender(g)
    }
  }

  def checkCollisions(dt: Float): Unit = {
    val collideables = gameObjects.filter(_.isInstanceOf[Collideable with Spatial]).map(_.asInstanceOf[Collideable with Spatial])
    for(firstCollidableIndex <- collideables.indices) {
      for(secondCollidableIndex <- firstCollidableIndex + 1 until collideables.length) {
        val (c1, c2) = (collideables(firstCollidableIndex), collideables(secondCollidableIndex))
        val (cb1, cb2) = (c1.collisionBox, c2.collisionBox)
        val (rec1, rec2) = (new Rectangle(cb1.x, cb1.y, cb1.getWidth, cb1.getWidth), new Rectangle(cb2.x, cb2.y, cb2.getWidth, cb2.getHeight))
        if(rec1.setPosition(rec1.x + c1.position.x, rec1.y + c1.position.y).overlaps(rec2.setPosition(rec2.x + c2.position.x, rec2.y + c2.position.y))) {
          if(c1.shouldCollideWith(c2)) {
            c1.onCollideWith(c2)
          }
          if(c2.shouldCollideWith(c1)) {
            c2.onCollideWith(c1)
          }
        }
      }
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
        monsterInstance.setLevel(difficultyScale / 4.0f)
        monsterSpawnBudget -= cost
        addGameObject(monsterInstance)
      }
    }
  }

  def startTeleporterEvent(): Unit = {
    val bosses = Array[CreatureEntity](
      new MageBossEntity,
      new SquidBossEntity,
      new ShadowBossEntity,
      new DemonBossEntity,
    )

    // Spawn boss
    val boss = bosses(Random.nextInt(bosses.length))
    boss.addItemToInventory(new RollbackHealthItem, 1)

    val newPos: Vector2 = new Vector2(1, 1).rotate(Random.between(0, 360)).nor().scl(Random.between(250, 300))
    boss.position = new Vector3(playerEntity.position.x, playerEntity.position.y, 0).add(new Vector3(newPos.x, newPos.y, 0))

    currentBoss = Some(boss)
    addGameObject(boss)

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

  def getCollideablesWithinCircle(circle: Circle, collisionMask: Int = Int.MaxValue): Array[Collideable] = {
    val collideables = gameObjects.filter(_.isInstanceOf[Collideable with Spatial]).map(_.asInstanceOf[Collideable with Spatial])
    val out: ArrayBuffer[Collideable] = new ArrayBuffer[Collideable]()
    for (col <- collideables) {
      val rec = new Rectangle(col.collisionBox.x + col.position.x, col.collisionBox.y + col.position.y, col.collisionBox.getWidth, col.collisionBox.getWidth)
      if((collisionMask & col.collisionLayer) != 0 && Intersector.overlaps(circle, rec))
        out.append(col)
    }
    out.toArray
  }
}
