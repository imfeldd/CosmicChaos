package CosmicChaos

trait GameObject  {

  val parentGameWorld: GameWorld = null

  def onInit

  def onUpdate(dt: Float)
}
