package CosmicChaos.HUD

import CosmicChaos.Entities.PlayerEntity
import CosmicChaos.Screens.GameScreen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class GameplayHUD(player: PlayerEntity, gameScreen: GameScreen) {
  val shapeRenderer = new ShapeRenderer()
  shapeRenderer.setAutoShapeType(true)
  val spriteBatch = new SpriteBatch()
  val bitmapFont = new BitmapFont()

  def onGraphicRender(): Unit = {
    val (w, h) = (Gdx.graphics.getWidth, Gdx.graphics.getHeight)

    shapeRenderer.begin()
    shapeRenderer.set(ShapeRenderer.ShapeType.Filled)

    // Game timer background
    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(w - 180, h - 70, 150, 40)

    // Player money background
    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(30, 80, 150, 40)

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

    // Timer text
    val totalMillis = (gameScreen.gameTimer* 1000).toInt
    val minutes = totalMillis / (60 * 1000)
    val seconds = (totalMillis % (60 * 1000)) / 1000
    val milliseconds = totalMillis % 1000
    bitmapFont.draw(spriteBatch, f"$minutes%02d:$seconds%02d.$milliseconds%03d", w - 180, h - 45, 150, 1, false)

    // Interaction text
    if(player.interactableOfInterest.isDefined) {
      val text = "[E] "+ player.interactableOfInterest.get.getInteractText
      bitmapFont.draw(spriteBatch, text, w/2, 30, 100, 1, false)
    }

    // Player money text
    bitmapFont.draw(spriteBatch, f"${player.cash}$$", 30, 105, 150, 1, false)

    spriteBatch.end()
  }
}

