package CosmicChaos.Core

import CosmicChaos.Core.World.GameWorld

trait GameObject  {

  var parentGameWorld: GameWorld = null

  def onEnterGameWorld

  def onUpdate(dt: Float)

  def onLeaveGameWorld = { }
}
