package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoreHealthRegenItem extends Item {
  override val name: String = "Weird Mushroom"
  override val description: String = s"Increases your health regeneration by 50% (+25% by stack)."
  override val icon: Texture = new BitmapImage("data/images/items/shroom.png").getImage
  override val rarity: ItemRarity = ItemRarity.common
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.healthRegenerationAmount.multiplier += 0.5f * 0.25f * (stackSize - 1)
  }
}
