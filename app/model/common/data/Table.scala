package model.common.data

case class Table(tableId: String,
                 gameName: String,
                 autoPlay: Boolean,
                 pybTimer: Int,
                 cardDrawTimer: Int,
                 gameResultTimer: Int
                )
