package model.roulette.data

case class TerminalHistory(roundId: Int = 0,
                           lastBalance: Double = 0,
                           betList: Seq[Bet] = Seq.empty[Bet],
                           winningBets: Seq[WinBet] = Seq.empty[WinBet],
                           totalBet: Double = 0,
                           winAmount: Double = 0,
                           currentBalance: Double = 0.0)
