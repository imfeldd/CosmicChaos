package CosmicChaos.Core.Items
import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter

class MoreDamageItem extends Item {
  override val name: String = "Boxing Glove"
  override val description: String = s"Deal an additional 50% (+25% per stack) damage to all enemies."
  override val icon: Texture = new BitmapImage("data/images/items/boxingGlove.png").getImage
  icon.setFilter(TextureFilter.Nearest, TextureFilter.Nearest)

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.damage.multiplier += 0.5f + 0.25f*(stackSize - 1)
  }
}
