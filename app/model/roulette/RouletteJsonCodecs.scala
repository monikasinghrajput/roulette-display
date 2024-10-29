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
package model.roulette

import model.common.data.{Chip, LicenseData, ServerLog}
import model.roulette.data._
import model.roulette.message._
import model.common.CommonJsonCodecs

trait RouletteJsonCodecs extends CommonJsonCodecs{

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val groupWrites: Writes[Group] = (groupData: Group) => Json.obj(
    "GroupFirstLine" -> groupData.GroupFirstLine,
    "GroupSecondLine" -> groupData.GroupSecondLine,
    "GroupThirdLine" -> groupData.GroupThirdLine,
    "Group1to12" -> groupData.Group1to12,
    "Group13to24" -> groupData.Group13to24,
    "Group25to36" -> groupData.Group25to36,
    "GroupBlack" -> groupData.GroupBlack,
    "GroupRed" -> groupData.GroupRed,
    "GroupOdd" -> groupData.GroupOdd,
    "GroupEven" -> groupData.GroupEven,
    "Group1to18" -> groupData.Group1to18,
    "Group19to36" -> groupData.Group19to36
  )

  implicit val groupReads: Reads[Group] = (
    (JsPath \ "GroupFirstLine").read[Double] and
      (JsPath \ "GroupSecondLine").read[Double] and
      (JsPath \ "GroupThirdLine").read[Double] and
      (JsPath \ "Group1to12").read[Double] and
      (JsPath \ "Group13to24").read[Double] and
      (JsPath \ "Group25to36").read[Double] and
      (JsPath \ "GroupBlack").read[Double] and
      (JsPath \ "GroupRed").read[Double] and
      (JsPath \ "GroupOdd").read[Double] and
      (JsPath \ "GroupEven").read[Double] and
      (JsPath \ "Group1to18").read[Double] and
      (JsPath \ "Group19to36").read[Double]
    ) (Group.apply _)

  implicit val betWrites: Writes[Bet] = (betData: Bet) => Json.obj(
    "index" -> betData.index,
    "betValue" -> betData.betValue,
    "group" -> betData.group,
    "betType" -> betData.betType
  )

  implicit val betReads: Reads[Bet] = (
    (JsPath \ "index").read[Int] and
      (JsPath \ "betValue").read[Double] and
      (JsPath \ "group").read[String] and
      (JsPath \ "betType").read[String]
    ) (Bet.apply _)

  implicit val winBetWrites: Writes[WinBet] = (winBetData: WinBet) => Json.obj(
    "WinningIndex" -> winBetData.winningIndex,
    "WinAmount" -> winBetData.winAmount
  )
  implicit val winBetReads: Reads[WinBet] = (
    (JsPath \ "WinningIndex").read[Int] and
      (JsPath \ "WinAmount").read[Double]
    ) (WinBet.apply _)

  implicit val winnerWrites: Writes[Winner] = (winnerData: Winner) => Json.obj(
    "WinAmount" -> winnerData.winAmount,
    "Nickname" -> winnerData.nickName,
  )

  implicit val winnerReads: Reads[Winner] = (
    (JsPath \ "WinAmount").read[Double] and
      (JsPath \ "Nickname").read[String]
    ) (Winner.apply _)



  implicit val winWrites: Writes[Win] = (winData: Win) => Json.obj(
    "WinningNumber" -> winData.winningNUmber,
    "roundId" -> winData.roundId,
  )

  implicit val winReads: Reads[Win] = (
    (JsPath \ "WinningNumber").read[Int] and
      (JsPath \ "roundId").read[Long]
    ) (Win.apply _)


  implicit val statWrites: Writes[Stat] = (statData: Stat) => Json.obj(
    "Number" -> statData.number,
    "Percent" -> statData.percent,
  )

  implicit val statReads: Reads[Stat] = (
    (JsPath \ "Number").read[Int] and
      (JsPath \ "Percent").read[Double]
    ) (Stat.apply _)


