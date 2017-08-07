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
package bio.gcat.geneticcode.core

import org.biojava3.core.sequence.compound.{AminoAcidCompound, AminoAcidCompoundSet}

/**
 * (c) 2012 by Markus Gumbel (m.gumbel@hs-mannheim.de)
 * @author Markus Gumbel
 */

abstract class CodonMapping() extends Serializable {

  def aminoAcid: Option[AminoAcidCompound] = None
  def isStart = false
  def isStop = false

  def toFullString = toString
}

case class AminoAcidMapping(s: String) extends CodonMapping {
  override def aminoAcid = Some(new AminoAcidCompoundSet().getCompoundForString(s))

  override def toString = aminoAcid.get.toString

  override def toFullString = aminoAcid.get.getLongName
}

object StartCodon extends CodonMapping {
  override def isStart = true
  override def toString = ">"
}

object StopCodon extends CodonMapping {
  override def isStop = true
  override def toString = "!"
}

object UnknownCodonMapping extends CodonMapping {
  override def toString = "?"
}
