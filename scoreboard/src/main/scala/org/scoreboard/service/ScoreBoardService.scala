package org.scoreboard.service

import org.scoreboard.model.Match
import org.scoreboard.util.FileUtils.{readFromFile, writeToFile}

import java.io.File
import java.time.LocalDateTime

object ScoreBoardService {

  def startMatch(jsonFile: File, home: String, away: String): Unit = {
    val matches = readFromFile(jsonFile)
    if (matches.exists(game => game.homeTeam == home && game.awayTeam == away))
      throw new IllegalArgumentException(s"Match: $home - $away already exists, please use `update` command.")
    writeToFile(jsonFile, matches :+ Match(homeTeam = home, awayTeam = away))
  }

  def updateScore(jsonFile: File, home: String, away: String, homeScore: Int, awayScore: Int, currentTime: LocalDateTime): String = {
    val matches = readFromFile(jsonFile)

    val updatedMatches: Map[Match, Option[Match]] = matches.map { game =>
      if (game.homeTeam == home && game.awayTeam == away) {
        game -> Some(Match(
          homeTeam = home,
          awayTeam = away,
          homeScore = homeScore,
          awayScore = awayScore,
          timestamp = currentTime))
    } else {
      game -> None
    }
  }.toMap

    if(updatedMatches.values.forall(_.isEmpty)){
      throw new IllegalArgumentException(getStartMessage(home, away))
    }else{

    val result: Seq[Match] = updatedMatches.map {
      case (_, Some(updated)) => updated
      case (game, None) => game
    }.toSeq

      writeToFile(jsonFile, result)
    }

  }

  def finishMatch(jsonFile: File, home: String, away: String): Unit = {
    val matches = readFromFile(jsonFile)
    if (!matches.exists(game => game.homeTeam == home && game.awayTeam == away))
      throw new IllegalArgumentException(getStartMessage(home, away))
    else {
      val updated = matches.filterNot(m => m.homeTeam == home && m.awayTeam == away)
    writeToFile(jsonFile, updated)}
  }

  def printSummary(jsonFile: File): Unit = {
    val matches = readFromFile(jsonFile)
    val sorted = matches.sortBy(_.timestamp)
    sorted.foreach { game =>
      println(s"${game.homeTeam} ${game.homeScore} - ${game.awayScore} ${game.awayTeam}")
    }
  }

  private def getStartMessage(home: String, away: String): String = {
    s"Match: $home - $away hasn't started yet. If you would like to start it, please use `start` command."
  }
}
