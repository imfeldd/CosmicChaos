package CosmicChaos.Core

import ch.hevs.gdx2d.lib.GdxGraphics

trait Renderable {
  var renderLayer: Int = 0
  def onGraphicRender(g: GdxGraphics): Unit
}
