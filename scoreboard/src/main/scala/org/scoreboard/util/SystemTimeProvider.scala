package org.scoreboard.util

import java.time.LocalDateTime

class SystemTimeProvider extends TimeProvider {
  override def now(): LocalDateTime = LocalDateTime.now()
}
