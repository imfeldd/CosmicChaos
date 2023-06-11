package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.OnKillEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Core.Stats.EntityStats
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class SpeedBoostOnKillItem extends Item with OnKillEffect{
  override val name: String = "Scuttling Prawn"
  override val description: String = "Gain a temporary speed boost on killing an enemy."
  override val icon: Texture = new BitmapImage("data/images/items/prawn.png").getImage
  override val rarity: ItemRarity = ItemRarity.common
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  private var remainingBoostCounter: Float = 0.0f

  override def update(dt: Float): Unit = {
    super.update(dt)
    remainingBoostCounter -= dt
  }

  override def modify(entityStats: EntityStats): Unit = {
    super.modify(entityStats)

    if(remainingBoostCounter > 0.0f)
      entityStats.maxSpeed.multiplier = 1.0f + 0.2f * stackSize
  }

  override def onKill(killed: CreatureEntity): Unit = {
    remainingBoostCounter = 0.33f  // non stacking, flat boost of a third of a second
  }
}
