package com.bk.nlp

import java.util.Properties

import com.bk.nlp.model.{Entities, NamedEntity}
import com.typesafe.scalalogging.Logger
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}

import scala.collection.JavaConverters._
import scala.io.{BufferedSource, Source}
import java.io.{FileNotFoundException, IOException}
import java.io.File
import java.io.PrintWriter

object EntityRecognition {

  val logger = Logger("EntityRecognition")
  val allowableTags: Set[String] = Set(Entities.PERSON, Entities.LOCATION, Entities.ORGANIZATION) map {
    _.name
  }

  def main(args: Array[String]): Unit = {
    var pathToProcess:String = ""
    if (args.length > 0) {
      pathToProcess = args(0)
    } else {
      pathToProcess = "/opt/octo/"
    }

    try {

      processFile(new File(pathToProcess), new File("/opt/output"))

    } catch {
      case e: FileNotFoundException => println("Couldn't find that file.")
      case e: IOException => println("Got an IOException!")
    }
  }

  def processFile(pathToProcess: File, pathToOutput: File): Unit = {

    val output:PrintWriter = new PrintWriter(pathToOutput + "/output.txt")

    for( file <- FileRoutines.listAllFiles(pathToProcess)) {
      val bufFile: BufferedSource = Source.fromFile(file)
      for (lines <- bufFile.getLines()) {

        extractEntities(lines).foreach(p => output.write(p.text + ", " + p.tag))
      }
      bufFile.close()
      output.flush()
    }
  }

  def extractEntities(text: String):List[NamedEntity] = {
    val props = new Properties
    props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, entitymentions")
    val pipeline = new StanfordCoreNLP(props)
    val document = new Annotation(text)

    pipeline.annotate(document)

    val sentences = document.get(classOf[SentencesAnnotation]).asScala.toList
    sentences.flatMap {
      sentence =>
        sentence.get(classOf[MentionsAnnotation]).asScala.toList.map {
          token => NamedEntity(token.get(classOf[TextAnnotation]), token.get(classOf[NamedEntityTagAnnotation]))
        }
    }.filter { entity => allowableTags.contains(entity.tag) }
  }




}

