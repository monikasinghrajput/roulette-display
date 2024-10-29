package model.common.messages

import model.common.data.Table

case class TableUpdatedMsg(MessageType: String = "TableUpdated",
                           table: Table,
                           timestamp: String)
