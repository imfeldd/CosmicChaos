package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.OnKillEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Core.Stats.EntityStats
import CosmicChaos.Entities.CreatureEntity
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

import scala.util.Random

class ItemOnKillItem extends Item with OnKillEffect{
  override val name: String = "5 Leaf Clover"
  override val description: String = "Gain a small chance for slain enemies to drop a random item."
  override val icon: Texture = new BitmapImage("data/images/items/clover.png").getImage
  override val rarity: ItemRarity = ItemRarity.legendary
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.criticalChance.flatAddition += 0.1f
  }

  override def onKill(killed: CreatureEntity): Unit = {
    val chance = 0.005 * stackSize
    if(Random.nextFloat() <= chance) {
      val item = holder.itemsInventory(Random.nextInt(holder.itemsInventory.length))
      holder.addItemToInventory(item, 1)
    }
  }
}
