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
package actors.roulette

//Standard Packages
import actors.LogManagerActor.{Error, Info, Warning}
import actors.MainActor.{GuestConnectRequest, PlayerStatusOffline, PlayerStatusOnline}
import akka.actor.{Actor, ActorRef, Props, Stash, Timers}
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.duration._
import scala.language.postfixOps
import services.{GameService, RouletteSSeaterTableService}
import model.common.data.LicenseData

import java.text.SimpleDateFormat
import scala.collection.mutable.{Map => MMap}

//Application Data Packages
import actors.MainActor
import model.common.data.AdminClientData
import model.roulette.data._
import model.roulette.RouletteJsonCodecs

object AutoRouletteActor {
  val name = "auto-roulette-actor"
  val path = s"/usr/$name"

  case class AdminConnected(name: String, actor: ActorRef, client: ActorRef)
  case class AdminDisConnected(name: String)
  case class PlayerConnected(name: String, actor: ActorRef, client: ActorRef)
  case class PlayerDisConnected(name: String)
  case class TopperConnected(name: String, actor: ActorRef, client: ActorRef)
  case class TopperDisconnected(name: String)
  case object Reconnected

  case class PlayerBetPlaced(name: String, betList: Seq[Bet], client: ActorRef)



  case object PlaceYourBets
  case object BallInRim
  case object NoMoreBets
  case object NoWinDetected
  case object SaveGameData
  case class ConfigUpdateCommand(configData: ConfigData)
  case class WinDetected(number: Int)

  case object OpenTimerKey
  case object TimerKey
  case object PollTick
  case object Open

  def props( tableId: String,
             tableName: String,
             limitId: Int, 
             gameService: GameService,
             tableService: RouletteSSeaterTableService
           ): Props = Props(new AutoRouletteActor(tableId, tableName, limitId, gameService, tableService))


}

