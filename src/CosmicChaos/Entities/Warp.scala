package CosmicChaos.Entities

import CosmicChaos.Core.Interactable
import com.badlogic.gdx.graphics.Texture

abstract class Warp extends Entity with Interactable {
  protected def getTexture: Texture
  protected val textureScale: Float
  collisionLayer = CollisionLayers.props
  collisionMask = CollisionLayers.none
}
