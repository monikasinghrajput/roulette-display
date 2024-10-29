package model.roulette.data

case class TerminalData(connected: Boolean = false,
                        totalBet: Double = 0,
                        winAmount: Double = 0,
                        lastWin: Double = 0,
                        history: Seq[TerminalHistory] = Seq.empty[TerminalHistory],
                        betList: Seq[Bet] = Seq.empty[Bet],
                        winningBets: Seq[WinBet] = Seq.empty[WinBet],
                        balance: Double = 0.0)
