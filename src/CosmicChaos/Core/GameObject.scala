package CosmicChaos.Core

import CosmicChaos.Core.World.GameWorld

trait GameObject  {

  var parentGameWorld: GameWorld = _

  def onEnterGameWorld(): Unit

  def onUpdate(dt: Float): Unit

  def onLeaveGameWorld(): Unit = { }
}
