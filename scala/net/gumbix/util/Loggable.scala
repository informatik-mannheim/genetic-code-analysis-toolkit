/*
Copyright 2011 the original author or authors.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package net.gumbix.util

import java.util.logging._
import scala.transient

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 */
trait Loggable {

  @transient lazy val log = Loggable.log
}

object Loggable {

  var consoleLog = true
  var fileLog = true

  @transient lazy val log = {
    val l = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
    l.setLevel(Level.FINEST)
    l.setUseParentHandlers(false)
    val consoleHandler = new ConsoleHandler()
    if (consoleLog) {
      consoleHandler.setLevel(Level.INFO)
    } else {
      consoleHandler.setLevel(Level.WARNING)
    }
    consoleHandler.setFormatter(new MyConsoleFormatter())
    l.addHandler(consoleHandler)

    if (fileLog) {
      val fileTxt = new FileHandler("logging.txt")
      fileTxt.setLevel(Level.FINEST)
      fileTxt.setFormatter(new MyConsoleFormatter())
      l.addHandler(fileTxt)
    }
    l
  }
}

class MyConsoleFormatter extends Formatter {
  def format(rec: LogRecord) = {
    rec.getMessage + "\n"
  }
}