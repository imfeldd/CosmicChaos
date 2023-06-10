package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.BeforeDealDamageEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoreDamageSameEnemyItem extends Item with BeforeDealDamageEffect {
  override val name: String = "Wood Splinter"
  override val description: String = "Hitting the same enemy multiple time increases the damage dealt a little bit more each hit."
  override val icon: Texture = new BitmapImage("data/images/items/splinter.png").getImage
  override val rarity: ItemRarity = ItemRarity.legendary
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  private var currentTarget: CreatureEntity = _
  private var hitsCount: Int = 0

  override def beforeDealDamage(amountToDeal: Float, isCrit: Boolean, target: CreatureEntity): Float = {
    if(currentTarget != target) {
      currentTarget = target
      hitsCount = 0
      amountToDeal
    }
    else {
      hitsCount += 1
      amountToDeal * (1.0f + 0.2f*stackSize*hitsCount)
    }
  }
}
