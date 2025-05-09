package org.scoreboard.service

import org.scoreboard.model.Match
import org.scoreboard.util.DateUtils.dateTimeFormatter
import org.scoreboard.util.{FileUtils, TimeProvider}

import java.io.File

class ScoreBoardService(using time: TimeProvider) {

  private val fileUtils: FileUtils = new FileUtils()

  def startMatch(jsonFile: File, home: String, away: String): String = {
    val matches = fileUtils.readFromFile(jsonFile)
    if (matches.exists(game => game.homeTeam == home && game.awayTeam == away)) {
      throw new IllegalArgumentException(s"Match: $home - $away already exists, please use `update` or `finish` command.")
    } else {
      println(s"Adding $home - $away match to the board...")
      val message = fileUtils.writeToFile(jsonFile, matches :+ Match(homeTeam = home, awayTeam = away))
      println(message)
      message
    }
  }

  def updateScore(jsonFile: File, home: String, away: String, homeScore: Int, awayScore: Int): String = {
    val matches = fileUtils.readFromFile(jsonFile)

    val updatedMatches: Map[Match, Option[Match]] = matches.map { game =>
      if (game.homeTeam == home && game.awayTeam == away) {
        game -> Some(Match(
          homeTeam = home,
          awayTeam = away,
          homeScore = homeScore,
          awayScore = awayScore,
          timestamp = time.now()))
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

      val message = fileUtils.writeToFile(jsonFile, result)
      println(message)
      message
    }

  }

  def finishMatch(jsonFile: File, home: String, away: String): String = {
    val matches = fileUtils.readFromFile(jsonFile)
    if (!matches.exists(game => game.homeTeam == home && game.awayTeam == away))
      throw new IllegalArgumentException(getStartMessage(home, away))
    else {
      val finishedGame = matches.filter(m => m.homeTeam == home && m.awayTeam == away)
      println(s"You are about to finish: ${finishedGame.mkString(",")}")
      val updated = matches.filterNot(m => m.homeTeam == home && m.awayTeam == away)
      fileUtils.writeToFile(jsonFile, updated)}
      val message = s"Match has been finished successfully."
      println(message)
      message
  }

  def printSummary(jsonFile: File): Unit = {
    val matches = fileUtils.readFromFile(jsonFile)
    val sorted = matches.sortBy(_.timestamp)
    val now = time.now()
    println(s"Current Status at ${now.format(dateTimeFormatter)} is:")
    sorted.foreach { game =>
      println(s"${game.homeTeam} ${game.homeScore} - ${game.awayScore} ${game.awayTeam}")
    }
  }

  private def getStartMessage(home: String, away: String): String = {
    s"Match: $home - $away hasn't started yet. If you would like to start it, please use `start` command."
  }
}
