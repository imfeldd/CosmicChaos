package CosmicChaos.Core.Items

import CosmicChaos.Core.Items.Effects.OnKillEffect
import CosmicChaos.Core.Items.ItemRarity.ItemRarity
import CosmicChaos.Entities.{CreatureEntity, Explosion}
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter
import com.badlogic.gdx.math.Vector3

import scala.util.Random

class ExplodeOnKillItem extends Item with OnKillEffect {
  override val name: String = "Chili Pepper"
  override val description: String = "Enemies have a chance to explode on death."
  override val icon: Texture = new BitmapImage("data/images/items/pepper.png").getImage
  override val rarity: ItemRarity = ItemRarity.rare
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def onKill(killed: CreatureEntity): Unit = {
    val chance = 0.2
    if (Random.nextFloat() <= chance) {
      val damage = holder.stats.damage * 8 * (0.5f + 0.5f*stackSize)
      val radius = 150 * (0.75f + 0.25f*stackSize)
      val explosion = new Explosion(damage, radius, holder)
      explosion.position = new Vector3(killed.position.x, killed.position.y, 0)
      holder.parentGameWorld.addGameObject(explosion)
    }
  }
}