class AutoRouletteActor(tableId: String,
                        tableName: String,
                        limitId: Int,
                        gameService: GameService,
                        tableService: RouletteSSeaterTableService)
    extends RouletteTable(tableId = tableId, tableName = tableName, limitId = limitId, gameService = gameService, tableService = tableService) 
      with Actor
      with Stash
      with Timers
      with RouletteJsonCodecs {

  import AutoRouletteActor._
  import MainActor._


  override def preStart(): Unit = {
    super.preStart()
    timers.startSingleTimer(OpenTimerKey, Open, timeout = 1 second)
    timers.startTimerWithFixedDelay(TimerKey, PollTick, 1 second)

    logActor ! Warning(file = "AutoRouletteActor", str = s"Roulette Table $tableId started")
    log.info(s"Roulette Table $tableId started")

  }

  override def postStop(): Unit = {
    logActor ! Error(file = "AutoRouletteActor", str = s"Roulette Table $tableId Stopped")
    log.info(s"Roulette Table $tableId Stopped")
    super.postStop()
  }

  def getRoundId(game: String = "17"): Long = {
    val gameId = game.toInt
    val week = java.time.LocalDateTime.now().getDayOfYear
    val hour = java.time.LocalDateTime.now().getHour
    val minute = java.time.LocalDateTime.now().getMinute
    val second = java.time.LocalDateTime.now().getSecond / 10

    String.format("%02d%03d%02d%02d%1d", gameId, week, hour, minute, second).toLong
  }

  override def receive: Receive = TABLE_STATE_0_CLOSED(state =
    TableState(
      roundId = tableService.getGameData(tableId).roundId,
      gameData = tableService.getGameData(tableId).data,
      wheelData = WheelData(),
      configData = ConfigData(),
      licenseData = LicenseData(),
    )
  )


  /** ******************************** TABLE_STATE_0_CLOSED   ********************************************* */
  private def TABLE_STATE_0_CLOSED(state: TableState): Receive = {
    case Open =>
      log.info(s"Open Received, Moving Table to IDLE state")
      logActor ! Info(file = "AutoRouletteActor", str = s"Open TABLE_STATE_0_CLOSED => TABLE_STATE_1_IDLE")
      logActor ! Info(file = "AutoRouletteActor", str = s"CONFIG: ${Json.stringify(Json.toJson(state.configData))}")
      context become TABLE_STATE_1_IDLE(state.copy())
    case PollTick =>
    case message =>
      log.info(s"Stashing $message because I can't handle it in the CLOSED state")

  }

  /** ******************************** TABLE_STATE_1_IDLE   ********************************************* */
  
  private def TABLE_STATE_1_IDLE(state: TableState): Receive = {
    //From clients
    case AdminConnected(name, actor, client)            => context become TABLE_STATE_1_IDLE(state = handleAdminConnected(tableState = state, adminIp = name, actor = actor, client = client))
    case AdminDisConnected(name)                        => context become TABLE_STATE_1_IDLE(state = handleAdminDisconnected(tableState = state, adminIp = name))
    case PlayerConnected(name, actor, client)           => context become TABLE_STATE_1_IDLE(state = handlePlayerConnected(tableState = state, playerIp = name, actor = actor, client = client, tableActorRef = self))
    case PlayerDisConnected(name)                       => context become TABLE_STATE_1_IDLE(state = handlePlayerDisconnected(tableState = state, playerIp = name))
    case TopperConnected(name, actor, client)           => context become TABLE_STATE_1_IDLE(state = handleTopperConnected(tableState = state, topperIp = name, actor = actor, client = client))
    case TopperDisconnected(name)                       => context become TABLE_STATE_1_IDLE(state = handleTopperDisconnected(tableState = state, topperIp = name))

    case GuestConnectAccepted(ip, actor, client)        => context become TABLE_STATE_1_IDLE(state = handleGuestConnectAccepted(tableState = state, playerIp = ip, actor = actor, client = client))
    case PlayerBalanceUpdated(name, balance)            => context become TABLE_STATE_1_IDLE(state = handleBalanceUpdate(tableState = state, uid = name, newBalance = balance ))



    case PlaceYourBets | BallInRim                      =>
      val newRoundId = getRoundId()
      state.sendPlaceYourBetsMsgToAllClients()
      logActor ! Info(file = "AutoRouletteActor", str = s"PlaceYourBets | BallInRim TABLE_STATE_1_IDLE => STATE_PLACE_YOUR_BETS Round=${newRoundId}")
      context become STATE_PLACE_YOUR_BETS(state = state.copy(roundId = newRoundId))


    case PollTick => if (state.configData.autoPlay) {
      context become TABLE_STATE_1_IDLE(state = state.copy(autoPlayCounter = state.autoPlayCounter + 1))
      if (state.autoPlayCounter == 5) {
        val newRoundId = getRoundId()

        state.sendPlaceYourBetsMsgToAllClients()
        logActor ! Info(file = "AutoRouletteActor", str = s"PollTick ${state.autoPlayCounter} TABLE_STATE_1_IDLE => STATE_PLACE_YOUR_BETS Round=$newRoundId")
        context become STATE_PLACE_YOUR_BETS(state = state.copy(autoPlayCounter = 0, roundId = newRoundId))
      }
    }

    case _ => log.logger.warn("Message Not Handled@TABLE_STATE_1_IDLE state")
  }

  /** ******************************** TABLE_STATE_2_PLACE_YOUR_BETS   ********************************************* */
  private def STATE_PLACE_YOUR_BETS(state: TableState): Receive = {
    //From clients
    case AdminConnected(name, actor, client)          => context become STATE_PLACE_YOUR_BETS(state = handleAdminConnected(tableState = state, adminIp = name, actor = actor, client = client))
    case AdminDisConnected(name)                      => context become STATE_PLACE_YOUR_BETS(state = handleAdminDisconnected(tableState = state, adminIp = name))
    case PlayerConnected(name, actor, client)         => context become STATE_PLACE_YOUR_BETS(state = handlePlayerConnected(tableState = state, playerIp = name, actor = actor, client = client, tableActorRef = self))
    case PlayerDisConnected(name)                     => context become STATE_PLACE_YOUR_BETS(state = handlePlayerDisconnected(tableState = state, playerIp = name))
    case TopperConnected(name, actor, client)         => context become STATE_PLACE_YOUR_BETS(state = handleTopperConnected(tableState = state, topperIp = name, actor = actor, client = client))
    case TopperDisconnected(name)                     => context become STATE_PLACE_YOUR_BETS(state = handleTopperDisconnected(tableState = state, topperIp = name))

    case GuestConnectAccepted(ip, actor, client)      => context become STATE_PLACE_YOUR_BETS(state = handleGuestConnectAccepted(tableState = state, playerIp = ip, actor = actor, client = client))
    case PlayerBalanceUpdated(name, balance)          => context become STATE_PLACE_YOUR_BETS(state = handleBalanceUpdate(tableState = state, uid = name, newBalance = balance))

    case NoMoreBets =>
      state.sendNoMoreBetsMsgToAllClients()
      logActor ! Info(file = "AutoRouletteActor", str = s"NoMoreBets STATE_PLACE_YOUR_BETS => STATE_NO_MORE_BETS")
      context become STATE_NO_MORE_BETS(state = state)


    case PollTick  => if(state.configData.autoPlay) {
      context become STATE_PLACE_YOUR_BETS(state = state.copy(autoPlayCounter = state.autoPlayCounter + 1))
      if (state.autoPlayCounter == 30) {
        state.sendNoMoreBetsMsgToAllClients()
        logActor ! Info(file = "AutoRouletteActor", str = s"PollTick ${state.autoPlayCounter} STATE_PLACE_YOUR_BETS => STATE_NO_MORE_BETS")
        context become STATE_NO_MORE_BETS(state = state.copy(autoPlayCounter = 0))
      }
    }




    case _ => log.logger.warn("Message Not Handled@STATE_PLACE_YOUR_BETS state")
  }

  /** ******************************** TABLE_STATE_3_NO_MORE_BETS   ********************************************* */
  private def STATE_NO_MORE_BETS(state: TableState): Receive = {
    //From clients
    case AdminConnected(name, actor, client)          => context become STATE_NO_MORE_BETS(state = handleAdminConnected(tableState = state, adminIp = name, actor = actor, client = client))
    case AdminDisConnected(name)                      => context become STATE_NO_MORE_BETS(state = handleAdminDisconnected(tableState = state, adminIp = name))
    case PlayerConnected(name, actor, client)         => context become STATE_NO_MORE_BETS(state = handlePlayerConnected(tableState = state, playerIp = name, actor = actor, client = client, tableActorRef = self))
    case PlayerDisConnected(name)                     => context become STATE_NO_MORE_BETS(state = handlePlayerDisconnected(tableState = state, playerIp = name))
    case TopperConnected(name, actor, client)         => context become STATE_NO_MORE_BETS(state = handleTopperConnected(tableState = state, topperIp = name, actor = actor, client = client))
    case TopperDisconnected(name)                     => context become STATE_NO_MORE_BETS(state = handleTopperDisconnected(tableState = state, topperIp = name))

    case GuestConnectAccepted(ip, actor, client)      => context become STATE_NO_MORE_BETS(state = handleGuestConnectAccepted(tableState = state, playerIp = ip, actor = actor, client = client))
    case PlayerBalanceUpdated(name, balance)          => context become STATE_NO_MORE_BETS(state = handleBalanceUpdate(tableState = state, uid = name, newBalance = balance))

    case PlayerBetPlaced(name, betList, client)       => context become STATE_NO_MORE_BETS(state = handlePlayerBetPlaced(tableState = state, playerIp = name, betList = betList, mainActor = gameService.getMainActor))

    case PollTick  => if(state.configData.autoPlay) {
      context become STATE_NO_MORE_BETS(state = state.copy(autoPlayCounter = state.autoPlayCounter + 1))
      if (state.autoPlayCounter == 10) {
        val winResult = getWheelResult()
        val newHistory = (state.gameData.history :+ Win(winResult, state.roundId)).takeRight(100)
        val sortedHistory = newHistory
          .groupBy(win => win.winningNUmber)
          .map(t => (t._1, t._2.length))
          .toSeq.sortBy(_._2)
        val sortedHistoryByNumber: Seq[(Int, Int)] = newHistory
          .groupBy(win => win.winningNUmber)
          .map(t => (t._1, t._2.length))
          .toSeq.sortBy(_._1)

        val seedHistory: Seq[(Int, Int)] = (0 to 36).map(n => (n: Int, 0))

        val newColdNumbers = sortedHistory.concat(seedHistory).distinctBy(_._1).sortBy(_._2).take(5).map(x => x._1).reverse
        val newHotNumbers = sortedHistory.concat(seedHistory).distinctBy(_._1).sortBy(_._2).takeRight(5).map(x => x._1).reverse
        val newStatistics = sortedHistoryByNumber.map(x => Stat(x._1, x._2))
        val newGroupData = computeGroupData(sortedHistoryByNumber)
        val newGameData = RouletteGameData(
          newGroupData,
          newColdNumbers,
          state.gameData.lastWinners,
          newHotNumbers,
          newHistory,
          newStatistics,
        )

        val updatedState = state.copy(
          gameData = newGameData
        )
        self ! SaveGameData

        logActor ! Info(file = "AutoRouletteActor", str = s"WinDetected STATE_NO_MORE_BETS => STATE_GAME_RESULT $winResult")
        logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT HISTORY(${newHistory.size}): $newHistory")
        logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT COLD: $newColdNumbers")
        logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT HOT: $newHotNumbers")
        logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT STATS(${newStatistics.size}): ${newStatistics.sortBy(x => x.percent)}")
        logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT RED-BLACK: ${newGroupData.GroupRed}%-${newGroupData.GroupBlack}%")
        logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT EVEN-ODD: ${newGroupData.GroupEven}%-${newGroupData.GroupOdd}%")
        logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT HALF1-HALF2: ${newGroupData.Group1to18}%-${newGroupData.Group19to36}%")

        updatedState.sendGameResultToAllClients(winResult = winResult, mainActor = gameService.getMainActor, tableId = tableId, self = self)
        context become STATE_GAME_RESULT(state = updatedState.copy(autoPlayCounter = 0))
      }
    }

    case WinDetected(number) =>
      val winResult = number
      val newHistory = (state.gameData.history :+ Win(winResult, state.roundId)).takeRight(100)
      val sortedHistory = newHistory
        .groupBy(win => win.winningNUmber)
        .map(t => (t._1, t._2.length))
        .toSeq.sortBy(_._2)
      val sortedHistoryByNumber: Seq[(Int, Int)] = newHistory
        .groupBy(win => win.winningNUmber)
        .map(t => (t._1, t._2.length))
        .toSeq.sortBy(_._1)

      val seedHistory: Seq[(Int, Int)] = (0 to 36).map(n => (n: Int, 0))

      val newColdNumbers = sortedHistory.concat(seedHistory).distinctBy(_._1).sortBy(_._2).take(5).map(x => x._1).reverse
      val newHotNumbers = sortedHistory.concat(seedHistory).distinctBy(_._1).sortBy(_._2).takeRight(5).map(x => x._1).reverse
      val newStatistics = sortedHistoryByNumber.map(x => Stat(x._1, x._2))
      val newGroupData = computeGroupData(sortedHistoryByNumber)
      val newGameData = RouletteGameData(
        newGroupData,
        newColdNumbers,
        state.gameData.lastWinners,
        newHotNumbers,
        newHistory,
        newStatistics,
      )

      val updatedState = state.copy(
        gameData = newGameData
      )
      self ! SaveGameData

      logActor ! Info(file = "AutoRouletteActor", str = s"WinDetected STATE_NO_MORE_BETS => STATE_GAME_RESULT $winResult")
      logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT HISTORY(${newHistory.size}): $newHistory")
      logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT COLD: $newColdNumbers")
      logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT HOT: $newHotNumbers")
      logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT STATS(${newStatistics.size}): ${newStatistics.sortBy(x => x.percent)}")
      logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT RED-BLACK: ${newGroupData.GroupRed}%-${newGroupData.GroupBlack}%")
      logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT EVEN-ODD: ${newGroupData.GroupEven}%-${newGroupData.GroupOdd}%")
      logActor ! Info(file = "AutoRouletteActor", str = s"STATE_GAME_RESULT HALF1-HALF2: ${newGroupData.Group1to18}%-${newGroupData.Group19to36}%")

      updatedState.sendGameResultToAllClients(winResult = winResult, mainActor = gameService.getMainActor, tableId = tableId, self = self)
      context become STATE_GAME_RESULT(state = updatedState)

    case _ => log.logger.warn("Message Not Handled@STATE_NO_MORE_BETS state")
  }

  /** ******************************** TABLE_STATE_4_GAME_RESULT   ********************************************* */
  private def STATE_GAME_RESULT(state: TableState): Receive = {
    //From clients
    case AdminConnected(name, actor, client)          => context become STATE_GAME_RESULT(state = handleAdminConnected(tableState = state, adminIp = name, actor = actor, client = client))
    case AdminDisConnected(name)                      => context become STATE_GAME_RESULT(state = handleAdminDisconnected(tableState = state, adminIp = name))
    case PlayerConnected(name, actor, client)         => context become STATE_GAME_RESULT(state = handlePlayerConnected(tableState = state, playerIp = name, actor = actor, client = client, tableActorRef = self))
    case PlayerDisConnected(name)                     => context become STATE_GAME_RESULT(state = handlePlayerDisconnected(tableState = state, playerIp = name))
    case TopperConnected(name, actor, client)         => context become STATE_GAME_RESULT(state = handleTopperConnected(tableState = state, topperIp = name, actor = actor, client = client))
    case TopperDisconnected(name)                     => context become STATE_GAME_RESULT(state = handleTopperDisconnected(tableState = state, topperIp = name))

    case GuestConnectAccepted(ip, actor, client)      => context become STATE_GAME_RESULT(state = handleGuestConnectAccepted(tableState = state, playerIp = ip, actor = actor, client = client))
    case PlayerBalanceUpdated(name, balance)          => context become STATE_GAME_RESULT(state = handleBalanceUpdate(tableState = state, uid = name, newBalance = balance))

    case SaveGameData                                 =>
      tableService.setGameData(tableId = tableId, data = InitialData(
        tableId = tableId,
        roundId = state.roundId,
        gameType = state.configData.tableName,
        data = state.gameData,
        rouletteType = state.configData.tableName,
        physicalTableId = tableId
      ) )

    case PlaceYourBets                                =>
      val newRoundId = getRoundId()
      logActor ! Info(file = "AutoRouletteActor", str = s"PlaceYourBets STATE_GAME_RESULT => STATE_PLACE_YOUR_BETS  Round=$newRoundId")

      state.sendPlaceYourBetsMsgToAllClients()
      context become STATE_PLACE_YOUR_BETS(state = state.copy(
        gameStatus = "STATE_PLACE_YOUR_BETS",
        roundId = newRoundId,

      ))

    case PollTick => if(state.configData.autoPlay) {
      context become STATE_GAME_RESULT(state = state.copy(autoPlayCounter = state.autoPlayCounter + 1))
      if (state.autoPlayCounter == 10) {
        val newRoundId = getRoundId()
        logActor ! Info(file = "AutoRouletteActor", str = s"PollTick ${state.autoPlayCounter} STATE_GAME_RESULT => STATE_PLACE_YOUR_BETS  Round=$newRoundId")

        state.sendPlaceYourBetsMsgToAllClients()
        context become STATE_PLACE_YOUR_BETS(state = state.copy(
          gameStatus = "STATE_PLACE_YOUR_BETS",
          roundId = newRoundId,
          autoPlayCounter = 0,

        ))
      }
    }



    case _ => log.logger.warn("Message Not Handled@STATE_GAME_RESULT state")
  }


  def handlePlayerBetPlaced(tableState: TableState, playerIp: String, betList: Seq[Bet], mainActor: ActorRef): TableState = {
    val clients = tableState.clients

    if (clients.contains(playerIp)) {
      val client = clients(playerIp)
      val totalBet = betList.map(_.betValue).sum
      val oldBalance = client.balance
      val newBalance = oldBalance - totalBet
      clients(playerIp) = client.copy(balance = newBalance, betList = betList)

      logActor ! Info(file = "AutoRouletteActor", str = s"TablePlayerBetPlaced $playerIp $oldBalance - $totalBet = $newBalance : ${Json.toJson(betList).toString()} ")

      mainActor ! TablePlayerBetPlaced(
        tableId = tableId,
        gameName = tableState.configData.tableName,
        roundId = tableState.roundId,
        details = Json.toJson(betList).toString(),
        playerIp = playerIp,
        amount = totalBet,
        tableActorRef = self
      )

      tableState.sendCurrentBalanceMsg(clients(playerIp).client, clients(playerIp).uid, clients(playerIp).balance)
    }

    tableState.copy(clients = clients)
  }


  def handleBalanceUpdate(tableState: TableState, uid: String, newBalance: Double): TableState = {
    tableState.clients.find(p => p._2.uid == uid) match {
      case Some(player) =>
        val clients = tableState.clients
        val client = player._2.client
        val playerIp = player._1
        val clientData = player._2
        val oldBalance = clientData.balance

        clients(playerIp) = clientData.copy(balance = newBalance)
        val updatedTableState = tableState.copy(clients = clients)

        logActor ! Info(file = "AutoRouletteActor", str = s"PlayerBalanceUpdated $playerIp  $oldBalance -> $newBalance")


        updatedTableState.sendCurrentBalanceMsg(client, uid, newBalance)
        updatedTableState

      case None =>
        tableState
    }
  }





}

