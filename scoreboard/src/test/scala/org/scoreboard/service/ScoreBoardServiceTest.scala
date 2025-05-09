package org.scoreboard.service

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.scoreboard.mocks.TestTimeProvider
import org.scoreboard.model.Match
import org.scoreboard.util.FileUtils

import java.io.{ByteArrayOutputStream, File, PrintStream}
import java.time.LocalDateTime

class ScoreBoardServiceTest {

  var tempFile: File = _
  private val now = LocalDateTime.of(2025, 5, 8, 11, 6)
  given timeProvider: TestTimeProvider = new TestTimeProvider(now)
  private val scoreBoardService: ScoreBoardService = new ScoreBoardService()
  private val fileUtils: FileUtils = new FileUtils()


  @BeforeEach
  def setup(): Unit = {
    tempFile = File.createTempFile("scoreboard", ".json")
    tempFile.deleteOnExit()
    fileUtils.writeToFile(tempFile, Seq.empty)
  }

  @AfterEach
  def cleanup(): Unit = {
    if (tempFile.exists()) tempFile.delete()
  }

  @Test
  def testStartMatchSuccess(): Unit = {
    val expected = "Match between home team: TeamA and away team: TeamB, current score: 0-0, last updated time: 2025-05-08 11:06:00. has been successfully added"
    val actual = scoreBoardService.startMatch(tempFile, "TeamA", "TeamB")
    val matches = fileUtils.readFromFile(tempFile)
    assertEquals(1, matches.size)
    assertEquals("TeamA", matches.head.homeTeam)
    assertEquals("TeamB", matches.head.awayTeam)
    assertEquals(expected, actual)

  }

  @Test
  def testStartMatchAlreadyExists(): Unit = {
    val expected = "Match: TeamA - TeamB already exists, please use `update` or `finish` command."
    scoreBoardService.startMatch(tempFile, "TeamA", "TeamB")

    val exception = assertThrows(classOf[IllegalArgumentException], () =>
      scoreBoardService.startMatch(tempFile, "TeamA", "TeamB")
    )
    assertEquals(expected, exception.getMessage)
  }

  @Test
  def testUpdateScore(): Unit = {
    val expected = "Match between home team: TeamA and away team: TeamB, current score: 2-1, last updated time: 2025-05-08 11:06:00.,Match between home team: TeamC and away team: TeamD, current score: 0-0, last updated time: 2025-05-08 11:06:00. has been successfully added"
    scoreBoardService.startMatch(tempFile, "TeamA", "TeamB")
    scoreBoardService.startMatch(tempFile, "TeamC", "TeamD")

    val actual = scoreBoardService.updateScore(tempFile, "TeamA", "TeamB", 2, 1)

    val matches = fileUtils.readFromFile(tempFile)
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
    assertEquals(expected, actual)
  }

  @Test
  def testUpdateScoreThrowsException(): Unit = {
    val expected = "Match: TeamX - TeamY hasn't started yet. If you would like to start it, please use `start` command."
    scoreBoardService.startMatch(tempFile, "TeamC", "TeamD")

    val exception = assertThrows(
      classOf[IllegalArgumentException],
      () => scoreBoardService.updateScore(tempFile, "TeamX", "TeamY", 2, 1)
    )

    assertEquals(expected, exception.getMessage)
  }


  @Test
  def testFinishMatch(): Unit = {
    val expected = "Match has been finished successfully."
    scoreBoardService.startMatch(tempFile, "TeamA", "TeamB")
    val actual = scoreBoardService.finishMatch(tempFile, "TeamA", "TeamB")
    val matches = fileUtils.readFromFile(tempFile)
    assertTrue(matches.isEmpty)
    assertEquals(expected, actual)
  }

  @Test
  def testFinishMatchNotStarted(): Unit = {
    val expected = "Match: TeamA - TeamB hasn't started yet. If you would like to start it, please use `start` command."
    val exception = assertThrows(classOf[IllegalArgumentException], () =>
      scoreBoardService.finishMatch(tempFile, "TeamA", "TeamB")
    )
    assertEquals(expected, exception.getMessage)
  }

  @Test
  def testPrintSummary(): Unit = {
    val earlier = now.minusHours(1)

    val gameOne = Match("Team1", "Team2", 1, 0, earlier)
    val gameTwo = Match("Team3", "Team4", 2, 2, now)
    fileUtils.writeToFile(tempFile, Seq(gameOne, gameTwo))

    val out = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(out)) {
      scoreBoardService.printSummary(tempFile)
    }

    val printed = out.toString.trim
    assertTrue(printed.startsWith("Current Status at 2025-05-08 11:06:00 is:"))
    assertTrue(printed.contains("Team1 1 - 0 Team2"))
    assertTrue(printed.contains("Team3 2 - 2 Team4"))
  }
}
