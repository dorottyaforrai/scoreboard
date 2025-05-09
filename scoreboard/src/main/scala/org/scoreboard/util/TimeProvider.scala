package org.scoreboard.util

import java.time.LocalDateTime

trait TimeProvider {

  def now(): LocalDateTime

}
