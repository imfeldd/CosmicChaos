package CosmicChaos.Core.Items
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoreAccuracyItem extends Item{
  override val name: String = "Solid Cigarettes"
  override val description: String = "Increases your weapon accuracy by 15% (+10% per stack)."
  override val icon: Texture = new BitmapImage("data/images/items/cigarettes.png").getImage
  override val rarity: ItemRarity = ItemRarity.rare
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.attackAccuracy.flatAddition += 0.15f + 0.1f*(stackSize - 1)
  }
}
