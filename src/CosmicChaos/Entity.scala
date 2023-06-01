package CosmicChaos
import ch.hevs.gdx2d.lib.GdxGraphics

abstract class Entity extends GameObject with Renderable with Spatial {
  val baseStats: Stats
  var stats: Stats

  override def onInit = {}
  override def onUpdate(dt: Float): Unit = {}
  override def onGraphicRender(g: GdxGraphics): Unit = {}
}
