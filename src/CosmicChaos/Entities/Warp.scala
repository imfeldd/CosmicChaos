package CosmicChaos.Entities

import CosmicChaos.Core.Interactable
import CosmicChaos.Core.World.CellularAutomata
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2

import scala.util.Random

abstract class Warp extends Entity with Interactable {

  protected def getTexture: Texture
  protected val textureScale: Float
  override val collisionLayer: Int = CollisionLayers.props
  override val collisionMask: Int = CollisionLayers.none

  private def findTeleportPosition(world: CellularAutomata): Vector2 = {
    // Logic to find a valid teleport position in the new world
    // Example: Randomly selecting a position
    val randomX = Random.nextInt(world.width)
    val randomY = Random.nextInt(world.height)
    new Vector2(randomX * world.tileSize, randomY * world.tileSize)
  }


  override def interact(player: PlayerEntity): Unit = {
    val newWorld = new CellularAutomata(800, 600, seed = 1234)
    newWorld.worldCreation()
    val teleportPosition = findTeleportPosition(newWorld) // Find a valid teleport position in the new world
    player.setPosition(teleportPosition.x, teleportPosition.y)
  }

  override def getInteractText: String
}