  implicit val rouletteGameDataWrites: Writes[RouletteGameData] = (playerGameData: RouletteGameData) => Json.obj(
    "Group" -> playerGameData.group,
    "ColdNumbers" -> playerGameData.coldNumbers,
    "LastWinners" -> playerGameData.lastWinners,
    "HotNumbers" -> playerGameData.hotNumbers,
    "History" -> playerGameData.history,
    "Statistics" -> playerGameData.statistics,
  )

  implicit val rouletteGameDataReads: Reads[RouletteGameData] = (
    (JsPath \ "Group").read[Group] and
      (JsPath \ "ColdNumbers").read[Seq[Int]] and
      (JsPath \ "LastWinners").read[Seq[Winner]] and
      (JsPath \ "HotNumbers").read[Seq[Int]] and
      (JsPath \ "History").read[Seq[Win]] and
      (JsPath \ "Statistics").read[Seq[Stat]]
    ) (RouletteGameData.apply _)

  implicit val playerGameDataWrites: Writes[PlayerGameData] = (playerGameData: PlayerGameData) => Json.obj(
    "Group" -> playerGameData.group,
    "playerBetOfThisRound" -> playerGameData.playerBetsOfThisRound,
    "ColdNumbers" -> playerGameData.coldNumbers,
    "balance" -> playerGameData.balance,
    "LastWinners" -> playerGameData.lastWinners,
    "HotNumbers" -> playerGameData.hotNumbers,
    "History" -> playerGameData.history,
    "Statistics" -> playerGameData.statistics,
  )
  implicit val playerGameDataReads: Reads[PlayerGameData] = (
    (JsPath \ "Group").read[Group] and
      (JsPath \ "playerBetOfThisRound").read[Seq[Bet]] and
      (JsPath \ "ColdNumbers").read[Seq[Int]] and
      (JsPath \ "balance").read[Double] and
      (JsPath \ "LastWinners").read[Seq[Winner]] and
      (JsPath \ "HotNumbers").read[Seq[Int]] and
      (JsPath \ "History").read[Seq[Win]] and
      (JsPath \ "Statistics").read[Seq[Stat]]
    ) (PlayerGameData.apply _)



  implicit val initialDataWrites: Writes[InitialData] = (initialData: InitialData) => Json.obj(
    "TableId" -> initialData.tableId,
    "roundId" -> initialData.roundId,
    "gameType" -> initialData.gameType,
    "data" -> initialData.data,
    "RouletteType" -> initialData.rouletteType,
    "physicalTableId" -> initialData.physicalTableId
  )

  implicit val initialDataReads: Reads[InitialData] = (
    (JsPath \ "TableId").read[String] and
      (JsPath \ "roundId").read[Long] and
      (JsPath \ "gameType").read[String] and
      (JsPath \ "data").read[RouletteGameData] and
      (JsPath \ "RouletteType").read[String] and
      (JsPath \ "physicalTableId").read[String]
    ) (InitialData.apply _)

  implicit val initialDataMsgWrites: Writes[InitialDataMessage] = (initialDataMsg: InitialDataMessage) => Json.obj(
    "MessageType" -> initialDataMsg.MessageType,
    "TableId" -> initialDataMsg.tableId,
    "destination" -> initialDataMsg.destination,
    "ClientId" -> initialDataMsg.clientId,
    "roundId" -> initialDataMsg.roundId,
    "gameType" -> initialDataMsg.gameType,
    "timestamp" -> initialDataMsg.timestamp,
    "data" -> initialDataMsg.data
  )

  implicit val initialDataMsgReads: Reads[InitialDataMessage] = (
    (JsPath \ "MessageType").read[String] and
      (JsPath \ "TableId").read[String] and
      (JsPath \ "destination").read[String] and
      (JsPath \ "ClientId").read[String] and
      (JsPath \ "roundId").read[Long] and
      (JsPath \ "gameType").read[String] and
      (JsPath \ "timestamp").read[String] and
      (JsPath \ "data").read[PlayerGameData]
    ) (InitialDataMessage.apply _)

