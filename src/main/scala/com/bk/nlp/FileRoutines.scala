package com.bk.nlp

import java.io.File

object FileRoutines {

  def listAllFiles(f: File): Stream[File] = {
    if (f.exists())
      f #:: ( if ( f.isDirectory )
              f.listFiles().toStream.flatMap(listAllFiles)
            else Stream.empty )
    else
      Stream.empty
  }

}

