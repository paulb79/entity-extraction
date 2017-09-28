package com.bk.nlp.model

object Entities  {
  sealed abstract class Classification(val name: String)
  case object PERSON extends Classification("PERSON")
  case object LOCATION extends Classification("LOCATION")
  case object ORGANIZATION extends Classification("ORGANIZATION")
}