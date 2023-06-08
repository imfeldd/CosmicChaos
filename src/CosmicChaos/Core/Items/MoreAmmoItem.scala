package CosmicChaos.Core.Items

import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoreAmmoItem extends Item {
  override val name: String = "Box of Bullets"
  override val description: String = "Increases your weapon's magazine capacity by 25% (+25% per stack)."
  override val icon: Texture = new BitmapImage("data/images/items/ammo.png").getImage
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.attackCapacity.flatAddition += 0.15f + 0.1f * (stackSize - 1)
  }
}
