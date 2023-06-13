package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoreAmmoItem extends Item {
  override val name: String = "Box of Bullets"
  override val description: String = "Increases your weapon's magazine capacity by 25% (+25% per stack)."
  override val icon: Texture = new BitmapImage("data/images/items/ammo.png").getImage
  override val rarity: ItemRarity = ItemRarity.rare
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.attackCapacity.multiplier += 0.25f*stackSize
  }
}
