package org.scoreboard.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

import java.time.LocalDateTime

@JsonPropertyOrder(Array("homeTeam", "awayTeam", "homeScore", "awayScore", "timestamp"))
case class Match(homeTeam: String, awayTeam: String, homeScore: Int = 0, awayScore: Int = 0, timestamp: LocalDateTime = LocalDateTime.now()) {
  override def toString : String = {
    s"Match between home team: $homeTeam and away team:$awayTeam, current score: $homeScore-$awayScore, time: $timestamp."
  }
}



