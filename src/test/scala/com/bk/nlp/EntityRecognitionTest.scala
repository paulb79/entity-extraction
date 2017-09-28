package com.bk.nlp

import com.bk.nlp.model.{Entities, NamedEntity}
import org.scalatest.{FunSuite, Matchers}

class EntityRecognitionTest extends FunSuite with Matchers {

  test("text with no entities in it should return empty list") {
    EntityRecognition.extractEntities("No entities here") shouldBe List()
  }

  test("text with a person with first name only should return a named entity categorised as a person") {
    EntityRecognition.extractEntities("Nobody could grill sausages quite like Dave ever since his experiment with veganism ended in failure") shouldBe
      List(NamedEntity("Dave", Entities.PERSON.name))
  }

  test("text with a person with first and last names should return an entity categorised as a person with both names present") {
    EntityRecognition.extractEntities("Nobody could grill sausages quite like Dave Jones ever since his experiment with veganism ended in failure") shouldBe
      List(NamedEntity("Dave Jones", Entities.PERSON.name))
  }

  test("text with a location should return a named entity categorised as a location") {
    EntityRecognition.extractEntities("Most months the weather in Seville is pleasant") shouldBe
      List(NamedEntity("Seville", Entities.LOCATION.name))
  }


  test("text with an organisation should return a named entity categorised as an organisation") {
    EntityRecognition.extractEntities("Not many people realise the Senate cannot be held responsible for the obesity epidemic") shouldBe
      List(NamedEntity("Senate", Entities.ORGANIZATION.name))
  }


  test("text with a person, location and organisation should return three entities with the correct categorisation") {
    EntityRecognition.extractEntities("Dave knew he would have to answer to the local police over his sausage mishap.  " +
      "Nobody else in Newport West had seen anything like it!  Someone suggested he join the Vegan Society.") should contain theSameElementsAs
      List(NamedEntity("Vegan Society", Entities.ORGANIZATION.name), NamedEntity("Dave", Entities.PERSON.name), NamedEntity("Newport West", Entities.LOCATION.name))
  }

}
