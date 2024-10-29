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
package controllers.roulette

import akka.actor.ActorSystem
import akka.stream.Materializer
import play.api.Logger
import play.api.libs.json.{JsError, JsPath, JsValue, Json, Reads}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import services.{GameService, RouletteSSeaterTableService}
import actors.roulette.clients.{AdminActor, ClientActor, TopperActor}
import actors.roulette.AutoRouletteActor.{BallInRim, NoMoreBets, PlaceYourBets, WinDetected}
import play.api.libs.functional.syntax._

import scala.util.{Failure, Success, Try}

case class WheelMsg(MessageType: String, result: String = "-1" )

class RouletteSSeaterController(components: ControllerComponents,
                                actorSystem: ActorSystem,
                                gameService: GameService,
                                rouletteSSeaterTableService: RouletteSSeaterTableService,
                                mat: Materializer)
  extends AbstractController(components) {

  val log = Logger(this.getClass)
  implicit val materializer = mat
  implicit val actorFactory = actorSystem


//  implicit val reads: Reads[WheelMsg] = Json.reads[WheelMsg]

  implicit val wheelMsgDecoder: Reads[WheelMsg] = (
  (JsPath \ "MessageType").read[String] and
   ((JsPath \ "result").read[String] or Reads.pure("-1"))
  ) (WheelMsg.apply _)


  def sendPlayerPage: Action[AnyContent] = Action { request =>
    log.logger.info(s"sending roulette player page for ${request.remoteAddress}")
    Ok(views.html.pages.roulette.player())
  }

  def sendTopperPage: Action[AnyContent] = Action { request =>
    log.logger.info(s"sending roulette topper page for ${request.remoteAddress}")
    Ok(views.html.pages.roulette.topper())
  }

  def sendVerticalTopperPage: Action[AnyContent] = Action { request =>
    log.info(s"sending roulette vertical topper page for ${request.remoteAddress}")
    Ok(views.html.pages.roulette.vtopper())

  }

  def sendAdminPage: Action[AnyContent] = Action { request =>
    log.logger.info(s"sending roulette admin page for ${request.remoteAddress}")
    Ok(views.html.pages.roulette.admin())
  }

  //Web Sockets


  def player: WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    val ip = request.getQueryString("ip").getOrElse(request.remoteAddress)

    log.logger.info(s"player socket request from ${ip}");

    ActorFlow.actorRef { out =>
      ClientActor.props(out, rouletteSSeaterTableService.getAutoRouletteActor, gameService.getLoggingActor, ip)
    }
  }


  def topper: WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    val ip = request.getQueryString("ip").getOrElse(request.remoteAddress)

    log.logger.info(s"topper socket request from ${ip}");

    ActorFlow.actorRef { out =>
      TopperActor.props(out, rouletteSSeaterTableService.getAutoRouletteActor, gameService.getLoggingActor, ip)
    }
  }

  def admin: WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    val ip = request.getQueryString("ip").getOrElse(request.remoteAddress)

    log.logger.info(s"admin socket request from ${ip}");

    ActorFlow.actorRef { out =>
      AdminActor.props(out, rouletteSSeaterTableService.getAutoRouletteActor, gameService.getLoggingActor, ip)
    }
  }


  //API Services for Roulette Stadium Seater Table

  def sendInitialDataJson: Action[AnyContent] = Action { request =>

    val limitId = request.getQueryString("limit_id").getOrElse("758359")
    val tableId = request.getQueryString("table_id").getOrElse("221002")
    val operatorId = request.getQueryString("operatorId").getOrElse("964")

    Ok(rouletteSSeaterTableService.getInitialDataJsonString)
  }

  def sendAuthenticateJson: Action[AnyContent] = Action { request =>
    val tableId = request.getQueryString("table_id").getOrElse("228000")

    Ok(rouletteSSeaterTableService.authenticateJsonString)
  }

  def sendStreamsJson: Action[AnyContent] = Action { request =>
    val tableId = request.getQueryString("table_id").getOrElse("228000")

    Ok(rouletteSSeaterTableService.sendStreamsJsonString)
  }

  def handleWheelMsg = Action(parse.json) { request =>
    log.info(s"${request.body} received")

    val wheelMsg = request.body.validate[WheelMsg]
    wheelMsg.fold(
      errors => {
        BadRequest(Json.obj("message" -> JsError.toJson(errors)))
      },
      msg => {
        msg.MessageType match {
          case "Opened" => {
            log.info(s"Received Place Your Bets")
            rouletteSSeaterTableService.getAutoRouletteActor ! PlaceYourBets
            Ok(Json.obj("message" -> ("Received Place Your Bets.")))
          }
          case "Loaded" => {
            log.info(s"Received Ball In Rim")
            rouletteSSeaterTableService.getAutoRouletteActor ! BallInRim
            Ok(Json.obj("message" -> ("Received No  Ball In Rim.")))
          }
          case "Locked" => {
            log.info(s"Received No More Bets")
            rouletteSSeaterTableService.getAutoRouletteActor ! NoMoreBets
            Ok(Json.obj("message" -> ("Received No More Bets.")))
          }
          case "Closed" => {
            log.info(s"Received Win Number ${msg.result.toInt}")
            rouletteSSeaterTableService.getAutoRouletteActor ! WinDetected(msg.result.toInt)
            Ok(Json.obj("message" -> (s"Received Win Number ${msg.result.toInt} Successfully.")))
          }
          case _ => {
            log.info(s"Received ${msg.MessageType}")
            Ok(Json.obj("message" -> ("Unknown Message Successfully.")))
          }
        }

      }
    )
  }

  def sendLogsJson: Action[AnyContent] = Action { request =>
    val countStr: String = request.getQueryString("count").getOrElse("100")
    val filterStr: String = request.getQueryString("filter").getOrElse("none")

    val count = Try(countStr.toInt) match {
      case Success(count) => count
      case Failure(ex) => 100
    }

    Ok(rouletteSSeaterTableService.sendLogsJsonString(count = count, filter = filterStr))
  }

}
