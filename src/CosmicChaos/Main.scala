package CosmicChaos

import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.{GdxGraphics, ScreenManager}


object Main extends PortableApplication( 1920, 1080) {

  val s: ScreenManager = new ScreenManager

  def main(args: Array[String]): Unit = {
    println("Hello world!")
  }

  override def onInit(): Unit = {
    s.registerScreen(classOf[Screens.GameScreen])
    s.transitionTo(0, ScreenManager.TransactionType.SLICE)
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    s.render(g)
  }

  override def onKeyDown(keycode: Int): Unit = {
    super.onKeyDown(keycode)
    if(s.getActiveScreen != null)
      s.getActiveScreen.onKeyDown(keycode)
  }

  override def onKeyUp(keycode: Int): Unit = {
    super.onKeyUp(keycode)
    if(s.getActiveScreen != null)
      s.getActiveScreen.onKeyUp(keycode)
  }
}