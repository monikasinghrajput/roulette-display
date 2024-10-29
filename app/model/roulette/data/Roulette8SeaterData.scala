package model.roulette.data

import model.common.data.ServerLog

case class Roulette8SeaterData(wheel: WheelData,
                               seats: Seq[Seat],
                               game: RouletteGameData,
                               logs: Seq[ServerLog] = Seq.empty[ServerLog])

