package model.common.messages

import model.common.data.{Player, ServerLog, Table}

case class InitialDataCageMsg(MessageType: String = "InitialData",
                              tables: Seq[Table] = Seq.empty[Table],
                              logs: Seq[ServerLog] = Seq.empty[ServerLog],
                              players: Seq[Player] = Seq.empty[Player],
                              moneyTransactions: Seq[MoneyTransactionMsg] = Seq.empty[MoneyTransactionMsg],
                              gameTransactions: Seq[GameTransactionMsg] = Seq.empty[GameTransactionMsg],
                              roundTransactions: Seq[RoundTransactionMsg] = Seq.empty[RoundTransactionMsg],
                              operations: Seq[OperationTransactionMsg] = Seq.empty[OperationTransactionMsg]
                             )