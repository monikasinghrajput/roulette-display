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
package model.roulette.data

import actors.MainActor.{TablePlayerBetLost, TablePlayerBetWon, TableRoundResultIndication}
import akka.actor.ActorRef

import collection.mutable.{Map => MMap}
import model.roulette.RouletteJsonCodecs
import play.api.Logger
import model.common.data._
import model.common.messages.CurrentBalanceMessage
import model.roulette.message._
import play.api.libs.json.Json

import java.text.SimpleDateFormat
import java.util.Calendar

case class TableState(roundId: Long = 1,
                      gameData: RouletteGameData = RouletteGameData(),
                      wheelData: WheelData = WheelData(),
                      configData: ConfigData = ConfigData(),
                      licenseData: LicenseData = LicenseData(),
                      admins: MMap[String, AdminClientData] = MMap.empty[String, AdminClientData],
                      clients: MMap[String, ClientData] = MMap.empty[String, ClientData],
                      toppers: MMap[String, ClientData] = MMap.empty[String, ClientData],

                      gameStatus: String = "CLOSED",
                      autoPlayCounter: Int = 0,
                      bettingTick: Int = 0,
                      wheelSpinningTick: Int = 0,
                      timerTimeLeft: Int = 0
                     )
  extends RouletteJsonCodecs {

  val logger: Logger = Logger(this.getClass)
  val dateFormat: SimpleDateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss.SSS z")

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

  def sendInitialDataAdminMsg(clientData: AdminClientData): Unit = {

    val roulette8SeaterData = Roulette8SeaterData(
      wheel = wheelData,
      game = gameData,
      seats = Seq.empty[Seat],
      logs = Seq.empty[ServerLog]
    )


    val initialDataMessage = InitialAdminDataMessage(
      roundId = roundId,
      tableId = configData.tableId,
      gameType = configData.tableName,
      clientId = clientData.name,
      timestamp = dateFormat.format(Calendar.getInstance().getTime),
      data = roulette8SeaterData,
      players = Seq.empty[Client],
    )

    clientData.client ! Json.toJson(initialDataMessage)

  }

  def sendInitialConfigMsg(client: ActorRef): Unit = {
    val initialConfigMsg = ConfigUpdateMsg(
      configData = configData,
      timestamp = dateFormat.format(Calendar.getInstance().getTime)
    )


    client ! Json.toJson(initialConfigMsg)
  }

  def sendInitialDataMsg(clientData: ClientData): Unit = {

    val playerGameData = PlayerGameData(
      group = gameData.group,
      coldNumbers = gameData.coldNumbers,
      hotNumbers = gameData.hotNumbers,
      lastWinners = gameData.lastWinners,
      statistics = gameData.statistics,
      history = gameData.history,
      playerBetsOfThisRound = clientData.betList,
      balance = clientData.balance)

    val initialDataMessage = InitialDataMessage("InitialData",
      tableId = configData.tableId,
      destination = "player",
      clientId = clientData.uid,
      roundId = roundId,
      gameType = configData.tableName,
      timestamp = dateFormat.format(Calendar.getInstance().getTime),
      playerGameData
    )


    clientData.client ! Json.toJson(initialDataMessage)

  }

  def sendCurrentBalanceMsg(client: ActorRef, uid: String, balance: Double): Unit = {
    val currentBalanceMessage = CurrentBalanceMessage(
      tableId = configData.tableId,
      clientId = uid,
      roundId = roundId,
      gameType = configData.tableName,
      timestamp = dateFormat.format(Calendar.getInstance().getTime),
      balance = balance,
    )

    client ! Json.toJson(currentBalanceMessage)
  }

  def sendPlaceYourBetsMsg(client: ActorRef): Unit = {
    val placeYourBetsMsg = PlaceYourBetsMessage(
      tableId = configData.tableId,
      roundId = roundId,
      timestamp = dateFormat.format(Calendar.getInstance().getTime),
      timerTime = configData.timerTime,
      timerTimeLeft = configData.timerTime - timerTimeLeft
    )

    client ! Json.toJson(placeYourBetsMsg)

  }

  def sendPlaceYourBetsMsgToAllClients(): Unit = {
    val placeYourBetsMsg = PlaceYourBetsMessage(
      tableId = configData.tableId,
      roundId = roundId,
      timestamp = dateFormat.format(Calendar.getInstance().getTime),
      timerTime = configData.timerTime,
      timerTimeLeft = configData.timerTime - timerTimeLeft
    )

    admins.foreach{
      admin =>
        admin._2.client ! Json.toJson(placeYourBetsMsg)
    }

    toppers.foreach{
      topper =>
        topper._2.client ! Json.toJson(placeYourBetsMsg)
    }

    clients.foreach{
      client =>
        client._2.client ! Json.toJson(placeYourBetsMsg)
    }

  }

  def sendNoMoreBetsMsg(client: ActorRef): Unit = {

    val noMoreBetsMsg = NoMoreBetsMessage(
      roundId = roundId,
      timestamp = dateFormat.format(Calendar.getInstance().getTime),
    )

    client ! Json.toJson(noMoreBetsMsg)


  }

  def sendNoMoreBetsMsgToAllClients(): Unit = {
    val noMoreBetsMsg = NoMoreBetsMessage(
      roundId = roundId,
      timestamp = dateFormat.format(Calendar.getInstance().getTime),
    )

    admins.foreach{
      admin =>
        admin._2.client ! Json.toJson(noMoreBetsMsg)
    }

    toppers.foreach{
      topper =>
        topper._2.client ! Json.toJson(noMoreBetsMsg)
    }

    clients.foreach{
      client =>
        client._2.client ! Json.toJson(noMoreBetsMsg)
    }

  }

  def sendConfigUpdateMsgToClients(): Unit = {

    val configUpdateMsg = ConfigUpdateMsg(
      configData = configData,
      timestamp = dateFormat.format(Calendar.getInstance().getTime))


    admins.foreach {
      admin =>
        admin._2.client ! Json.toJson(configUpdateMsg)
    }

    toppers.foreach {
      topper =>
        topper._2.client ! Json.toJson(configUpdateMsg)
    }

    clients.foreach {
      client =>
        client._2.client ! Json.toJson(configUpdateMsg)
    }


  }


  def sendGameResultToAllClients(winResult: Int, mainActor: ActorRef, tableId: String, self: ActorRef): Unit = {

    val gameResultMessage = GameResultMessage(
      tableId = configData.tableId,
      roundId = roundId,
      gameType = configData.tableName,
      timestamp = dateFormat.format(Calendar.getInstance().getTime),
      group = gameData.group,
      coldNumbers = gameData.coldNumbers,
      hotNumbers = gameData.hotNumbers,
      statistics = gameData.statistics,
      lastWinners = gameData.lastWinners,
      gameResults = gameData.history,
      winAmount = 0
    )

    toppers.foreach(topper => {
      sendGameResultMsg(topper._2, 0)
    })

    //bets list & win list are to be populated fresh - with clients.foreach
    var playerBetsList: List[(String, Seq[Bet])] = List.empty[(String, Seq[Bet])]
    var playerWonBetsList: List[(String, Seq[WinBet])] = List.empty[(String, Seq[WinBet])]
    clients.foreach {
      client => {
        if (client._2.betList.nonEmpty) {

          val totalWin: Double = client._2.betList
            .filter(bet => betIndexMap(bet.index).contains(winResult))
            .map(bet => (betPayout(bet.index) + 1) * bet.betValue)
            .sum

          sendGameResultMsg(client._2, totalWin)

          val playerWonBets = client._2.betList
            .filter(bet => betIndexMap(bet.index).contains(winResult))
            .map(bet => WinBet(winningIndex = bet.index, winAmount = (betPayout(bet.index) + 1) * bet.betValue))



          playerBetsList = playerBetsList ++ List(client._2.uid -> client._2.betList)
          playerWonBetsList = playerWonBetsList ++ List(client._2.uid -> playerWonBets)


          if (totalWin > 0) {
            //Update Balance and Reset betList
            clients(client._1) = clients(client._1).copy(balance = clients(client._1).balance + totalWin, betList = Seq.empty[Bet])

            val wonBetsMessage = WonBetsMessage(clientId = client._2.uid, winningBets = playerWonBets)
            client._2.client ! Json.toJson(wonBetsMessage)
            sendCurrentBalanceMsg(client._2.client, client._2.uid, clients(client._1).balance)

            mainActor ! TablePlayerBetWon(
              tableId = tableId,
              gameName = configData.tableName,
              roundId = roundId,
              details = Json.toJson(playerWonBets).toString(),
              betDetails = Json.toJson(client._2.betList).toString(),
              resultDetails = Json.toJson(gameResultMessage.copy(clientId = client._2.uid, winAmount = totalWin )).toString(),
              playerIp = client._1,
              amount = totalWin,
              tableActorRef = self)

          } else {

            //Reset betList
            clients(client._1) = clients(client._1).copy(betList = Seq.empty[Bet])

            mainActor ! TablePlayerBetLost(
              tableId = tableId,
              gameName = configData.tableName,
              roundId = roundId,
              details = Json.toJson(playerWonBets).toString(),
              betDetails = Json.toJson(client._2.betList).toString(),
              resultDetails = Json.toJson(gameResultMessage.copy(clientId = client._2.uid, winAmount = totalWin )).toString(),
              playerIp = client._1,
              amount = totalWin,
              tableActorRef = self)
          }
        }
        else {
          sendGameResultMsg(client._2, 0)
        }
      }
    }

    val playersTotalBet: List[(String, Double)] = playerBetsList.map(bet => bet._1 -> bet._2.map(_.betValue).sum)
    val playersTotalWin: List[(String, Double)] = playerWonBetsList.map(win => win._1 -> win._2.map(_.winAmount).sum)

    mainActor ! TableRoundResultIndication(
      tableId = tableId,
      gameName = configData.tableName,
      roundId = roundId,
      winningHand = winResult.toString,
      gameResult = Json.toJson(gameResultMessage.copy(winAmount = playersTotalBet.map(_._2).sum - playersTotalWin.map(_._2).sum)).toString(),
      playersTotalBet = playersTotalBet,
      playersTotalWin = playersTotalWin,
      playerBetsList = Json.toJson(playerBetsList).toString(),
      playerWonBetsList = Json.toJson(playerWonBetsList).toString()
    )

  }

  def sendGameResultMsg(clientData: ClientData, winAmount: Double = 0): Unit = {

    val gameResultMessage = GameResultMessage(
      tableId = configData.tableId,
      clientId = clientData.uid,
      roundId = roundId,
      gameType = configData.tableName,
      timestamp = dateFormat.format(Calendar.getInstance().getTime),
      group = gameData.group,
      coldNumbers = gameData.coldNumbers,
      hotNumbers = gameData.hotNumbers,
      statistics = gameData.statistics,
      lastWinners = gameData.lastWinners,
      gameResults = gameData.history,
      winAmount = winAmount
    )
    clientData.client ! Json.toJson(gameResultMessage)

  }

}
