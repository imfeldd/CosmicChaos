package CosmicChaos.Core.Items

import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoreAttackSpeedItem extends Item {
  override val name: String = "Soldier's Syringe"
  override val description: String = "Increases attack speed by 15% (+15% by stack)."
  override val icon: Texture = new BitmapImage("data/images/items/syringe.png").getImage
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.attackSpeed.flatAddition += 0.15f * stackSize
  }
}
