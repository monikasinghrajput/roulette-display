package model.roulette.message

import model.roulette.data.WinBet

import java.time.Instant

case class WonBetsMessage(MessageType: String = "WonBets",
                          destination: String = "player",
                          clientId: String = "",
                          roundTripStartTime: Long = Instant.now.getEpochSecond,
                          winningBets: Seq[WinBet] = Seq.empty[WinBet])
