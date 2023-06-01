package CosmicChaos.HUD

import CosmicChaos.Core.Renderable
import CosmicChaos.Entities.PlayerEntity
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.jogamp.opengl.util.awt.TextRenderer

class PlayerHUD(player: PlayerEntity) extends Renderable {
  val shapeRenderer = new ShapeRenderer()
  shapeRenderer.setAutoShapeType(true)
  val spriteBatch = new SpriteBatch()
  val bitmapFont = new BitmapFont()

  override def onGraphicRender(g: GdxGraphics): Unit = {
    shapeRenderer.begin()
    shapeRenderer.set(ShapeRenderer.ShapeType.Filled)

    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(30, 30, 350, 40)

    shapeRenderer.setColor(Color.GREEN)
    shapeRenderer.rect(30, 30, 350 * math.max(0, player.currentHealth/player.stats.maxHealth), 40)

    shapeRenderer.end()

    spriteBatch.begin()
    bitmapFont.draw(spriteBatch, s"${player.currentHealth.toInt}/${player.stats.maxHealth.toInt}", 30, 55, 350, 1, false)
    spriteBatch.end()
  }
}