  implicit val initialAdminDataReads: Reads[InitialAdminData] = (
    (JsPath \ "tableId").read[String] and
      (JsPath \ "roundId").read[Int] and
      (JsPath \ "data").read[Roulette8SeaterData]
    ) (InitialAdminData.apply _)

  implicit val seatHistoryReads: Reads[SeatHistory] = (
    (JsPath \ "roundId").read[Int] and
      (JsPath \ "lastBalance").read[Double] and
      (JsPath \ "betList").read[Seq[Bet]] and
      (JsPath \ "winningBets").read[Seq[WinBet]] and
      (JsPath \ "totalBet").read[Double] and
      (JsPath \ "winAmount").read[Double] and
      (JsPath \ "currentBalance").read[Double]
    ) (SeatHistory.apply _)


  implicit val seatHistoryWrites: Writes[SeatHistory] = (seatHistory: SeatHistory) => Json.obj(
    "roundId" -> seatHistory.roundId,
    "lastBalance" -> seatHistory.lastBalance,
    "betList" -> seatHistory.betList,
    "winningBets" -> seatHistory.winningBets,
    "totalBet" -> seatHistory.totalBet,
    "winAmount" -> seatHistory.winAmount,
    "currentBalance" -> seatHistory.currentBalance,
  )

  implicit val seatDataReads: Reads[Seat] = (
    (JsPath \ "uid").read[String] and
      (JsPath \ "connected").read[Boolean] and
      (JsPath \ "totalBet").read[Double] and
      (JsPath \ "winAmount").read[Double] and
      (JsPath \ "lastWin").read[Double] and
      (JsPath \ "history").read[Seq[SeatHistory]] and
      (JsPath \ "betList").read[Seq[Bet]] and
      (JsPath \ "winningBets").read[Seq[WinBet]] and
      (JsPath \ "isTurn").read[Boolean] and
      (JsPath \ "gameStatus").read[String] and
      (JsPath \ "isDealer").read[Boolean] and
      (JsPath \ "isSmallBet").read[Boolean] and
      (JsPath \ "isBigBet").read[Boolean] and
      (JsPath \ "cards").read[Seq[String]] and
      (JsPath \ "bets").read[Seq[Double]] and
      (JsPath \ "actions").read[Seq[String]]
    ) (Seat.apply _)


  implicit val seatDataWrites: Writes[Seat] = (seatData: Seat) => Json.obj(
    "uid" -> seatData.uid,
    "connected" -> seatData.connected,
    "totalBet" -> seatData.totalBet,
    "winAmount" -> seatData.winAmount,
    "lastWin" -> seatData.lastWin,
    "history" -> seatData.history,
    "betList" -> seatData.betList,
    "winningBets" -> seatData.winningBets,
    "isTurn" -> seatData.isTurn,
    "gameStatus" -> seatData.gameStatus,
    "isDealer" -> seatData.isDealer,
    "isSmallBet" -> seatData.isSmallBet,
    "isBigBet" -> seatData.isBigBet,
    "cards" -> seatData.cards,
    "bets" -> seatData.bets,
    "actions" -> seatData.actions,
  )

  implicit val wheelDataReads: Reads[WheelData] = (
    (JsPath \ "connected").read[Boolean] and
      (JsPath \ "status").read[String] and
      (JsPath \ "lastResult").read[Int]
    ) (WheelData.apply _)


  implicit val wheelDataWrites: Writes[WheelData] = (wheelData: WheelData) => Json.obj(
    "connected" -> wheelData.connected,
    "status" -> wheelData.status,
    "lastResult" -> wheelData.lastResult,
  )

  implicit val roulette8SeaterDataReads: Reads[Roulette8SeaterData] = (
    (JsPath \ "wheel").read[WheelData] and
      (JsPath \ "seats").read[Seq[Seat]] and
      (JsPath \ "game").read[RouletteGameData] and
      (JsPath \ "logs").read[Seq[ServerLog]]
    ) (Roulette8SeaterData.apply _)


