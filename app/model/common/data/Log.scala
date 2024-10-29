package model.common.data

case class Log(logType: String,
                file: String,
               str: String,
               timestamp: String,
              ){
  override def toString: String = s"$timestamp $logType [$file] $str"
}
