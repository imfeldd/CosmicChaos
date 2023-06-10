package CosmicChaos.Entities

import CosmicChaos.Core.Interactable
import CosmicChaos.Core.Items.{Item, ItemRarity}
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture

import scala.util.Random

abstract class Chest extends Entity with Interactable {
  protected val closedTexture: Texture
  protected val openedTexture: Texture
  protected val textureScale: Float = 1.0f
  collisionLayer = CollisionLayers.props
  collisionMask = CollisionLayers.none

  var basePrice: Float
  var opened: Boolean = false
  val commonChance: Float
  val rareChance: Float
  val legendaryChance: Float

  override def onGraphicRender(g: GdxGraphics): Unit = {
    val tex = if(opened) openedTexture else closedTexture
    val (w, h) = (tex.getWidth*textureScale, tex.getHeight*textureScale)
    g.draw(tex, position.x - w/2, position.y - h/2, w, h)
  }

  override def interact(player: PlayerEntity): Unit = {
    if(player.cash < basePrice)
      return

    // Roll for item
    val roll = Random.nextFloat()
    val item =
      if(roll <= commonChance)
        Item.getRandomItemOfRarity(ItemRarity.common)
      else if(roll <= commonChance + rareChance)
        Item.getRandomItemOfRarity(ItemRarity.rare)
      else
        Item.getRandomItemOfRarity(ItemRarity.legendary)

    player.addItemToInventory(item, 1)
    player.experience += 5

    opened = true
    isInteractable = false
    player.cash -= basePrice
  }

  override def getInteractText: String = f"Open $name for $basePrice%.0f$$"
}