  implicit val roulette8SeaterDataWrites: Writes[Roulette8SeaterData] = (roulette8SeaterData: Roulette8SeaterData) => Json.obj(
    "wheel" -> roulette8SeaterData.wheel,
    "seats" -> roulette8SeaterData.seats,
    "game" -> roulette8SeaterData.game,
    "logs" -> roulette8SeaterData.logs,
  )

  implicit val initialAdminDataWrites: Writes[InitialAdminData] = (initialAdminData: InitialAdminData) => Json.obj(
    "tableId" -> initialAdminData.tableId,
    "roundId" -> initialAdminData.roundId,
    "data" -> initialAdminData.data,
  )

  implicit val clientWrites: Writes[Client] = (player: Client) => Json.obj(
    "uid" -> player.uid,
    "playerIp" -> player.playerIp,
    "betList" -> player.betList,
    "winningBets" -> player.winningBets,
    "balance" -> player.balance
  )

  implicit val clientReads: Reads[Client] = (
    (JsPath \ "uid").read[String] and
      (JsPath \ "playerIp").read[String] and
      (JsPath \ "betList").read[Seq[Bet]] and
      (JsPath \ "winningBets").read[Seq[WinBet]] and
      (JsPath \ "balance").read[Double]
    ) (Client.apply _)

  implicit val initialAdminDataMsgWrites: Writes[InitialAdminDataMessage] = (initialAdminDataMsg: InitialAdminDataMessage) => Json.obj(
    "MessageType" -> initialAdminDataMsg.MessageType,
    "TableId" -> initialAdminDataMsg.tableId,
    "ClientId" -> initialAdminDataMsg.clientId,
    "roundId" -> initialAdminDataMsg.roundId,
    "gameType" -> initialAdminDataMsg.gameType,
    "timestamp" -> initialAdminDataMsg.timestamp,
    "data" -> initialAdminDataMsg.data,
    "players" -> initialAdminDataMsg.players
  )

  implicit val initialAdminDataMsgReads: Reads[InitialAdminDataMessage] = (
    (JsPath \ "MessageType").read[String] and
      (JsPath \ "TableId").read[String] and
      (JsPath \ "ClientId").read[String] and
      (JsPath \ "roundId").read[Long] and
      (JsPath \ "gameType").read[String] and
      (JsPath \ "timestamp").read[String] and
      (JsPath \ "data").read[Roulette8SeaterData] and
      (JsPath \ "players").read[Seq[Client]]
    ) (InitialAdminDataMessage.apply _)


  implicit val placeYourBetsMsgWrites: Writes[PlaceYourBetsMessage] = (placeYourBetsMsg: PlaceYourBetsMessage) => Json.obj(
    "MessageType" -> placeYourBetsMsg.MessageType,
    "TableId" -> placeYourBetsMsg.tableId,
    "destination" -> placeYourBetsMsg.destination,
    "ClientId" -> placeYourBetsMsg.clientId,
    "roundId" -> placeYourBetsMsg.roundId,
    "gameType" -> placeYourBetsMsg.gameType,
    "RoundTripStartTime" -> placeYourBetsMsg.roundTripStartTime,
    "timestamp" -> placeYourBetsMsg.timestamp,
    "TimerTimeLeft" -> placeYourBetsMsg.timerTimeLeft,
    "TimerTime" -> placeYourBetsMsg.timerTime
  )

  implicit val placeYourBetsMsgReads: Reads[PlaceYourBetsMessage] = (
    (JsPath \ "MessageType").read[String] and
      (JsPath \ "TableId").read[String] and
      (JsPath \ "destination").read[String] and
      (JsPath \ "ClientId").read[String] and
      (JsPath \ "roundId").read[Long] and
      (JsPath \ "gameType").read[String] and
      (JsPath \ "RoundTripStartTime").read[Long] and
      (JsPath \ "timestamp").read[String] and
      (JsPath \ "TimerTimeLeft").read[Int] and
      (JsPath \ "TimerTime").read[Int]
    ) (PlaceYourBetsMessage.apply _)

