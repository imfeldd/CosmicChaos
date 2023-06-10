package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.OnKillEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoreMoneyOnKill extends Item with OnKillEffect {
  override val name: String = "Pocket Chance"
  override val description: String = "Gain 33% (+33% per stack) more money from slain enemies."
  override val icon: Texture = new BitmapImage("data/images/items/money.png").getImage
  override val rarity: ItemRarity = ItemRarity.rare
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def onKill(killed: CreatureEntity): Unit = {
    killed.cash *= (1.0f + 0.33f*stackSize)
  }
}
