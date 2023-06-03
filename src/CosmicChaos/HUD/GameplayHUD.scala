package CosmicChaos.HUD

import CosmicChaos.Entities.PlayerEntity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class GameplayHUD(player: PlayerEntity) {
  val shapeRenderer = new ShapeRenderer()
  shapeRenderer.setAutoShapeType(true)
  val spriteBatch = new SpriteBatch()
  val bitmapFont = new BitmapFont()

  def onGraphicRender(): Unit = {
    shapeRenderer.begin()
    shapeRenderer.set(ShapeRenderer.ShapeType.Filled)

    // Healthbar background
    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(30, 30, 350, 40)

    // Healthbar foreground
    shapeRenderer.setColor(Color.GREEN)
    shapeRenderer.rect(30, 30, 350 * math.max(0, player.currentHealth/player.stats.maxHealth), 40)

    shapeRenderer.end()
    spriteBatch.begin()

    // Healthbar counter
    bitmapFont.draw(spriteBatch, s"${player.currentHealth.toInt}/${player.stats.maxHealth.toInt}", 30, 55, 350, 1, false)

    spriteBatch.end()
  }
}

