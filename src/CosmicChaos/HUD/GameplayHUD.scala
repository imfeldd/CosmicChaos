package CosmicChaos.HUD

import CosmicChaos.Core.Items.{Item, ItemRarity}
import CosmicChaos.Entities.PlayerEntity
import CosmicChaos.HUD.GameplayHUD.newItems
import CosmicChaos.Screens.GameScreen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.{BitmapFont, GlyphLayout, SpriteBatch}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2

import scala.collection.mutable


class GameplayHUD(player: PlayerEntity, gameScreen: GameScreen) {
  val shapeRenderer = new ShapeRenderer()
  shapeRenderer.setAutoShapeType(true)
  val spriteBatch = new SpriteBatch()
  val bitmapFont = new BitmapFont()

  private var newItemToDisplay: Option[Item] = None
  private var newItemTimer: Float = 0.0f

  def onGraphicRender(): Unit = {

    if(newItemToDisplay.nonEmpty) {
      newItemTimer -= Gdx.graphics.getDeltaTime
      if(newItemTimer <= 0) {
        newItemToDisplay = None
      }
    }

    if(newItemToDisplay.isEmpty && newItems.nonEmpty) {
      newItemToDisplay = Some(newItems.dequeue())
      newItemTimer = 3.0f
    }

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

    // New Item notification background
    if(newItemToDisplay.nonEmpty) {
      val i = newItemToDisplay.get
      val gl = new GlyphLayout()
      gl.setText(bitmapFont, i.description)
      val lines = math.max(2, math.ceil(gl.width / 350 + 0.01f))

      // Text Background
      shapeRenderer.setColor(Color.DARK_GRAY)
      shapeRenderer.rect(w/2 - 64 - 20 - 8, 150 - 34*lines.toInt, 350 + 64 + 20*2, 40 + 34*lines.toInt)

      // Icon Background
      shapeRenderer.setColor(i.rarity match {
        case ItemRarity.common => Color.WHITE
        case ItemRarity.rare => Color.YELLOW
        case ItemRarity.legendary => Color.RED
      })
      shapeRenderer.set(ShapeRenderer.ShapeType.Line)
      shapeRenderer.rect(w/2 - 64 - 20, 100 ,64, 64)
      shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
    }

    shapeRenderer.end()
    spriteBatch.begin()

    val itemsPerCol = 8
    val iconSize = 64
    val iconMargin = 16
    val basePos = new Vector2(30, 150)
    for(itemIdx <- player.itemsInventory.indices) {
      val item = player.itemsInventory(itemIdx)
      val col = itemIdx / itemsPerCol
      val row = itemIdx % itemsPerCol
      val (posX, posY) = (basePos.x + col * (iconSize + iconMargin), basePos.y + row * (iconSize + iconMargin))

      // Draw the item icon
      spriteBatch.draw(
        item.icon,
        posX,
        posY,
        iconSize, iconSize
      )

      // Draw the item stack count
      if(item.stackSize != 0)
        bitmapFont.draw(spriteBatch, s"x${item.stackSize}", posX, posY)
    }

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

    // New Item notification
    if(newItemToDisplay.nonEmpty) {
      val i = newItemToDisplay.get
      bitmapFont.draw(spriteBatch, i.name, w/2, 170, 350, 1, false)
      bitmapFont.draw(spriteBatch, i.description, w/2, 130, 350, 1, true)
      spriteBatch.draw(i.icon, w/2 - 64 - 20, 100, 64, 64)
    }

    // Player money text
    bitmapFont.draw(spriteBatch, f"${player.cash}$$", 30, 105, 150, 1, false)

    spriteBatch.end()
  }
}

object GameplayHUD {
  val newItems = new mutable.Queue[Item]()

  def showItemNotification(item: Item): Unit = {
    newItems.enqueue(item)
  }
}

