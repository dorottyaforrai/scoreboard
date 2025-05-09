package org.scoreboard.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.scoreboard.util.DateUtils.dateTimeFormatter
import org.scoreboard.util.{SystemTimeProvider, TimeProvider}

import java.time.LocalDateTime

@JsonPropertyOrder(Array("homeTeam", "awayTeam", "homeScore", "awayScore", "timestamp"))
case class Match(homeTeam: String, awayTeam: String, homeScore: Int, awayScore: Int, timestamp: LocalDateTime) {
  override def toString : String = {
    s"Match between home team: $homeTeam and away team: $awayTeam, current score: $homeScore-$awayScore, last updated time: ${timestamp.format(dateTimeFormatter)}."
  }
}

object Match {
  def apply(homeTeam: String, awayTeam: String, homeScore: Int = 0, awayScore: Int = 0)(implicit timeProvider: TimeProvider): Match = {
    Match(homeTeam, awayTeam, homeScore, awayScore, timeProvider.now())
  }
}



