package CosmicChaos.Screens

import CosmicChaos.Core.Renderable
import CosmicChaos.Core.World.GameWorld
import CosmicChaos.Entities.{ImmortalSnailEnemy, PlayerEntity}
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector3

import scala.util.Random

class GameScreen extends RenderingScreen {
  val gameWorld: GameWorld = new GameWorld
  val player: PlayerEntity = new PlayerEntity{team = 1}

  var cameraShake: Float = 0.5f

  override def onInit(): Unit = {
    val testEnemy = new ImmortalSnailEnemy{team = 2}
    testEnemy.position = new Vector3(100, 100, 0)

    gameWorld.addGameObject(player)
    gameWorld.addGameObject(testEnemy)
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
    g.clear()

    doCameraShake(g)

    val shapeRenderer = new ShapeRenderer()
    shapeRenderer.setAutoShapeType(true)
    shapeRenderer.begin()
    shapeRenderer.circle(10, 10, 10)
    shapeRenderer.end()

    // We cast to array to prevent exception if the ArrayBuffer is mutated during the loop. TODO: Maybe find a better way to do this?
    for(gameObject <- gameWorld.gameObjects.toArray) {
      gameObject.onUpdate( 1/60.0f )

      gameObject match {
        case r: Renderable => r.onGraphicRender(g)
        case _ =>
      }
    }
  }

  private def doCameraShake(g: GdxGraphics): Unit= {
    def randFromMin1To1: Float = Random.between(-1.0f, 2.0f)

    val shake = math.pow(math.min(1, cameraShake), 2)
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
