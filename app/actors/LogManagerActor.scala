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
package actors


import akka.actor.{Actor, Props}
import dao.LogDao
import model.common.data.{Log, ServerLog}
import play.api.Logger

import java.text.SimpleDateFormat
import java.util.Calendar

object LogManagerActor {
  val name = "log-manager-actor"
  val path = s"/usr/${name}"


  case object GetAllLogs
  case class TablePreStarted(tableId: String, tableName: String)
  case class Info(tableId: String = "221002", file: String = "", str: String);
  case class Warning(tableId: String = "221002", file: String = "", str: String);
  case class Error(tableId: String = "221002", file: String = "", str: String);

  def props(logDao: LogDao): Props = Props(new LogManagerActor(logDao))
}

class LogManagerActor(logDao: LogDao) extends Actor {

  import actors.LogManagerActor._

  val log: Logger = Logger(this.getClass);
  val dateFormat =  new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss.SSS z")
  private var logs: Seq[String] = Seq.empty[String]

  override def preStart(): Unit = {
    super.preStart()
    log.logger.info(s"Log Manager preStart Called")
    logs = logDao.getLogs
  }

  override def postStop(): Unit = {
    logDao.setLogs(logs)
    super.postStop()
  }

  override def receive: Receive = {
    case Info(tableId, file, str ) =>
      val log = tableId match {
        case "221002" =>
          Log(logType = s"[roulette] [$tableId] [info]", file = file, str = str, timestamp = dateFormat.format(Calendar.getInstance().getTime))
        case "228000" =>
          Log(logType = s"[ab] [$tableId] [info]", file = file, str = str, timestamp = dateFormat.format(Calendar.getInstance().getTime))
        case "32100" =>
          Log(logType = s"[baccarat] [$tableId] [info]", file = file, str = str, timestamp = dateFormat.format(Calendar.getInstance().getTime))
      }

      logs = log.toString +: logs
      logDao.setLogs(logs)

    case Warning(tableId, file, str ) =>
      val log = tableId match {
        case "221002" =>
          Log(logType = s"[roulette] [$tableId] [warning]", file = file, str = str, timestamp = dateFormat.format(Calendar.getInstance().getTime))
        case "228000" =>
          Log(logType = s"[ab] [$tableId] [warning]", file = file, str = str, timestamp = dateFormat.format(Calendar.getInstance().getTime))
        case "32100" =>
          Log(logType = s"[baccarat] [$tableId] [warning]", file = file, str = str, timestamp = dateFormat.format(Calendar.getInstance().getTime))
      }

      logs = log.toString +: logs
      logDao.setLogs(logs)

    case Error(tableId, file, str ) =>
      val log = tableId match {
        case "221002" =>
          Log(logType = s"[roulette] [$tableId] [error]", file = file, str = str, timestamp = dateFormat.format(Calendar.getInstance().getTime))
        case "228000" =>
          Log(logType = s"[ab] [$tableId] [error]", file = file, str = str, timestamp = dateFormat.format(Calendar.getInstance().getTime))
        case "32100" =>
          Log(logType = s"[baccarat] [$tableId] [error]", file = file, str = str, timestamp = dateFormat.format(Calendar.getInstance().getTime))
      }

      logs = log.toString +: logs


    case GetAllLogs                             => sender() ! logs.take(200)

    case x => log.logger.info(s"Unknown message ${x}!")
  }


}

