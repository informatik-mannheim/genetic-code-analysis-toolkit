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
package bio.gcat.geneticcode.dich

import bio.gcat.geneticcode.dich.CodonImplicitDefs._
import scala.collection.mutable

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2016 Markus Gumbel
 */
abstract class DichProperty(val label: String)
  extends CodonProperty with DichPartition {

  def property(c: Codon) = {
    if (h1.contains(c)) "0" else if (h2.contains(c)) "1" else "_"
  }

  override def toString = "Dich. code table classifier (" + label + ")"
}
