package CosmicChaos.Entities

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Rectangle

class LegendaryChest extends Chest {
  override val name: String = "Legendary Chest"
  override val collisionBox: Rectangle = new Rectangle(-10, -10, 10, 10)
  override val closedTexture: Texture = new Texture("data/images/chests/chest_gold_closed.png")
  override val openedTexture: Texture = new Texture("data/images/chests/chest_gold_opened.png")
  override val textureScale: Float = 2.5f

  override var basePrice: Float = 120.0f * 8.0f
  override val commonChance: Float = 0.0f
  override val rareChance: Float = 0.0f
  override val legendaryChance: Float = 1.0f
}
