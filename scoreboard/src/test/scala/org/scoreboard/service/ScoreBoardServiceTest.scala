package org.scoreboard.service

import org.junit.jupiter.api._
import org.junit.jupiter.api.Assertions._
import org.scoreboard.model.Match
import org.scoreboard.util.FileUtils

import java.io.{ByteArrayOutputStream, File, PrintStream}
import java.time.LocalDateTime

class ScoreBoardServiceTest {

  var tempFile: File = _
  private val now = LocalDateTime.of(2025, 5, 8, 11, 6)

  @BeforeEach
  def setup(): Unit = {
    tempFile = File.createTempFile("scoreboard", ".json")
    tempFile.deleteOnExit()
    FileUtils.writeToFile(tempFile, Seq.empty)
  }

  @AfterEach
  def cleanup(): Unit = {
    if (tempFile.exists()) tempFile.delete()
  }

  @Test
  def testStartMatchSuccess(): Unit = {
    ScoreBoardService.startMatch(tempFile, "TeamA", "TeamB")
    val matches = FileUtils.readFromFile(tempFile)
    assertEquals(1, matches.size)
    assertEquals("TeamA", matches.head.homeTeam)
    assertEquals("TeamB", matches.head.awayTeam)
  }

  @Test
  def testStartMatchAlreadyExists(): Unit = {
    ScoreBoardService.startMatch(tempFile, "TeamA", "TeamB")
    val exception = assertThrows(classOf[IllegalArgumentException], () =>
      ScoreBoardService.startMatch(tempFile, "TeamA", "TeamB")
    )
    assertTrue(exception.getMessage.contains("already exists"))
  }

  @Test
  def testUpdateScore(): Unit = {
    ScoreBoardService.startMatch(tempFile, "TeamA", "TeamB")
    ScoreBoardService.startMatch(tempFile, "TeamC", "TeamD")

    ScoreBoardService.updateScore(tempFile, "TeamA", "TeamB", 2, 1, now)

    val matches = FileUtils.readFromFile(tempFile)
    assertEquals(2, matches.size)

    val maybeUpdated = matches.find(m => m.homeTeam == "TeamA" && m.awayTeam == "TeamB").get
    val maybeUntouched = matches.find(m => m.homeTeam == "TeamC" && m.awayTeam == "TeamD").get

    assertEquals(2, maybeUpdated.homeScore)
    assertEquals(1, maybeUpdated.awayScore)
    assertEquals(now, maybeUpdated.timestamp)

    assertEquals("TeamC", maybeUntouched.homeTeam)
    assertEquals("TeamD", maybeUntouched.awayTeam)
    assertEquals(0, maybeUntouched.homeScore)
    assertEquals(0, maybeUntouched.awayScore)
  }

  @Test
  def testUpdateScoreThrowsException(): Unit = {
    ScoreBoardService.startMatch(tempFile, "TeamC", "TeamD")

    val thrown = assertThrows(
      classOf[IllegalArgumentException],
      () => ScoreBoardService.updateScore(tempFile, "TeamX", "TeamY", 2, 1, now)
    )

    assertTrue(thrown.getMessage.contains("Match: TeamX - TeamY hasn't started yet"))
  }


  @Test
  def testFinishMatch(): Unit = {
    ScoreBoardService.startMatch(tempFile, "TeamA", "TeamB")
    ScoreBoardService.finishMatch(tempFile, "TeamA", "TeamB")
    val matches = FileUtils.readFromFile(tempFile)
    assertTrue(matches.isEmpty)
  }

  @Test
  def testFinishMatchNotStarted(): Unit = {
    val ex = assertThrows(classOf[IllegalArgumentException], () =>
      ScoreBoardService.finishMatch(tempFile, "TeamA", "TeamB")
    )
    assertTrue(ex.getMessage.contains("hasn't started yet"))
  }

  @Test
  def testPrintSummary(): Unit = {
    val now = LocalDateTime.now()
    val earlier = now.minusHours(1)

    val m1 = Match("Team1", "Team2", 1, 0, earlier)
    val m2 = Match("Team3", "Team4", 2, 2, now)
    FileUtils.writeToFile(tempFile, Seq(m2, m1))

    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      ScoreBoardService.printSummary(tempFile)
    }

    val printed = out.toString.trim
    assertTrue(printed.startsWith("Team1 1 - 0 Team2"))
    assertTrue(printed.contains("Team3 2 - 2 Team4"))
  }
}
