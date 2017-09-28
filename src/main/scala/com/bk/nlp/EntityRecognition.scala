package com.bk.nlp


import java.util.Properties

import com.bk.nlp.model.{Entities, NamedEntity}
import com.typesafe.scalalogging.Logger
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}

import scala.collection.JavaConverters._

object EntityRecognition {

  val logger = Logger("EntityRecognition")
  val allowableTags: Set[String] = Set(Entities.PERSON, Entities.LOCATION, Entities.ORGANIZATION) map {
    _.name
  }


  def extractEntities(text: String): List[NamedEntity] = {
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

