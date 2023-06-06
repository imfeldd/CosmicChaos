package CosmicChaos.Core

import CosmicChaos.Entities.{Entity, PlayerEntity}

trait Interactable extends Entity {
  var isInteractable: Boolean = true

  def interact(player: PlayerEntity): Unit
  def getInteractText: String
}
