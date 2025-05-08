package org.scoreboard


import org.scoreboard.service.ScoreBoardService

import java.io.File
import java.time.LocalDateTime


object Main {

    def main(args: Array[String]): Unit = {
      val currentTime = LocalDateTime.now()
      val jsonFile: File = new File("scoreboard.json")


      args.toList match {
        case "start" :: home :: away :: Nil =>
          ScoreBoardService.startMatch(jsonFile, home, away)

        case "update" :: home :: away :: hScore :: aScore :: Nil =>
          ScoreBoardService.updateScore(jsonFile, home, away, hScore.toInt, aScore.toInt, currentTime)

        case "finish" :: home :: away :: Nil =>
          ScoreBoardService.finishMatch(jsonFile, home, away)

        case "summary" :: Nil =>
          ScoreBoardService.printSummary(jsonFile)

        case _ =>
          println("Usage: start <home> <away> | update <home> <away> <homeScore> <awayScore> | finish <home> <away> | summary")
      }
    }


}
