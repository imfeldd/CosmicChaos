package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.DealtDamageEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Core.Stats.EntityStats
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class HealOnCritItem extends Item with DealtDamageEffect {
  override val name: String = "Good Pair of Headphones"
  override val description: String = "Gain 10% 'Critical Strike' chance. Each time you deal a 'Critical Chance', you get healed for 5 (+3 per stack) health points."
  override val icon: Texture = new BitmapImage("data/images/items/headphones.png").getImage
  override val rarity: ItemRarity = ItemRarity.rare
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.criticalChance.flatAddition += 0.1f
  }

  override def dealtDamage(amountDealt: Float, wasCrit: Boolean, target: CreatureEntity): Unit = {
    if(!wasCrit)
      return

    holder.heal(5 + 3*(stackSize - 1))
  }
}
