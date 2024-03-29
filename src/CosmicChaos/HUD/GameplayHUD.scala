package CosmicChaos.HUD

import CosmicChaos.Core.Items.{Item, ItemRarity}
import CosmicChaos.Core.World.TeleporterEventState
import CosmicChaos.Entities.PlayerEntity
import CosmicChaos.HUD.GameplayHUD.{whiteFont, _}
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

  private var newItemToDisplay: Option[Item] = None
  private var newItemTimer: Float = 0.0f

  def onGraphicRender(): Unit = {
    whiteFont.getData.setScale(0.75f)

    if(newItemToDisplay.nonEmpty) {
      newItemTimer -= Gdx.graphics.getDeltaTime
      if(newItemTimer <= 0) {
        newItemToDisplay = None
      }
    }

    if(newItemToDisplay.isEmpty && newItems.nonEmpty) {
      newItemToDisplay = Some(newItems.dequeue())
      newItemTimer = 0.33f * newItemToDisplay.get.description.split(" ").length  // ~ 180 wpm = 3 words per second
    }

    val (w, h) = (Gdx.graphics.getWidth, Gdx.graphics.getHeight)

    shapeRenderer.begin()
    shapeRenderer.set(ShapeRenderer.ShapeType.Filled)

    // Game timer background
    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(w - 180, h - 70, 150, 40)

    // Player money background
    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(w - 180, h - 120, 150, 40)

    // Current objective background
    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(w - 280, h - 170, 250, 40)

    // Healthbar background
    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(30, 30, 350, 40)

    // Healthbar foreground
    shapeRenderer.setColor(new Color(0.0f, 0.7f, 0.0f, 1.0f))
    shapeRenderer.rect(30, 30, 350 * math.max(0, player.currentHealth/player.stats.maxHealth), 40)

    // Level background
    shapeRenderer.setColor(Color.DARK_GRAY)
    shapeRenderer.rect(30, 85, 350, 30)

    // Level foreground
    val levelPercent = (player.level - math.floor(player.level)) / 1.0f
    shapeRenderer.setColor(new Color(0.3f, 0.2f, 0.8f, 1.0f))
    shapeRenderer.rect(30, 85, (350 * levelPercent).toInt, 30)

    // New Item notification background
    if(newItemToDisplay.nonEmpty) {
      val i = newItemToDisplay.get
      val gl = new GlyphLayout()
      gl.setText(whiteFont, i.description)
      val lines = math.max(2, math.ceil(gl.width / 350 + 0.05f))

      // Text Background
      shapeRenderer.setColor(Color.DARK_GRAY)
      shapeRenderer.rect(w/2 - 64 - 20 - 8, 120 - 23*lines.toInt, 350 + 64 + 20*2, 65 + 23*lines.toInt)

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

    // Boss healthbar background
    if (player.parentGameWorld.currentBoss.nonEmpty) {
      val boss = player.parentGameWorld.currentBoss.get
      val healthPercent = boss.currentHealth/boss.stats.maxHealth

      // Healthbar background
      shapeRenderer.setColor(Color.DARK_GRAY)
      shapeRenderer.rect(w/2 - 350/2, h - 35, 350, 33)

      // Healthbar foreground
      shapeRenderer.setColor(new Color(0.7f, 0.1f, 0.1f, 1.0f))
      shapeRenderer.rect(w/2 - 350/2, h - 35, 350 * healthPercent, 33)
    }

    shapeRenderer.end()
    spriteBatch.begin()

    // Player's inventory
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
        whiteFont.draw(spriteBatch, s"x${item.stackSize}", posX, posY)
    }

    // Healthbar counter
    whiteFont.draw(spriteBatch, s"${player.currentHealth.toInt}/${player.stats.maxHealth.toInt}", 30, 55, 350, 1, false)

    // Timer text
    val totalMillis = (gameScreen.gameTimer* 1000).toInt
    val minutes = totalMillis / (60 * 1000)
    val seconds = (totalMillis % (60 * 1000)) / 1000
    val milliseconds = totalMillis % 1000
    whiteFont.draw(spriteBatch, f"$minutes%02d:$seconds%02d.$milliseconds%03d", w - 180, h - 45, 150, 1, false)

    // Player money text
    greenFont.getData.setScale(0.8f)
    greenFont.draw(spriteBatch, f"${player.cash.toInt}$$", w - 180, h - 95, 150, 1, false)

    // Objective text
    val objective = player.parentGameWorld.teleporterEventState match {
      case TeleporterEventState.notStarted => "Find the teleporter"
      case TeleporterEventState.charging => s"Charging (${(player.parentGameWorld.teleporter.chargePercent*100).toInt}%)"
      case TeleporterEventState.charged => "Go trough the teleporter"
    }
    whiteFont.draw(spriteBatch, objective, w - 280, h - 145, 250, 1, false)

    // Interaction text
    if(player.interactableOfInterest.isDefined) {
      val text = "[E] "+ player.interactableOfInterest.get.getInteractText
      whiteFont.draw(spriteBatch, text, w/2, 30, 100, 1, false)
    }

    // New Item notification
    if(newItemToDisplay.nonEmpty) {
      val i = newItemToDisplay.get
      val titleFont = i.rarity match {
        case ItemRarity.common => whiteFont
        case ItemRarity.rare => yellowFont
        case ItemRarity.legendary => redFont
      }
      titleFont.getData.setScale(1f)
      titleFont.draw(spriteBatch, i.name, w/2, 170, 350, 1, false)
      whiteFont.getData.setScale(0.75f)
      whiteFont.draw(spriteBatch, i.description, w/2, 130, 350, 1, true)
      spriteBatch.draw(i.icon, w/2 - 64 - 20, 100, 64, 64)
    }

    // Player level text
    whiteFont.draw(spriteBatch, f"lvl. ${player.level.toInt}", 30, 105, 350, 1, false)

    // Player ammo counter
    val ammoStr = if(player.weapon.isMagazineEmpty) "RELOADING" else s"${player.weapon.currentAmmoCount}/${player.weapon.ammoCapacity}"
    whiteFont.draw(spriteBatch, ammoStr,  Gdx.input.getX + 24, Gdx.graphics.getHeight - Gdx.input.getY)

    // Boss health text
    if(player.parentGameWorld.currentBoss.nonEmpty) {
      val boss = player.parentGameWorld.currentBoss.get
      whiteFont.draw(spriteBatch, s"${boss.currentHealth.toInt} / ${boss.stats.maxHealth.value.toInt}", w/2 - 350/2, h - 10, 350, 1, false)
      whiteFont.getData.setScale(2.0f)
      whiteFont.draw(spriteBatch, boss.name, w/2 - 350/2, h - 50, 350, 1, false)
      whiteFont.getData.setScale(1.0f)
      whiteFont.draw(spriteBatch, s"- ${boss.nameSubtitle} -", w / 2 - 350 / 2, h - 105, 350, 1, false)
    }

    spriteBatch.end()
  }
}

object GameplayHUD {
  val newItems = new mutable.Queue[Item]()
  val whiteFont = new BitmapFont(Gdx.files.internal("data/fonts/vga_white.fnt"), Gdx.files.internal("data/fonts/vga_white.png"), false, true)
  val redFont = new BitmapFont(Gdx.files.internal("data/fonts/vga_red.fnt"), Gdx.files.internal("data/fonts/vga_red.png"), false, true)
  val yellowFont = new BitmapFont(Gdx.files.internal("data/fonts/vga_yellow.fnt"), Gdx.files.internal("data/fonts/vga_yellow.png"), false, true)
  val greenFont = new BitmapFont(Gdx.files.internal("data/fonts/vga_green.fnt"), Gdx.files.internal("data/fonts/vga_green.png"), false, true)

  def showItemNotification(item: Item): Unit = {
    newItems.enqueue(item)
  }
}

