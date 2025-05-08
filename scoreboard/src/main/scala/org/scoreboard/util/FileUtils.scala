package org.scoreboard.util

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scoreboard.model.Match

import java.io.File
import scala.util.{Failure, Success, Try}

object FileUtils {

  //private val jsonFile: File = new File("scoreboard.json")

  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  mapper.registerModule(new JavaTimeModule())
  mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

  def readFromFile(jsonFile: File): Seq[Match] = {
    if (!jsonFile.exists() || jsonFile.length() == 0) return Seq.empty
    mapper.readValue(jsonFile, new TypeReference[Seq[Match]] {})
  }

  def writeToFile(jsonFile: File, matches: Seq[Match]): String = {
    val writer = mapper.writerFor(classOf[Seq[Match]])
    Try {
      writer.withDefaultPrettyPrinter().writeValue(jsonFile, matches)
    } match {
      case Success(_) => s"${matches.mkString(",")} has been successfully added to ${jsonFile.getAbsolutePath}"
      case Failure(exception) => s"Failed to write: ${matches.mkString(",")} into ${jsonFile.getAbsolutePath}: ${exception.getMessage}"
    }
  }

}
