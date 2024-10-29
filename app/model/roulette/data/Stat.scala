package model.roulette.data

case class Stat(number: Int, percent: Double) {
  override def toString: String = s"$number->${percent.toInt} times"
}
