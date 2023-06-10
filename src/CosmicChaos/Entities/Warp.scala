package CosmicChaos.Entities

import CosmicChaos.Core.Interactable
import CosmicChaos.Core.World.CellularAutomata
import ch.hevs.gdx2d.components.audio.MusicPlayer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.{Vector2, Vector3}

import scala.util.Random

abstract class Warp extends Entity with Interactable {

  protected def getTexture: Texture
  protected val textureScale: Float
  collisionLayer = CollisionLayers.props
  collisionMask = CollisionLayers.none

  private def findTeleportPosition(world: CellularAutomata): Vector2 = {
    // Logic to find a valid teleport position in the new world
    // Example: Randomly selecting a position
    val randomX = Random.nextInt(world.width)
    val randomY = Random.nextInt(world.height)
    new Vector2(randomX, randomY)
  }

  override def interact(player: PlayerEntity): Unit = {
    parentGameWorld.MyAlgo.worldCreation()
    val teleportPosition = findTeleportPosition(parentGameWorld.MyAlgo) // Find a valid teleport position in the new world
    println(teleportPosition)
    player.position = new Vector3(teleportPosition.x, teleportPosition.y, 0)
    val soundEffect = new MusicPlayer("data/music/warp.wav")
    soundEffect.play()
  }

  override def getInteractText: String
}
