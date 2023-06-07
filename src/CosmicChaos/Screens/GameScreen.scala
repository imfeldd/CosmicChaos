package CosmicChaos.Screens

import CosmicChaos.Core.World.GameWorld
import CosmicChaos.Core.{Collideable, Renderable, Spatial}
import CosmicChaos.Entities.Enemies.{FirstBossEntity, FlyingAlienEnemyEntity, ImmortalSnailEnemyEntity}
import CosmicChaos.Entities.{NormalChest, PlayerEntity, Teleporter}
import CosmicChaos.HUD.{DeathHUD, GameplayHUD}
import CosmicChaos.Screens.GameScreen.cameraShake
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.{Rectangle, Vector3}

import scala.util.Random

class GameScreen extends RenderingScreen {
  val gameWorld: GameWorld = new GameWorld
  val player: PlayerEntity = new PlayerEntity{team = 1}
  val gameplayHud: GameplayHUD = new GameplayHUD(player, this)
  val deathHud: DeathHUD = new DeathHUD(player)
  var gameTimer: Float = 0.0f
  val seed = 1234L

  override def onInit(): Unit = {
    // Temporary testing code
    val testEnemy = new ImmortalSnailEnemyEntity{team = 2}
    testEnemy.position = new Vector3(100, 100, 0)
    val testGunner = new FlyingAlienEnemyEntity{team=2}
    testGunner.position = new Vector3(-100, 100, 0)
    val testGunner2 = new FlyingAlienEnemyEntity{team = 2}
    testGunner2.position = new Vector3(-120, 170, 0)
    val testGunner3 = new FlyingAlienEnemyEntity{team = 2}
    testGunner3.position = new Vector3(-150, 200, 0)

    val chest = new NormalChest
    val teleporter = new Teleporter
    val magicMage = new FirstBossEntity

    gameWorld.addGameObject(player)
    gameWorld.addGameObject(testEnemy)
    gameWorld.addGameObject(testGunner)
    gameWorld.addGameObject(testGunner2)
    gameWorld.addGameObject(testGunner3)
    gameWorld.addGameObject(chest)
    gameWorld.addGameObject(teleporter)
    gameWorld.addGameObject(magicMage)
  }

  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)

    player.onKeyDown(keycode)
  }

  override def onKeyUp(keycode: Int): Unit = {
    super.onKeyUp(keycode)

    player.onKeyUp(keycode)
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.begin()
    g.clear()
    doCameraShake(g)
    gameWorld.MyAlgo.draw(g)

    //g.getCamera.zoom = 9

    gameTimer += Gdx.graphics.getDeltaTime

    // TODO: Move all this shit to GameWorld
    val collideables = gameWorld.gameObjects.filter(_.isInstanceOf[Collideable with Spatial]).map(_.asInstanceOf[Collideable with Spatial])
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

    for(gameObject <- gameWorld.gameObjects.toArray) {
      gameObject.onUpdate(Gdx.graphics.getDeltaTime)
    }

    val renderablesWithSpatial = gameWorld.gameObjects.filter(_.isInstanceOf[Renderable with Spatial]).map(_.asInstanceOf[Renderable with Spatial])
    for (renderable <- renderablesWithSpatial.sortBy(x => (x.renderLayer, -x.position.y))) {  // Sort by render layer descending, then by y pos ascending
      renderable.onGraphicRender(g)
    }

    g.end()

    // Keep the hud stuff after we're done with GdxGraphics since we're using our own Batches
    if(player.isDead) {
      deathHud.onGraphicRender()
    }
    else {
      gameplayHud.onGraphicRender()
    }
  }

  private def doCameraShake(g: GdxGraphics): Unit= {
    def randFromMin1To1: Float = Random.between(-1.0f, 2.0f)

    val shake = math.pow(math.min(1, cameraShake), 4)
    val maxAngle = 20
    val maxOffset = 20

    val angle = maxAngle * shake * randFromMin1To1
    val offsetX = maxOffset * shake * randFromMin1To1
    val offsetY = maxOffset * shake * randFromMin1To1

    player.centerCameraOnPlayer(g)
    g.getCamera.rotate(angle.toFloat)
    g.getCamera.translate(offsetX.toFloat, offsetY.toFloat)
    g.getCamera.update()

    cameraShake *= 0.9f
  }
}

object GameScreen {
  var cameraShake: Float = 0.0f
}