  implicit val noMoreBetsMsgWrites: Writes[NoMoreBetsMessage] = (noMoreBetsMsg: NoMoreBetsMessage) => Json.obj(
    "MessageType" -> noMoreBetsMsg.MessageType,
    "TableId" -> noMoreBetsMsg.tableId,
    "destination" -> noMoreBetsMsg.destination,
    "ClientId" -> noMoreBetsMsg.clientId,
    "roundId" -> noMoreBetsMsg.roundId,
    "gameType" -> noMoreBetsMsg.gameType,
    "RoundTripStartTime" -> noMoreBetsMsg.roundTripStartTime,
    "timestamp" -> noMoreBetsMsg.timestamp
  )

  implicit val noMoreBetsMsgReads: Reads[NoMoreBetsMessage] = (
    (JsPath \ "MessageType").read[String] and
      (JsPath \ "TableId").read[String] and
      (JsPath \ "destination").read[String] and
      (JsPath \ "ClientId").read[String] and
      (JsPath \ "roundId").read[Long] and
      (JsPath \ "gameType").read[String] and
      (JsPath \ "RoundTripStartTime").read[Long] and
      (JsPath \ "timestamp").read[String]
    ) (NoMoreBetsMessage.apply _)

  implicit val wonBetsMsgWrites: Writes[WonBetsMessage] = (wonBetsMsg: WonBetsMessage) => Json.obj(
    "MessageType" -> wonBetsMsg.MessageType,
    "destination" -> wonBetsMsg.destination,
    "ClientId" -> wonBetsMsg.clientId,
    "RoundTripStartTime" -> wonBetsMsg.roundTripStartTime,
    "WinningBets" -> wonBetsMsg.winningBets
  )

  implicit val wonBetsMsgReads: Reads[WonBetsMessage] = (
    (JsPath \ "MessageType").read[String] and
      (JsPath \ "destination").read[String] and
      (JsPath \ "ClientId").read[String] and
      (JsPath \ "RoundTripStartTime").read[Long] and
      (JsPath \ "WinningBets").read[Seq[WinBet]]
    ) (WonBetsMessage.apply _)

  implicit val gameResultMsgWrites: Writes[GameResultMessage] = (gameResultMsg: GameResultMessage) => Json.obj(
    "MessageType" -> gameResultMsg.MessageType,
    "TableId" -> gameResultMsg.tableId,
    "destination" -> gameResultMsg.destination,
    "ClientId" -> gameResultMsg.clientId,
    "roundId" -> gameResultMsg.roundId,
    "gameType" -> gameResultMsg.gameType,
    "RoundTripStartTime" -> gameResultMsg.roundTripStartTime,
    "timestamp" -> gameResultMsg.timestamp,
    "Group" -> gameResultMsg.group,
    "ColdNumbers" -> gameResultMsg.coldNumbers,
    "HotNumbers" -> gameResultMsg.hotNumbers,
    "Statistics" -> gameResultMsg.statistics,
    "LastWinners" -> gameResultMsg.lastWinners,
    "GameResults" -> gameResultMsg.gameResults,
    "WinAmount" -> gameResultMsg.winAmount
  )

  implicit val gameResultMsgReads: Reads[GameResultMessage] = (
    (JsPath \ "MessageType").read[String] and
      (JsPath \ "TableId").read[String] and
      (JsPath \ "destination").read[String] and
      (JsPath \ "ClientId").read[String] and
      (JsPath \ "roundId").read[Long] and
      (JsPath \ "gameType").read[String] and
      (JsPath \ "RoundTripStartTime").read[Long] and
      (JsPath \ "timestamp").read[String] and
      (JsPath \ "Group").read[Group] and
      (JsPath \ "ColdNumbers").read[Seq[Int]] and
      (JsPath \ "HotNumbers").read[Seq[Int]] and
      (JsPath \ "Statistics").read[Seq[Stat]] and
      (JsPath \ "LastWinners").read[Seq[Winner]] and
      (JsPath \ "GameResults").read[Seq[Win]] and
      (JsPath \ "WinAmount").read[Double]
    ) (GameResultMessage.apply _)


