/*
* *********************************************************************************************************************************************************************
* Copyright (c) 2017 Tykhe Gaming Private Limited - All Rights Reserved

Licensed under the Software License Agreement (the "License") of Tykhe Gaming Private Limited.
You may not use this file except in compliance with the License.
You may obtain a copy of the License at http://tykhegaming.github.io/LICENSE.txt.

NOTICE
ALL INFORMATION CONTAINED HEREIN IS, AND REMAINS THE PROPERTY OF TYKHE GAMING PRIVATE LIMITED.
THE INTELLECTUAL AND TECHNICAL CONCEPTS CONTAINED HEREIN ARE PROPRIETARY TO TYKHE GAMING PRIVATE LIMITED AND ARE PROTECTED BY TRADE SECRET OR COPYRIGHT LAW.
DISSEMINATION OF THIS INFORMATION OR REPRODUCTION OF THIS MATERIAL IS STRICTLY FORBIDDEN UNLESS PRIOR WRITTEN PERMISSION IS OBTAINED FROM TYKHE GAMING PRIVATE LIMITED.

* **********************************************************************************************************************************************************************
* Change History
* **********************************************************************************************************************************************************************
* |     Date      |     Name     |      Change     |      Details
* |  01/08/2022   | Wilson Sam   |     Created     |  First Milestone
* |  10/10/2023   | Wilson Sam   |     Version     |  Packaged For Demo
* **********************************************************************************************************************************************************************
* */
package actors.roulette.clients

import actors.LogManagerActor.Warning
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import model.roulette.data._
import model.roulette.RouletteJsonCodecs
import play.api.libs.json._

object ClientActor {
  val name = "rng-roulette-client"
  val path = "/usr/$name"

  def props(out: ActorRef,
            dealer: ActorRef,
            logActor: ActorRef,
            remoteAddress: String): Props =
    Props(new ClientActor(out, dealer, logActor, remoteAddress))


}

class ClientActor(out: ActorRef, dealer: ActorRef, logActor: ActorRef, remoteAddress: String)
  extends Actor with ActorLogging with RouletteJsonCodecs {

  import actors.roulette.AutoRouletteActor._

  private var clientIp = remoteAddress

  override def preStart(): Unit = {
    logActor ! Warning( file = "ClientActor",  str =  s"Client Socket Flow Actor for ${clientIp} Started")
    log.info(s"player client created ${clientIp}")
    super.preStart()
  }


  override def postStop(): Unit = {
    logActor ! Warning( file = "ClientActor",  str =  s"Client Socket Flow Actor for ${clientIp} Stopped")
    if (clientIp != "") dealer ! PlayerDisConnected(clientIp);
    super.postStop()
  }

  override def receive: Receive = {
    case clientMsg: JsString =>
    case clientMsg: JsValue =>
      clientMsg("MessageType") match {
        case JsString("INITIALIZE_PLAYER") => {
          log.info(s"PlayerConnected Send from the client Actor $clientIp")
          dealer ! PlayerConnected(clientIp, self, out)
        }

        case JsString("PlaceBet") => {
          val betList = clientMsg("BetsList").as[Seq[Bet]]
          dealer ! PlayerBetPlaced(clientIp, betList, out)
        }
        case _ => log.info(s"Unknown MessageType ${clientMsg("MessageType")} Received!")
      }
    case Reconnected =>
      log.info("Client Reconnected Scenario ???!!")
      logActor ! Warning( file = "ClientActor",  str =  s"Client Reconnected Scenario ???!!")
      clientIp = ""
    case _ => log.info("Unknown Message Received!")
  }
}
