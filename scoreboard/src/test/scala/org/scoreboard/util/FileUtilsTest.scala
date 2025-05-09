package org.scoreboard.util

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.scoreboard.mocks.TestTimeProvider
import org.scoreboard.model.Match

import java.io.File
import java.time.LocalDateTime

class FileUtilsTest {

  var tempFile: File = _
  private val now = LocalDateTime.of(2025, 5, 8, 11, 6)
  given timeProvider: TestTimeProvider = new TestTimeProvider(now)
  private val fileUtils: FileUtils = new FileUtils()

  @BeforeEach
  def setUp(): Unit = {
    tempFile = File.createTempFile("scoreboard", ".json")
    tempFile.deleteOnExit()
  }

  @AfterEach
  def tearDown(): Unit = {
    if (tempFile.exists()) tempFile.delete()
  }

  @Test
  def testWriteToFileSuccessfully(): Unit = {
    val match1 = Match("TeamA", "TeamB", 2, 1, timeProvider.now())
    val match2 = Match("TeamC", "TeamD", 3, 2, timeProvider.now().minusHours(1))

    val result = fileUtils.writeToFile(tempFile,Seq(match1, match2))
    assertTrue(result.contains("has been successfully added"))

    val matches = fileUtils.readFromFile(tempFile)
    assertTrue(matches.contains(match1))
    assertTrue(matches.contains(match2))
  }

  @Test
  def testWriteToFileFailure(): Unit = {
    tempFile.setWritable(false)

    val match1 = Match("TeamA", "TeamB", 2, 1, timeProvider.now())

    val exception = assertThrows(classOf[RuntimeException], () =>
      fileUtils.writeToFile(tempFile, Seq(match1))
    )
    assertTrue(exception.getMessage.contains("Failed to write:"))
  }

  @Test
  def testReadFromFileWhenFileDeleted(): Unit = {
    tempFile.delete()
    val result = fileUtils.readFromFile(tempFile)
    assertTrue(result.isEmpty)
  }

  @Test
  def testReadFromFileWhenEmpty(): Unit = {
    val result = fileUtils.readFromFile(tempFile)
    assertTrue(result.isEmpty)
  }

  @Test
  def testReadFromFileWithValidData(): Unit = {
    val match1 = Match("TeamA", "TeamB", 2, 1, timeProvider.now())
    val match2 = Match("TeamC", "TeamD", 3, 2)

    fileUtils.writeToFile(tempFile, Seq(match1, match2))
    val result = fileUtils.readFromFile(tempFile)

    assertTrue(result.contains(match1))
    assertTrue(result.contains(match2))
  }
}
