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
import play.api.libs.json.{JsString, JsValue}

object TopperActor {
  val name = "rng-roulette-topper"
  val path = s"/usr/$name"

  def props(out: ActorRef,
            dealer: ActorRef,
            logActor: ActorRef,
            remoteAddress: String): Props
  = Props(new TopperActor(out, dealer, logActor, remoteAddress))

}

class TopperActor(out: ActorRef,
                  dealer: ActorRef,
                  logActor: ActorRef,
                  remoteAddress: String)
  extends Actor
  with ActorLogging  {

  import actors.roulette.AutoRouletteActor._

  private var clientIp = remoteAddress


  override def preStart(): Unit = {
    logActor ! Warning( file = "TopperActor", str = s"Topper Socket Flow Actor for ${clientIp} Started")
    super.preStart()
  }


  override def postStop(): Unit = {
    logActor ! Warning( file = "TopperActor", str = s"Topper Socket Flow Actor for ${clientIp} Stopped")

    if (clientIp != "") dealer ! TopperDisconnected(clientIp);
    super.postStop()
  }

  override def receive: Receive = {
    case clientMsg: JsString =>
    case clientMsg: JsValue =>
      clientMsg("MessageType") match {

        case JsString("INITIALIZE_TOPPER") => {
          log.info(s"Topper: INITIALIZE_TOPPER received from ${clientIp}!")
          dealer ! TopperConnected(clientIp, self, out)
        }

        case _ => log.warning(s"Unknown MessageType ${clientMsg("MessageType")} Received!")
      }
    case Reconnected =>
      log.info("Topper Reconnected Scenario ???!!")
      logActor ! Warning( file = "TopperActor", str = s"Topper Reconnected Scenario ???!!")
      clientIp = ""

    case _ => log.warning("Unknown Message Received!")
  }

}
