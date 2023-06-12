package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.OnGetHitEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class FlatDamageReductionItem extends Item with OnGetHitEffect{
  override val name: String = "Umbrella"
  override val description: String = "Reduces all incoming damage by 3 (+3 per stack)."
  override val icon: Texture = new BitmapImage("data/images/items/umbrella.png").getImage
  override val rarity: ItemRarity = ItemRarity.common
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def gotHit(by: CreatureEntity, amount: Float, wasCrit: Boolean): Float = {
    math.max(1, amount - 3*stackSize)
  }
}
