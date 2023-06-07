package CosmicChaos.Core.Items

import CosmicChaos.Core.Stats.EntityStats
import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import com.badlogic.gdx.graphics.Texture

class MoreSpeedItem extends Item {
  override val name: String = "Pair of Schlaps"

  override val description: String = s"Increases your maximum running speed by 25 (+20 by stack)."
  override val icon: Texture = new BitmapImage("data/images/items/shlaps.png").getImage

  override def modify(entityStats: EntityStats): Unit = {
    entityStats.maxSpeed.flatAddition += 25 + 20 * (stackSize - 1)
  }
}
