package CosmicChaos.Screens

import ch.hevs.gdx2d.components.screen_management.RenderingScreen
import ch.hevs.gdx2d.lib.GdxGraphics

class MainMenuScreen extends RenderingScreen {
  override def onInit(): Unit = {}

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.drawStringCentered(g.getScreenHeight/2, "Cosmic Chaos")
  }

}
