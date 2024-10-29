package model.roulette.data

import akka.actor.ActorRef

case class ClientData(actor:ActorRef = null ,
                      client:ActorRef = null,
                      uid : String = "-1",
                      playerIp: String = "192.168.0.1",
                      betList: Seq[Bet] = Seq.empty[Bet],
                      winningBets: Seq[WinBet] = Seq.empty[WinBet],
                      balance: Double = 0.0)
