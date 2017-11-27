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

    val dirs = subdirs(new File(pathToProcess))

    val output = new PrintWriter("outputs/" + "entities_" + scala.util.Random.alphanumeric.take(5).mkString + ".txt" )
    output.println(":processing dataset for entity extraction " + pathToProcess)

    try {

      if (dirs.isEmpty) {
        for (file <- getListOfFiles(new File(pathToProcess))) {
          extractEntitiesFromFile(file, output)
        }
      } else {
        for (dir <- dirs) {
          for (file <- getListOfFiles(dir)) {
            extractEntitiesFromFile(file, output)
          }
        }
      }

    } catch {
      case e: FileNotFoundException => println("Couldn't find that file.")
      case e: IOException => println("Got an IOException!")
    } finally {
      output.close()
    }
  }

  def extractEntitiesFromFile(file: File, output: PrintWriter): Unit = {
    val bufFile: BufferedSource = Source.fromFile(file)
    for (lines <- bufFile.getLines()) {

      extractEntities(lines).foreach(p => output.println(p.text + ", " + p.tag))

    }
    bufFile.close()
  }

  def subdirs(dir: File): Iterator[File] = {
    val children = dir.listFiles.filter(_.isDirectory)
    children.toIterator ++ children.toIterator.flatMap(subdirs _)
  }

  def getListOfFiles(dir: File): List[File] = {
    if (dir.exists() && dir.isDirectory()) {
      dir.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
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

