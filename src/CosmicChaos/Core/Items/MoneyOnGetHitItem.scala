package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.OnGetHitEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoneyOnGetHitItem extends Item with OnGetHitEffect {
  override val name: String = "Blood Money"
  override val description: String = "Gain money on taking damage."
  override val icon: Texture = new BitmapImage("data/images/items/gold.png").getImage
  override val rarity: ItemRarity = ItemRarity.common
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def gotHit(by: CreatureEntity, amount: Float, wasCrit: Boolean): Unit = {
    if(by == holder)
      return

    holder.cash += amount/25.0f * (1 + 0.33f*(stackSize - 1))
  }
}
