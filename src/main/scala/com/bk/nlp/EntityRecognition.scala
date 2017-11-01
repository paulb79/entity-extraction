package com.bk.nlp

import java.util.Properties

import com.bk.nlp.model.{Entities, NamedEntity}
import com.typesafe.scalalogging.Logger
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}

import scala.collection.JavaConverters._
import scala.io.Source
import java.io.{FileNotFoundException, IOException}
import java.io.File

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
      pathToProcess = "/opt/octo"
    }

    val files = getListOfFiles(pathToProcess)

    try {
      for (file <- files) {
        println(s"Processing ${file.toString()} ")
        val bufFile = Source.fromFile(file)
        for(flines <- bufFile.getLines()) {

          extractEntities(flines).foreach(p => println(p.text + " :  [ " + p.tag + " ]"))
        }
        bufFile.close()
      }
    } catch {
      case e: FileNotFoundException => println("Couldn't find that file.")
      case e: IOException => println("Got an IOException!")
    }
  }

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
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

