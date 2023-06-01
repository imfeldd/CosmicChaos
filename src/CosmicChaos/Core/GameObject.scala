package CosmicChaos.Core

import CosmicChaos.Core.World.GameWorld

trait GameObject  {

  var parentGameWorld: GameWorld = null

  def onInit

  def onUpdate(dt: Float)

  def onLeaveGameWorld = { }
}