abstract class RouletteTable(tableId: String,
                             tableName: String,
                             limitId: Int,
                             gameService: GameService,
                             tableService: RouletteSSeaterTableService)
extends AutoRouletteGameLogics {

  val log: Logger = Logger(this.getClass)
  val dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss.SSS z")

  val logActor: ActorRef = gameService.getLoggingActor
  val mainActor: ActorRef = gameService.getMainActor

  def handleAdminConnected(tableState: TableState, adminIp: String, actor: ActorRef, client: ActorRef): TableState = {
    var admins = tableState.admins

    if (admins.contains(adminIp)) {
      admins(adminIp) = admins(adminIp).copy(actor = actor, client = client)
      logActor ! Info(file = "RouletteTable", str = s"Admin Reconnected from $adminIp")
      log.info(s"Admin Reconnected from $adminIp")
    } else {
      admins = admins ++ MMap(adminIp -> AdminClientData(actor = actor, client = client, name = adminIp))
      logActor ! Info(file = "RouletteTable", str = s"Admin from $adminIp connected")
      log.info(s"Admin from  $adminIp connected")
    }

    val updatedState = tableState.copy(admins = admins)
    updatedState.sendInitialDataAdminMsg(clientData = admins(adminIp))

    updatedState.gameStatus match {
      case "PLACE_YOUR_BETS" =>
        updatedState.sendPlaceYourBetsMsg(client = admins(adminIp).client)
      case "NO_MORE_BETS" =>
        updatedState.sendNoMoreBetsMsg(client = admins(adminIp).client)
      case _ =>
    }

    updatedState

  }

  /*
  * What it does when a player connects to a table ??
  * Pseudo Code
  * Inputs: (tableState, playerIp, actor, client, bettingTick)
  *
  * 1. Check if "playerIp" is already present in "clients"
  *   a) if YES just update the new [actor and client]
  *   b) else CREATE a new instance of ClientData and append to clients
  * 4. Compose InitialDataMessage and send it to "client"
  * 5. Compose CurrentBalanceMessage and send it to "client"
  * 6. Based on current TableState
  *   a) TABLE_STATE_1_IDLE    -
  *   b) STATE_PLACE_YOUR_BETS - send  PlaceYourBetsMessage
  *   c) STATE_NO_MORE_BETS    - send  NoMoreBetsMessage
  *   d) STATE_GAME_RESULT     - send  GameResultMessage
  *
  * */
  def handlePlayerConnected(tableState: TableState,
                            playerIp: String,
                            actor: ActorRef,
                            client: ActorRef,
                            tableActorRef : ActorRef): TableState = {

    var clients = tableState.clients

    if(clients.contains(playerIp)) {
      clients -= playerIp
    } else {
      log.info(s"A Player with ip $playerIp Trying to connect")
      val playerOpt = gameService.getPlayers.find(_.clientIp == playerIp)
      playerOpt match {
        case Some(foundPlayer) =>
          log.info(s"An existing IP Connected...$playerIp => uid=${foundPlayer.uid} balance=${foundPlayer.balance}")
        case None =>
          log.info(s"Guest IP $playerIp Trying to connect..")
        mainActor ! GuestConnectRequest(playerIp, actor, client, tableActorRef)
      }
    }

    gameService.getPlayers.find(_.clientIp == playerIp) match {
      case Some(player) =>
        //update table tableState - players
        clients = clients ++ MMap(playerIp -> ClientData(uid = player.uid, balance = player.balance, actor = actor, client = client))
        val updatedState = tableState.copy(clients = clients)

        tableState.sendInitialDataMsg(clientData = clients(playerIp))
        tableState.sendInitialConfigMsg(clients(playerIp).client)
        tableState.sendCurrentBalanceMsg(clients(playerIp).client, clients(playerIp).uid, clients(playerIp).balance)
        tableState.gameStatus match {
          case "PLACE_YOUR_BETS" =>
            tableState.sendPlaceYourBetsMsg(client = client)
          case "NO_MORE_BETS" =>
            tableState.sendNoMoreBetsMsg(client = client)
          case _ =>
        }

        mainActor ! PlayerStatusOnline(playerIp)

        updatedState
      case None =>
        tableState
    }


  }

  def handleTopperConnected(tableState: TableState, topperIp: String, actor: ActorRef, client: ActorRef): TableState = {

    var toppers = tableState.toppers

    if (toppers.contains(topperIp)) {
      toppers(topperIp) = toppers(topperIp).copy(actor = actor, client = client)
      log.info("Topper reconnected")
    } else {
      toppers = toppers ++ MMap(topperIp -> ClientData(actor = actor, client = client))
      log.info("Topper Fresh Connection")

    }

    val updatedState = tableState.copy(toppers = toppers)

    tableState.sendInitialDataMsg(clientData = toppers(topperIp))
    tableState.sendInitialConfigMsg(toppers(topperIp).client)
    tableState.gameStatus match {
      case "PLACE_YOUR_BETS" =>
        tableState.sendPlaceYourBetsMsg(client = client)
      case "NO_MORE_BETS" =>
        tableState.sendNoMoreBetsMsg(client = client)
      case _ =>
    }
    updatedState


  }

  def handleAdminDisconnected(tableState: TableState, adminIp: String): TableState = {
    val admins = tableState.admins
    if (admins.contains(adminIp)) admins -= adminIp
    val updatedState = tableState.copy(admins = admins)
    updatedState
  }

  def handleTopperDisconnected(tableState: TableState, topperIp: String): TableState = {
    val toppers = tableState.toppers
    if (toppers.contains(topperIp)) toppers -= topperIp
    val updatedState = tableState.copy(toppers = toppers)
    updatedState
  }

  def handlePlayerDisconnected(tableState: TableState, playerIp: String): TableState = {
    val clients = tableState.clients
    if (clients.contains(playerIp)) {
      clients -= playerIp
      //Turn player status to offline by
      mainActor ! PlayerStatusOffline(playerIp)
    }
    val updatedState = tableState.copy(clients = clients)

    updatedState
  }

  def handleGuestConnectAccepted(tableState: TableState,
                                 playerIp: String,
                                 actor: ActorRef,
                                 client: ActorRef): TableState = {

    var clients = tableState.clients
    log.error("handleGuestConnectAccepted")
    //Now check again
    gameService.getPlayers.find(_.clientIp == playerIp) match {
      case Some(player) =>
        //update table state - clients
        clients = clients ++ MMap(playerIp -> ClientData(uid = player.uid, balance = player.balance, actor = actor, client = client))
        val updatedState = tableState.copy(clients = clients)

        tableState.sendInitialDataMsg(clientData = clients(playerIp))
        tableState.sendInitialConfigMsg(clients(playerIp).client)
        tableState.sendCurrentBalanceMsg(clients(playerIp).client, clients(playerIp).uid, clients(playerIp).balance)
        tableState.gameStatus match {
          case "PLACE_YOUR_BETS" =>
            tableState.sendPlaceYourBetsMsg(client = client)
          case "NO_MORE_BETS" =>
            tableState.sendNoMoreBetsMsg(client = client)
          case _ =>
        }

        mainActor ! PlayerStatusOnline(playerIp)

        updatedState
      case None =>
        tableState
    }
  }

  def handleInfoPaperShow(tableState: TableState, show: Boolean): TableState = {
    val updatedState = tableState.copy(configData = tableState.configData.copy(showInfoPaper = show))
    updatedState.sendConfigUpdateMsgToClients()
    updatedState
  }

  def handleAutoPlayToggle(tableState: TableState): TableState = {
    log.info("Auto Play Toggle Received")

    val updatedConfigData = tableState.configData.copy(autoPlay = !tableState.configData.autoPlay)

    val updatedState = tableState.copy(
      configData = updatedConfigData,
      roundId = tableState.roundId,
      gameStatus = "IDLE",
      autoPlayCounter = 0,
    )
    updatedState.sendConfigUpdateMsgToClients()
    updatedState
  }

}

