package CosmicChaos.Core.Stats

import scala.language.implicitConversions

case class StatValue(baseValue: Float) {
  var multiplier: Float = 1.0f
  var baseAddition: Float = 0.0f
  var flatAddition: Float = 0.0f

  def value: Float = {
    (baseValue + baseAddition) * multiplier + flatAddition
  }
}

object StatValue {
  implicit def StatValueToFloat(s: StatValue): Float =
    s.value
}