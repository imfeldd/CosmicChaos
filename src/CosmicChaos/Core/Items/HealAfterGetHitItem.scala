package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.OnGetHitEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class HealAfterGetHitItem extends Item with OnGetHitEffect {
  override val name: String = "First Aid Kit"
  override val description: String = "Heal for 8 (+5 per stack) health points 2 seconds after getting hit."
  override val icon: Texture = new BitmapImage("data/images/items/healthpack.png").getImage
  override val rarity: ItemRarity = ItemRarity.common
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  private var healTimer: Float = 0.0f
  private var gotHit: Boolean = false

  override def update(dt: Float): Unit = {
    if(gotHit) {
      healTimer -= dt
      if(healTimer <= 0.0f) {
        gotHit = false
        holder.heal(8 + 5*(stackSize - 1))
      }
    }
  }

  override def gotHit(by: CreatureEntity, amount: Float, wasCrit: Boolean): Unit = {
    gotHit = true
    healTimer = 2.0f
  }
}
