package org.scoreboard.mocks

import org.scoreboard.util.TimeProvider

import java.time.LocalDateTime

class TestTimeProvider(time: LocalDateTime) extends TimeProvider {

  override def now(): LocalDateTime = time
}
