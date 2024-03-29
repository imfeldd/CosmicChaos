package CosmicChaos.Core.Items
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoreCritsItem extends Item {
  override val name: String = "Walkman"
  override val description: String = "Gain 10% (+10% per stack) chance to deal 'Critical Strike', dealing double damage."
  override val icon: Texture = new BitmapImage("data/images/items/walkman.png").getImage
  override val rarity: ItemRarity = ItemRarity.common
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.criticalChance.flatAddition += 0.1f * stackSize
  }
}
