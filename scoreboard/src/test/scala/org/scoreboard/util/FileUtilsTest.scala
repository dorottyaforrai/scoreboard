package org.scoreboard.util

import org.junit.jupiter.api._
import org.junit.jupiter.api.Assertions._
import org.scoreboard.model.Match

import java.io.File
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileUtilsTest {

  var tempFile: File = _

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
    val match1 = Match("TeamA", "TeamB", 2, 1)
    val match2 = Match("TeamC", "TeamD", 3, 2)

    val result = FileUtils.writeToFile(tempFile,Seq(match1, match2))
    assertTrue(result.contains("has been successfully added"))

    val matches = FileUtils.readFromFile(tempFile)
    assertTrue(matches.contains(match1))
    assertTrue(matches.contains(match2))
  }

  @Test
  def testWriteToFileFailure(): Unit = {
    tempFile.setWritable(false)

    val match1 = Match("TeamA", "TeamB", 2, 1)
    val result = FileUtils.writeToFile(tempFile, Seq(match1))

    assertTrue(result.contains("Failed to write"))
  }

  @Test
  def testReadFromFileWhenFileDeleted(): Unit = {
    tempFile.delete()
    val result = FileUtils.readFromFile(tempFile)
    assertTrue(result.isEmpty)
  }

  @Test
  def testReadFromFileWhenEmpty(): Unit = {
    val result = FileUtils.readFromFile(tempFile)
    assertTrue(result.isEmpty)
  }

  @Test
  def testReadFromFileWithValidData(): Unit = {
    val match1 = Match("TeamA", "TeamB", 2, 1, LocalDateTime.of(2025, 5, 8, 11, 6))
    val match2 = Match("TeamC", "TeamD", 3, 2)

    FileUtils.writeToFile(tempFile, Seq(match1, match2))
    val result = FileUtils.readFromFile(tempFile)

    assertTrue(result.contains(match1))
    assertTrue(result.contains(match2))
  }
}
