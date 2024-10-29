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
package services

import play.api.routing.Router
import play.api.{ApplicationLoader, Configuration, Environment, Logger}
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc._

import scala.concurrent._

class ProdErrorHandler(
    environment: Environment,
    configuration: Configuration,
    devContext: Option[ApplicationLoader.DevContext] = None,
    router: => Option[Router] = None
) extends DefaultHttpErrorHandler(environment, configuration, devContext.map(_.sourceMapper), router) {

  private val log = Logger(this.getClass)

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    implicit val req = request
    Future.successful(Results.NotFound(views.html.errorPage()))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    log.error("Exception occurred", exception)
    implicit val req = request
    Future.successful(Results.InternalServerError(views.html.errorPage()))
  }

  override protected def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    super.onBadRequest(request, message)
  }
}
