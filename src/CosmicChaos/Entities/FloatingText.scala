package CosmicChaos.Entities

import CosmicChaos.Core.{GameObject, Renderable, Spatial}
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2

class FloatingText(text: String, lifeTime: Float, velocity: Vector2, font: BitmapFont = null) extends GameObject with Renderable with Spatial {

  private var timeLeft: Float = lifeTime

  renderLayer = 9

  override def onUpdate(dt: Float): Unit = {
    super.onUpdate(dt)

    timeLeft -= dt

    if(timeLeft <= 0.0f) {
      parentGameWorld.removeGameObject(this)
      return
    }

    position.add(velocity.x * dt, velocity.y * dt, 0)
  }

  override def onGraphicRender(g: GdxGraphics): Unit = {
    if(font == null)
      g.drawString(position.x, position.y, text)
    else
      g.drawString(position.x, position.y, text, font)
  }
}
