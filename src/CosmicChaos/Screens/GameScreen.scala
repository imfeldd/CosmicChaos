package CosmicChaos.Screens

import CosmicChaos.Core.World.GameWorld
import CosmicChaos.Entities._
import CosmicChaos.HUD.{DeathHUD, GameplayHUD}
import CosmicChaos.Screens.GameScreen.cameraShake
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap

import scala.util.Random

class GameScreen extends RenderingScreen {
  val gameWorld: GameWorld = new GameWorld
  def player: PlayerEntity = gameWorld.playerEntity
  var gameplayHud: GameplayHUD = _
  var deathHud: DeathHUD = _
  var gameTimer: Float = 0.0f
  val crosshair = new Pixmap(Gdx.files.internal("data/images/crosshair.png"))

  override def onInit(): Unit = {
    Gdx.graphics.setCursor(Gdx.graphics.newCursor(crosshair, crosshair.getWidth/2, crosshair.getHeight/2))

    gameWorld.initializeWorld()
    gameplayHud = new GameplayHUD(gameWorld.playerEntity, this)
    deathHud = new DeathHUD(gameWorld.playerEntity)
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

    gameTimer += Gdx.graphics.getDeltaTime

    gameWorld.update(Gdx.graphics.getDeltaTime)

    gameWorld.draw(g)

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
