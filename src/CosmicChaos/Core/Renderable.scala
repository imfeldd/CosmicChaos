package CosmicChaos.Core

import ch.hevs.gdx2d.lib.GdxGraphics

trait Renderable extends Spatial {
  var renderLayer: Int = 0
  def onGraphicRender(g: GdxGraphics): Unit
}
