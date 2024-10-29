package model.roulette.data

case class Payout(StraightUpBet: Int = 35,
                  SplitBet: Int = 17,
                  StreetBet: Int = 11,
                  CornerBet: Int = 8,
                  BasketBet: Int = 6,
                  LineBet: Int = 5,
                  ColumnBet: Int = 2,
                  DozenBet: Int = 2,
                  OutsideBet: Int = 1)