  implicit val videoStreamDataWrites: Writes[VideoStreamData] = (videoStreamData: VideoStreamData) => Json.obj(
    "videoStreamHtmlIP" -> videoStreamData.videoStreamHtmlIP,
    "videoStreamFlashURL" -> videoStreamData.videoStreamFlashURL,
    "videoStreamHtmlURL" -> videoStreamData.videoStreamHtmlURL,
    "videoStreamFlashIP" -> videoStreamData.videoStreamFlashIP,
  )
  implicit val videoStreamDataReads: Reads[VideoStreamData] = (
    (JsPath \ "videoStreamHtmlIP").read[String] and
      (JsPath \ "videoStreamFlashURL").read[String] and
      (JsPath \ "videoStreamHtmlURL").read[String] and
      (JsPath \ "videoStreamFlashIP").read[String]
    ) (VideoStreamData.apply _)


  implicit val tableLimitReads: Reads[TableLimit] = (
    (JsPath \ "chips").read[Seq[Chip]] and
      (JsPath \ "Min_Bet").read[Int] and
      (JsPath \ "Max_Bet").read[Int] and
      (JsPath \ "Min_SideBet").read[Int] and
      (JsPath \ "Max_SideBet").read[Int] and
      (JsPath \ "Min_StraightUpBet").read[Int] and
      (JsPath \ "Max_StraightUpBet").read[Int] and
      (JsPath \ "Min_SplitBet").read[Int] and
      (JsPath \ "Max_SplitBet").read[Int] and
      (JsPath \ "Min_StreetBet").read[Int] and
      (JsPath \ "Max_StreetBet").read[Int] and
      (JsPath \ "Min_CornerBet").read[Int] and
      (JsPath \ "Max_CornerBet").read[Int] and
      (JsPath \ "Min_LineBet").read[Int] and
      (JsPath \ "Max_LineBet").read[Int] and
      (JsPath \ "Min_Dozen_ColumnBet").read[Int] and
      (JsPath \ "Max_Dozen_ColumnBet").read[Int] and
      (JsPath \ "Min_OutsideBet").read[Int] and
      (JsPath \ "Max_OutsideBet").read[Int]
    ) (TableLimit.apply _)

  implicit val tableLimitWrites: Writes[TableLimit] = (
    (JsPath \ "chips").write[Seq[Chip]] and
      (JsPath \ "Min_Bet").write[Int] and
      (JsPath \ "Max_Bet").write[Int] and
      (JsPath \ "Min_SideBet").write[Int] and
      (JsPath \ "Max_SideBet").write[Int] and
      (JsPath \ "Min_StraightUpBet").write[Int] and
      (JsPath \ "Max_StraightUpBet").write[Int] and
      (JsPath \ "Min_SplitBet").write[Int] and
      (JsPath \ "Max_SplitBet").write[Int] and
      (JsPath \ "Min_StreetBet").write[Int] and
      (JsPath \ "Max_StreetBet").write[Int] and
      (JsPath \ "Min_CornerBet").write[Int] and
      (JsPath \ "Max_CornerBet").write[Int] and
      (JsPath \ "Min_LineBet").write[Int] and
      (JsPath \ "Max_LineBet").write[Int] and
      (JsPath \ "Min_Dozen_ColumnBet").write[Int] and
      (JsPath \ "Max_Dozen_ColumnBet").write[Int] and
      (JsPath \ "Min_OutsideBet").write[Int] and
      (JsPath \ "Max_OutsideBet").write[Int]
    ) (unlift(TableLimit.unapply))

  implicit val terminalHistoryReads: Reads[TerminalHistory] = (
    (JsPath \ "roundId").read[Int] and
      (JsPath \ "lastBalance").read[Double] and
      (JsPath \ "betList").read[Seq[Bet]] and
      (JsPath \ "winningBets").read[Seq[WinBet]] and
      (JsPath \ "totalBet").read[Double] and
      (JsPath \ "winAmount").read[Double] and
      (JsPath \ "currentBalance").read[Double]
    ) (TerminalHistory.apply _)


