package org.scoreboard

import org.scoreboard.service.ScoreBoardService
import org.scoreboard.util.SystemTimeProvider

import java.io.File

object Main {

    def main(args: Array[String]): Unit = {
      val jsonFile: File = new File("scoreboard.json")
      given timeProvider: SystemTimeProvider = new SystemTimeProvider()
      val scoreBoardService = new ScoreBoardService()

      args.toList match {
        case "start" :: home :: away :: Nil =>
          scoreBoardService.startMatch(jsonFile, home, away)

        case "update" :: home :: away :: hScore :: aScore :: Nil =>
          scoreBoardService.updateScore(jsonFile, home, away, hScore.toInt, aScore.toInt)

        case "finish" :: home :: away :: Nil =>
          scoreBoardService.finishMatch(jsonFile, home, away)

        case "summary" :: Nil =>
          scoreBoardService.printSummary(jsonFile)

        case _ =>
          println("Usage: start <home> <away> | update <home> <away> <homeScore> <awayScore> | finish <home> <away> | summary")
      }
    }


}
