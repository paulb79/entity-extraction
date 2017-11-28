package com.bk.nlp

import org.scalatest.{FunSuite, Matchers}
import java.io.File

class FileRoutinesTest extends FunSuite with Matchers {

  test("Open file path with no entries in it") {
    FileRoutines.listAllFiles(new File("")).toList shouldEqual List()
  }

  test("Open file path with folders in it and get expected list including children") {
    val path = "test-data/allen-p/test/"
    FileRoutines.listAllFiles(new File(path)).sorted.toList shouldBe List(new File(path), new File(path + "1."), new File(path + "2."), new File(path + "3."), new File(path + "4."), new File(path + "5."), new File(path + "6."))
  }

}
