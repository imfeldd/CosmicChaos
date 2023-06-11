package CosmicChaos.Core.Items
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

import scala.util.Random

class FreeItemsItem extends Item  {
  override val name: String = "Corrupted Diskette"
  override val description: String = "Get a random common or rare item every minute and a half."
  override val icon: Texture = new BitmapImage("data/images/items/diskette.png").getImage
  override val rarity: ItemRarity = ItemRarity.legendary
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  private val frequency: Float = 90.0f
  private var timer: Float = frequency

  override def update(dt: Float): Unit = {
    super.update(dt)

    timer -= dt

    if(timer <= 0) {
      timer = frequency - 5.0f*math.log10(stackSize + 1).toFloat
      val rarity = Random.nextFloat() match {
        case x if x <= 0.8f => ItemRarity.common
        case _              => ItemRarity.rare
      }
      holder.addItemToInventory(Item.getRandomItemOfRarity(rarity))
    }
  }
}
