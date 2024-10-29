package model.common.messages

import model.common.data.Table

case class TableAddedMsg(MessageType: String, table: Table, timestamp: String)
