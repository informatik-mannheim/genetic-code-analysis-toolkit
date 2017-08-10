/*
 * Copyright [2017] [Mannheim University of Applied Sciences]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package bio.gcat.util

import java.util.Properties

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
object Version {

  def label = "GCAT B(D)A Scanner (" + version + ")"

  def version = {
    val path = "/bio/gcat/version.properties"
    val stream = getClass().getResourceAsStream(path)
    if (stream == null) "UNKNOWN"
    val props = new Properties()
    try {
      props.load(stream)
      stream.close()
      props.get("version")
    } catch {
      case _ : Throwable => "UNKNOWN"
    }
  }

  override def toString = label
}
