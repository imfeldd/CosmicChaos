package CosmicChaos.Core.Items.Debuff

import CosmicChaos.Core.Items.{Item, ItemRarity}
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Core.Stats.{EntityStats}
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeType.Bitmap

class FlipMovement extends Item{

  override val name: String = "Flip Ring"
  override val description: String = "Your movement are now permanently compromised"
  override val icon: Texture = new BitmapImage("data/images/items/Inversor.png").getImage
  override val rarity: ItemRarity = ItemRarity.common

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.acceleration.multiplier *= -1.0f
  }
}
