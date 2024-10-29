package model.roulette.message

import java.time.Instant

case class NoMoreBetsMessage(MessageType: String = "NO_MORE_BETS",
                             tableId: String = "",
                             destination: String = "player",
                             clientId: String = "",
                             roundId: Long = 0,
                             gameType: String = "AutomatedRoulette",
                             roundTripStartTime: Long = Instant.now.getEpochSecond,
                             timestamp: String = "")
