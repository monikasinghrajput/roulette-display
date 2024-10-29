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
* |  22/09/2023   | Wilson Sam   |     Created     |  First Milestone
* **********************************************************************************************************************************************************************
* */
package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import controllers.Assets.Asset
import play.api.Logger
import play.api.mvc._
import services.GameService


import play.api.libs.streams.ActorFlow
import actors.clients.CageClientActor
import play.api.libs.json.{JsError, JsValue, Json, Reads}

class AppController(components: ControllerComponents,
                    assets: Assets,
                    actorSystem: ActorSystem,
                    gameService: GameService,
                    mat: Materializer)
  extends AbstractController(components)  {

  val log = Logger(this.getClass)
  implicit val materializer = mat
  implicit val actorFactory = actorSystem

  /*
  * serving html5 pages /front-end interfaces
  * - cage
  * - lobby
  * */

  def sendCageClientPage: Action[AnyContent] = Action { request =>
    //    log.logger.info(s"sending Cage/Banking page for ${request.remoteAddress}")
    Ok(views.html.pages.cage())
  }

  def sendLobbyPage: Action[AnyContent] = Action { request =>
    Ok(views.html.pages.lobby())
  }

  /*
  * serving websocket requests with a 2 way Json Interface
  *  - cage
  * */

  def cage: WebSocket = WebSocket.accept[JsValue, JsValue] { request =>
    //    log.logger.info(s"Controller Serving cage socket request from ${request.remoteAddress}");

    ActorFlow.actorRef { out =>
      CageClientActor.props(out, gameService.getMainActor, request.remoteAddress)
    }
  }


  def error500(): Action[AnyContent] = Action {
    InternalServerError(views.html.errorPage())
  }

  def versioned(path: String, file: Asset) = assets.versioned(path, file)
}
