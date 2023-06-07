package CosmicChaos.Core.Items

import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture

class MoreHealthItem extends Item {
  override val name: String = "Roast Chicken"
  override val description: String = s"Increases your maximum health by 50 (+50 by stack)."
  override val icon: Texture = new BitmapImage("data/images/items/chicken.png").getImage

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.maxHealth.flatAddition += 50 * stackSize
  }
}
