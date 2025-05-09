package org.scoreboard.util

import java.time.format.DateTimeFormatter

object DateUtils {
  private final val dateTimeFormat = "yyyy-MM-dd HH:mm:ss"

  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat)
}
