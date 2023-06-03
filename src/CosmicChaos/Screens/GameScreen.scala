package CosmicChaos.Screens

import CosmicChaos.Core.{Renderable, Spatial}
import CosmicChaos.Core.World.GameWorld
import CosmicChaos.Entities.{GunnerEnemyEntity, ImmortalSnailEnemy, PlayerEntity}
import CosmicChaos.HUD.{DeathHUD, GameplayHUD}
import CosmicChaos.Screens.GameScreen.cameraShake
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3

import scala.util.Random

class GameScreen extends RenderingScreen {
  val gameWorld: GameWorld = new GameWorld
  val player: PlayerEntity = new PlayerEntity{team = 1}
  val gameplayHud: GameplayHUD = new GameplayHUD(player)
  val deathHud: DeathHUD = new DeathHUD(player)

  override def onInit(): Unit = {
    // Temporary testing code
    val testEnemy = new ImmortalSnailEnemy{team = 2}
    testEnemy.position = new Vector3(100, 100, 0)
    val testGunner = new GunnerEnemyEntity{team=2}
    testGunner.position = new Vector3(-100, 100, 0)

    gameWorld.addGameObject(player)
    gameWorld.addGameObject(testEnemy)
    gameWorld.addGameObject(testGunner)
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
    val maxAngle = 10
    val maxOffset = 10

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
