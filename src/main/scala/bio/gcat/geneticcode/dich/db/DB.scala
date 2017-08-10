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
package bio.gcat.geneticcode.dich.db

import java.io._

/**
 * Serialize an object graph to and from a file.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2012 Markus Gumbel
 */
trait DB[A] {

  /**
   * Saves an object graph to a *.ser file.
   * @param filename Filename without suffix.
   */
  def store(filename: String) {
    val file = filename + ".ser"
    new File(file).delete()
    // Save the object to file
    val out = new ObjectOutputStream(new FileOutputStream(file))
    out.writeObject(this)
    out.close()
  }

  /**
   * Load an object graph from a *.ser file.
   * @param filename Filename without suffix.
   */
  def load(filename: String): A = {
    val file = filename + ".ser"
    val in = new ObjectInputStream(new FileInputStream(file))
    val p = in.readObject().asInstanceOf[A]
    in.close()
    p
  }
}