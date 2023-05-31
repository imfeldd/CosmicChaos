import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.lib.GdxGraphics

object Main extends PortableApplication {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
  }

  override def onInit(): Unit = {

  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    g.clear()

    g.drawStringCentered(100, "Welcome to gdx2d !")
    g.drawFPS()
  }
}