  implicit val terminalHistoryWrites: Writes[TerminalHistory] = (terminalHistory: TerminalHistory) => Json.obj(
    "roundId" -> terminalHistory.roundId,
    "lastBalance" -> terminalHistory.lastBalance,
    "betList" -> terminalHistory.betList,
    "winningBets" -> terminalHistory.winningBets,
    "totalBet" -> terminalHistory.totalBet,
    "winAmount" -> terminalHistory.winAmount,
    "currentBalance" -> terminalHistory.currentBalance,
  )

  implicit val terminalDataReads: Reads[TerminalData] = (
    (JsPath \ "connected").read[Boolean] and
      (JsPath \ "totalBet").read[Double] and
      (JsPath \ "winAmount").read[Double] and
      (JsPath \ "lastWin").read[Double] and
      (JsPath \ "history").read[Seq[TerminalHistory]] and
      (JsPath \ "betList").read[Seq[Bet]] and
      (JsPath \ "winningBets").read[Seq[WinBet]] and
      (JsPath \ "balance").read[Double]
    ) (TerminalData.apply _)


  implicit val terminalDataWrites: Writes[TerminalData] = (terminalData: TerminalData) => Json.obj(
    "connected" -> terminalData.connected,
    "totalBet" -> terminalData.totalBet,
    "winAmount" -> terminalData.winAmount,
    "lastWin" -> terminalData.lastWin,
    "history" -> terminalData.history,
    "betList" -> terminalData.betList,
    "winningBets" -> terminalData.winningBets,
    "balance" -> terminalData.balance,
  )

  implicit val configDataWrites: Writes[ConfigData] = (o: ConfigData) => Json.obj(
    "tableLimit" -> o.tableLimit,
    "tableId" -> o.tableId,
    "limitId" -> o.limitId,
    "tableName" -> o.tableName,
    "timerTime" -> o.timerTime,
    "showInfoPaper" -> o.showInfoPaper,
    "autoDraw" -> o.autoDraw,
    "autoPlay" -> o.autoPlay,
    "isOppositeBettingAllowed" -> o.isOppositeBettingAllowed,
  )


  implicit val configDataReads: Reads[ConfigData] = (
    (JsPath \ "tableLimit").read[TableLimit] and
      (JsPath \ "tableId").read[String] and
      (JsPath \ "limitId").read[Int] and
      (JsPath \ "tableName").read[String] and
      (JsPath \ "timerTime").read[Int] and
      ((JsPath \ "showInfoPaper").read[Boolean] or Reads.pure(false)) and
      ((JsPath \ "autoDraw").read[Boolean] or Reads.pure(false)) and
      ((JsPath \ "autoPlay").read[Boolean] or Reads.pure(false)) and
      ((JsPath \ "isOppositeBettingAllowed").read[Boolean] or Reads.pure(false))
    ) (ConfigData.apply _)


  implicit val configUpdateMsgWrites: Writes[ConfigUpdateMsg] = (configUpdateMsg: ConfigUpdateMsg) => Json.obj(
    "MessageType" -> configUpdateMsg.MessageType,
    "configData" -> configUpdateMsg.configData,
    "timestamp" -> configUpdateMsg.timestamp,
  )

  implicit val gameDataWrites: Writes[GameData] = (o: GameData) => Json.obj(
    "roundId" -> o.roundId,
    "gameData" -> o.gameData,
    "configData" -> o.configData,
    "licenseData" -> o.licenseData,
    "gameStatus" -> o.gameStatus,
  )

  implicit val gameDataReads: Reads[GameData] = (
    (JsPath \ "roundId").read[Long] and
      (JsPath \ "gameData").read[RouletteGameData] and
      (JsPath \ "configData").read[ConfigData] and
      (JsPath \ "licenseData").read[LicenseData] and
      (JsPath \ "gameStatus").read[String]
    )(GameData.apply _)

}
