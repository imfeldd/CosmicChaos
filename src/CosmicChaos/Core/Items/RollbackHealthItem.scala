package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.OnGetHitEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

import scala.collection.mutable

class RollbackHealthItem extends Item with OnGetHitEffect {
  override val name: String = "Marco's Watch"
  override val description: String = "Whenever your health gets too low, gain back the health you had a moment ago. Needs to recharge after use."
  override val icon: Texture = new BitmapImage("data/images/items/watch.png").getImage
  override val rarity: ItemRarity = ItemRarity.legendary
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  private var healthValues: mutable.Queue[Float] = new mutable.Queue[Float]
  private var chargeTimer: Float = 0.0f

  override def update(dt: Float): Unit = {
    chargeTimer -= dt

    healthValues.enqueue(holder.currentHealth)
    if(healthValues.length >= 600) {  // hold approx. 10 seconds of health history
      healthValues.dequeue()
    }
  }

  override def gotHit(by: CreatureEntity, amount: Float, wasCrit: Boolean): Unit = {
    if(holder.currentHealth <= holder.stats.maxHealth*0.1f && chargeTimer <= 0.0f) {
      chargeTimer = 10.0f - math.min(1.5f*stackSize, 4.5f)
      holder.heal(healthValues.max - holder.currentHealth)  // set health to highest recent value
    }
  }
}