trait AutoRouletteGameLogics {

  val low: Seq[Int] = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18) //low
  val even: Seq[Int] = Seq(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36) //even
  val black: Seq[Int] = Seq(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35) //black
  val red: Seq[Int] = Seq(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36) //red
  val odd: Seq[Int] = Seq(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35) //odd
  val high: Seq[Int] = Seq(19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36) //high

  //columns
  val column3: Seq[Int] = Seq(3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36) //3rd
  val column2: Seq[Int] = Seq(2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35) //2nd
  val column1: Seq[Int] = Seq(1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34) //1st

  // dozens
  val dozen1: Seq[Int] = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12) //1st
  val dozen2: Seq[Int] = Seq(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24) //2nd
  val dozen3: Seq[Int] = Seq(25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36) //3rd

  def getWheelResult(): Int = {
    val r = scala.util.Random
    r.nextInt(37) // return Random.between(0,36)
  }

  def computeGroupData(history: Seq[(Int, Int)]): Group = {
    val size = if (history.map(_._2).sum == 0) 1 else history.map(_._2).sum

    val groupLow = history.filter(x => low.contains(x._1)).map(_._2).sum * 100 / size
    val groupEven = history.filter(x => even.contains(x._1)).map(_._2).sum * 100 / size
    val groupBlack = history.filter(x => black.contains(x._1)).map(_._2).sum * 100 / size
    val groupRed = history.filter(x => red.contains(x._1)).map(_._2).sum * 100 / size
    val groupOdd = history.filter(x => odd.contains(x._1)).map(_._2).sum * 100 / size
    val groupHigh = history.filter(x => high.contains(x._1)).map(_._2).sum * 100 / size

    val groupColumn3 = history.filter(x => column3.contains(x._1)).map(_._2).sum * 100 / size
    val groupColumn2 = history.filter(x => column2.contains(x._1)).map(_._2).sum * 100 / size
    val groupColumn1 = history.filter(x => column1.contains(x._1)).map(_._2).sum * 100 / size
    val groupDozen1 = history.filter(x => dozen1.contains(x._1)).map(_._2).sum * 100 / size
    val groupDozen2 = history.filter(x => dozen2.contains(x._1)).map(_._2).sum * 100 / size
    val groupDozen3 = history.filter(x => dozen3.contains(x._1)).map(_._2).sum * 100 / size

    Group(
      Group1to18 = groupLow,
      GroupEven = groupEven,
      GroupBlack = groupBlack,
      GroupRed = groupRed,
      GroupOdd = groupOdd,
      Group19to36 = groupHigh,
      GroupFirstLine = groupColumn1,
      GroupSecondLine = groupColumn2,
      GroupThirdLine = groupColumn3,
      Group1to12 = groupDozen1,
      Group13to24 = groupDozen2,
      Group25to36 = groupDozen3,
    )
  }

  val betIndexMap: Map[Int, Seq[Int]] = Map(
    100 -> Seq(0),
    101 -> Seq(1),
    102 -> Seq(2),
    103 -> Seq(3),
    104 -> Seq(4),
    105 -> Seq(5),
    106 -> Seq(6),
    107 -> Seq(7),
    108 -> Seq(8),
    109 -> Seq(9),
    110 -> Seq(10),
    111 -> Seq(11),
    112 -> Seq(12),
    113 -> Seq(13),
    114 -> Seq(14),
    115 -> Seq(15),
    116 -> Seq(16),
    117 -> Seq(17),
    118 -> Seq(18),
    119 -> Seq(19),
    120 -> Seq(20),
    121 -> Seq(21),
    122 -> Seq(22),
    123 -> Seq(23),
    124 -> Seq(24),
    125 -> Seq(25),
    126 -> Seq(26),
    127 -> Seq(27),
    128 -> Seq(28),
    129 -> Seq(29),
    130 -> Seq(30),
    131 -> Seq(31),
    132 -> Seq(32),
    133 -> Seq(33),
    134 -> Seq(34),
    135 -> Seq(35),
    135 -> Seq(35),
    136 -> Seq(36),
    137 -> Seq(37),

    //Splits
    200 -> Seq(0, 3),
    201 -> Seq(3, 6),
    202 -> Seq(6, 9),
    203 -> Seq(9, 12),
    204 -> Seq(12, 15),
    205 -> Seq(15, 18),
    206 -> Seq(18, 21),
    207 -> Seq(21, 24),
    208 -> Seq(24, 27),
    209 -> Seq(27, 30),
    210 -> Seq(30, 33),
    211 -> Seq(33, 36),

    212 -> Seq(2, 3),
    213 -> Seq(5, 6),
    214 -> Seq(8, 9),
    215 -> Seq(11, 12),
    216 -> Seq(14, 15),
    217 -> Seq(17, 18),
    218 -> Seq(20, 21),
    219 -> Seq(23, 24),
    220 -> Seq(26, 27),
    221 -> Seq(29, 30),
    222 -> Seq(32, 33),
    223 -> Seq(35, 36),

    224 -> Seq(0, 2),
    225 -> Seq(2, 5),
    226 -> Seq(5, 8),
    227 -> Seq(8, 11),
    228 -> Seq(11, 14),
    229 -> Seq(14, 17),
    230 -> Seq(17, 20),
    231 -> Seq(20, 23),
    232 -> Seq(23, 26),
    233 -> Seq(26, 29),
    234 -> Seq(29, 32),
    235 -> Seq(32, 35),

    236 -> Seq(1, 2),
    237 -> Seq(4, 5),
    238 -> Seq(7, 8),
    239 -> Seq(10, 11),
    240 -> Seq(13, 14),
    241 -> Seq(16, 17),
    242 -> Seq(19, 20),
    243 -> Seq(22, 23),
    244 -> Seq(25, 26),
    245 -> Seq(28, 29),
    246 -> Seq(31, 32),
    247 -> Seq(34, 35),

    248 -> Seq(0, 1),
    249 -> Seq(1, 4),
    250 -> Seq(4, 7),
    251 -> Seq(7, 10),
    252 -> Seq(10, 13),
    253 -> Seq(13, 16),
    254 -> Seq(16, 19),
    255 -> Seq(19, 22),
    256 -> Seq(22, 25),
    257 -> Seq(25, 28),
    258 -> Seq(28, 31),
    259 -> Seq(31, 34),

    290 -> Seq(0, 37),
    291 -> Seq(3, 37),
    292 -> Seq(1, 0),

    //trio
    300 -> Seq(0, 2, 3), //american
    301 -> Seq(0, 1, 2), //american

    //street
    500 -> Seq(1, 2, 3),
    501 -> Seq(4, 5, 6),
    502 -> Seq(7, 8, 9),
    503 -> Seq(10, 11, 12),
    504 -> Seq(13, 14, 15),
    505 -> Seq(16, 17, 18),
    506 -> Seq(19, 20, 21),
    507 -> Seq(22, 23, 24),
    508 -> Seq(25, 26, 27),
    509 -> Seq(28, 29, 30),
    510 -> Seq(31, 32, 33),
    511 -> Seq(34, 35, 36),

    590 -> Seq(0, 37, 2), //american
    591 -> Seq(0, 1, 2), //american
    592 -> Seq(37, 2, 3), //american

    //corners
    400 -> Seq(2, 3, 5, 6),
    401 -> Seq(5, 6, 8, 9),
    402 -> Seq(8, 9, 11, 12),
    403 -> Seq(11, 12, 14, 15),
    404 -> Seq(14, 15, 17, 18),
    405 -> Seq(17, 18, 20, 21),
    406 -> Seq(20, 21, 23, 24),
    407 -> Seq(23, 24, 26, 27),
    408 -> Seq(26, 27, 29, 30),
    409 -> Seq(29, 30, 32, 33),
    410 -> Seq(32, 33, 35, 36),

    411 -> Seq(1, 2, 4, 5),
    412 -> Seq(4, 5, 7, 8),
    413 -> Seq(7, 8, 10, 11),
    414 -> Seq(10, 11, 13, 14),
    415 -> Seq(13, 14, 16, 17),
    416 -> Seq(16, 17, 19, 20),
    417 -> Seq(19, 20, 22, 23),
    418 -> Seq(22, 23, 25, 26),
    419 -> Seq(25, 26, 28, 29),
    420 -> Seq(28, 29, 31, 32),
    421 -> Seq(31, 32, 34, 35),
    422 -> Seq(0, 1, 2, 3),


    //six line
    600 -> Seq(1, 2, 3, 4, 5, 6),
    601 -> Seq(4, 5, 6, 7, 8, 9),
    602 -> Seq(7, 8, 9, 10, 11, 12),
    603 -> Seq(10, 11, 12, 13, 14, 15),
    604 -> Seq(13, 14, 15, 16, 17, 18),
    605 -> Seq(16, 17, 18, 19, 20, 21),
    606 -> Seq(19, 20, 21, 22, 23, 24),
    607 -> Seq(22, 23, 24, 25, 26, 27),
    608 -> Seq(25, 26, 27, 28, 29, 30),
    609 -> Seq(28, 29, 30, 31, 32, 33),
    610 -> Seq(31, 32, 33, 34, 35, 36),

    690 -> Seq(0, 37, 1, 2, 3),

    //columns
    700 -> Seq(3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36), //3rd
    701 -> Seq(2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35), //2nd
    702 -> Seq(1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34), //1st

    // dozens
    800 -> Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12), //1st
    801 -> Seq(13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24), //2nd
    802 -> Seq(25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36), //3rd


    // outside bets
    900 -> Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18), //low
    901 -> Seq(2, 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36), //even
    902 -> Seq(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35), //black
    903 -> Seq(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36), //red
    904 -> Seq(1, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31, 33, 35), //odd
    905 -> Seq(19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36), //high

  )
  val betPayout: Map[Int, Int] = Map(
    100 -> 35,
    101 -> 35,
    102 -> 35,
    103 -> 35,
    104 -> 35,
    105 -> 35,
    106 -> 35,
    107 -> 35,
    108 -> 35,
    109 -> 35,
    110 -> 35,
    111 -> 35,
    112 -> 35,
    113 -> 35,
    114 -> 35,
    115 -> 35,
    116 -> 35,
    117 -> 35,
    118 -> 35,
    119 -> 35,
    120 -> 35,
    121 -> 35,
    122 -> 35,
    123 -> 35,
    124 -> 35,
    125 -> 35,
    126 -> 35,
    127 -> 35,
    128 -> 35,
    129 -> 35,
    130 -> 35,
    131 -> 35,
    132 -> 35,
    133 -> 35,
    134 -> 35,
    135 -> 35,
    135 -> 35,
    136 -> 35,
    137 -> 35,

    //Splits
    200 -> 17,
    201 -> 17,
    202 -> 17,
    203 -> 17,
    204 -> 17,
    205 -> 17,
    206 -> 17,
    207 -> 17,
    208 -> 17,
    209 -> 17,
    210 -> 17,
    211 -> 17,

    212 -> 17,
    213 -> 17,
    214 -> 17,
    215 -> 17,
    216 -> 17,
    217 -> 17,
    218 -> 17,
    219 -> 17,
    220 -> 17,
    221 -> 17,
    222 -> 17,
    223 -> 17,

    224 -> 17,
    225 -> 17,
    226 -> 17,
    227 -> 17,
    228 -> 17,
    229 -> 17,
    230 -> 17,
    231 -> 17,
    232 -> 17,
    233 -> 17,
    234 -> 17,
    235 -> 17,

    236 -> 17,
    237 -> 17,
    238 -> 17,
    239 -> 17,
    240 -> 17,
    241 -> 17,
    242 -> 17,
    243 -> 17,
    244 -> 17,
    245 -> 17,
    246 -> 17,
    247 -> 17,

    248 -> 17,
    249 -> 17,
    250 -> 17,
    251 -> 17,
    252 -> 17,
    253 -> 17,
    254 -> 17,
    255 -> 17,
    256 -> 17,
    257 -> 17,
    258 -> 17,
    259 -> 17,

    290 -> 17,
    291 -> 17,
    292 -> 17,

    //trio
    300 -> 11, //american
    301 -> 11, //american

    //street
    500 -> 11,
    501 -> 11,
    502 -> 11,
    503 -> 11,
    504 -> 11,
    505 -> 11,
    506 -> 11,
    507 -> 11,
    508 -> 11,
    509 -> 11,
    510 -> 11,
    511 -> 11,

    590 -> 11, //american
    591 -> 11, //american
    592 -> 11, //american

    //corners
    400 -> 8,
    401 -> 8,
    402 -> 8,
    403 -> 8,
    404 -> 8,
    405 -> 8,
    406 -> 8,
    407 -> 8,
    408 -> 8,
    409 -> 8,
    410 -> 8,

    411 -> 8,
    412 -> 8,
    413 -> 8,
    414 -> 8,
    415 -> 8,
    416 -> 8,
    417 -> 8,
    418 -> 8,
    419 -> 8,
    420 -> 8,
    421 -> 8,
    422 -> 8,


    //six line
    600 -> 5,
    601 -> 5,
    602 -> 5,
    603 -> 5,
    604 -> 5,
    605 -> 5,
    606 -> 5,
    607 -> 5,
    608 -> 5,
    609 -> 5,
    610 -> 5,

    690 -> 5,

    //columns
    700 -> 2, //3rd
    701 -> 2, //2nd
    702 -> 2, //1st

    // dozens
    800 -> 2, //1st
    801 -> 2, //2nd
    802 -> 2, //3rd


    // outside bets
    900 -> 1,
    901 -> 1,
    902 -> 1,
    903 -> 1,
    904 -> 1,
    905 -> 1, //high

  )

}

