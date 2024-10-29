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

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import play.api.libs.json.{JsString, JsValue}
import model.roulette.data.{ConfigData, Win}
import model.roulette.RouletteJsonCodecs


object AdminActor {
  val name = "roulette-stadium-seater-admin-actor"
  val path = s"/usr/$name"


  def props(out: ActorRef, //ui client reference
            dealer: ActorRef, //table actor
            logActor: ActorRef, //log actor
            remoteAddress: String //ip address as a string?
  ): Props = Props(new AdminActor(out = out, dealer = dealer, logActor = logActor,remoteAddress = remoteAddress))
}

class AdminActor(out: ActorRef, dealer: ActorRef, logActor: ActorRef, remoteAddress: String)
  extends Actor
    with RouletteJsonCodecs
    with ActorLogging {

  import actors.roulette.AutoRouletteActor._

  private val clientIp = remoteAddress

  override def preStart(): Unit = super.preStart()

  override def postStop(): Unit = {
    if (clientIp != "") dealer ! AdminDisConnected(clientIp)
    super.postStop()
  }

  override def receive: Receive = {
    case pingMsg: JsString =>
    case clientMsg: JsValue =>
      clientMsg("MessageType") match {
        case JsString("INITIALIZE_ADMIN") =>
//          log.info(s"INITIALIZE_ADMIN received from $clientIp!")
          dealer ! AdminConnected(clientIp, self, out)


        case JsString("CONFIG_UPDATE") =>
          val configData = clientMsg("configData").validate[ConfigData].fold(
            invalid = { fieldErrors =>
              log.info(s"CONFIG_UPDATE Decoding failed..")
              fieldErrors.foreach { x =>
                log.info(s"field: ${x._1}, errors: ${x._2}")
              }
              ConfigData()
            },
            valid = { data =>
              log.info(s"CONFIG_UPDATE Decoding Success..")
              data
            }
          )
          log.info(s"Config Data Decoded => ${configData.toString}")
          if (configData.tableName != "EMPTY") dealer ! ConfigUpdateCommand(configData)

        case JsString("PLACE_YOUR_BETS_COMMAND") =>
          dealer ! PlaceYourBets
        case JsString("NO_MORE_BETS_COMMAND") =>
          dealer ! NoMoreBets

        case JsString("GAME_RESULT") =>
          log.info(s"GAME_RESULT received ${clientMsg("winResult")}")
          val winResult = clientMsg("winResult").validate[Win].fold(
            invalid = { fieldErrors =>
              fieldErrors.foreach { x =>
                log.info(s"field: ${x._1}, errors: ${x._2}")
              }
              Win(-1,-1)
            },
            valid = { data =>
              data
            }
          )
          if (winResult.roundId != -1) dealer ! WinDetected(winResult.winningNUmber)

        case _ => log.info(s"Unknown MessageType ${clientMsg("MessageType")} Received!")
      }


    case _ => log.info("Unknown Message Received!")
  }


}
