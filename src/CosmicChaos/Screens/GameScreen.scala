package CosmicChaos.Screens

import CosmicChaos.{GameWorld, PlayerEntity, Renderable}
import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx

class GameScreen extends RenderingScreen {
  val gameWorld: GameWorld = new GameWorld
  val player: PlayerEntity = new PlayerEntity

  override def onInit(): Unit = {
    gameWorld.gameObjects.addOne(player)
    Gdx.input.
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

    for(gameObject <- gameWorld.gameObjects) {
      gameObject.onUpdate( 1/60.0f )

      gameObject match {
        case r: Renderable => r.onGraphicRender(g)
      }
    }
  }
}
