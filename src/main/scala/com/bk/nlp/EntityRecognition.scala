package com.bk.nlp


import com.bk.nlp.model.{Entities, NamedEntity}
import com.typesafe.scalalogging.Logger
import edu.stanford.nlp.simple.{Document, Sentence}

import scala.collection.JavaConverters._
import scala.collection.mutable

object EntityRecognition {

  val logger = Logger("EntityRecognition")
  val allowableTags: Set[String] = Set(Entities.PERSON, Entities.LOCATION, Entities.ORGANIZATION) map {_.name}

  def extractEntities(text: String): List[NamedEntity] = {
    logger.info("Extracing entities from text....")
    val document: Document = new Document(text)
    document.sentences().asScala.toList.flatMap(sentence => processSentence(sentence))
  }

  def processSentence(sentence: Sentence) = {
    val words: List[String] = sentence.words().asScala.toList
    val tags: Vector[String] = sentence.nerTags().asScala.toVector
    val governers: List[Option[Integer]] = sentence.governors().asScala.toList.map(op => if (op.isPresent) Some(op.get) else None)
    val labels: List[Option[String]] = sentence.incomingDependencyLabels().asScala.toList.map(op => if (op.isPresent) Some(op.get) else None)

    val doNotProcess: mutable.Set[Integer] = mutable.Set[Integer]()

    words.indices.filter(i => allowableTags.contains(tags(i))).map(
      idx => {
        val label: Option[String] = labels(idx)
        if (doNotProcess.contains(idx)) {
          NamedEntity(null, "IGNORE")
        }
        else if (label.isDefined && "compound".equals(label.get)) {
          val governedWordIndex: Integer = governers(idx).get
          doNotProcess.add(governedWordIndex)
          NamedEntity(words(idx) + " " + words(governedWordIndex), tags(idx))
        } else {
          NamedEntity(words(idx), tags(idx))
        }
      }

    ).filter(ne => !"IGNORE".equals(ne.tag))

  }

}

