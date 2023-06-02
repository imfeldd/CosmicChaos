package CosmicChaos.HUD

import CosmicChaos.Core.Renderable
import CosmicChaos.Entities.PlayerEntity
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class DeathHUD(player: PlayerEntity) extends Renderable {
  val shapeRenderer = new ShapeRenderer()
  shapeRenderer.setAutoShapeType(true)
  val spriteBatch = new SpriteBatch()
  val bitmapFont = new BitmapFont()

  override def onGraphicRender(g: GdxGraphics): Unit = {
    shapeRenderer.begin()
    shapeRenderer.set(ShapeRenderer.ShapeType.Filled)

    // Background
    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(0, Gdx.graphics.getHeight / 2 - 20, Gdx.graphics.getWidth, 80)

    shapeRenderer.end()
    spriteBatch.begin()

    // Text
    bitmapFont.draw(spriteBatch, s"YOU DIED", 0, Gdx.graphics.getHeight / 2 + 42, Gdx.graphics.getWidth, 1, false)
    bitmapFont.draw(spriteBatch, s"KILLED BY ${player.deathCause.name.toUpperCase}", 0, Gdx.graphics.getHeight / 2 + 12, Gdx.graphics.getWidth, 1, false)

    spriteBatch.end()
  }
}
