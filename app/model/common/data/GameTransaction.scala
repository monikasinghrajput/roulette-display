package model.common.data

case class GameTransaction(roundId: Long,
                           rake: Double = 0.0,
                           game: String = "Roulette",
                           transType: String = "Undefined",
                           player: String,
                           totalBet: Double = 0,
                           totalWin: Double = 0,
                           oldBalance: Double = 0,
                           balance: Double = 0)
