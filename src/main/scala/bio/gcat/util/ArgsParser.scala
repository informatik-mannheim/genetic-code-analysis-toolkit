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

import bio.gcat.geneticcode.dich._

/**
 * Very simple but useful args parser.
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
trait ArgsParser {

  def toBDAList(args: Array[String], pos: Int) = args(pos) match {
    case "()" => List()
    case _ => {
      args(pos).map {
        c => c match {
          case 'R' => RumerBDA
          case 'P' => ParityBDA
          case 'A' => AntiCodonBDA
        }
      }.toList
    }
  }

  def toInt(args: Array[String], pos: Int) = args(pos).toInt

  def toDouble(args: Array[String], pos: Int) = args(pos).toDouble

  def toProp(args: Array[String], pos: Int) = args(pos) match {
    case "64" => IdCodonProperty
    case "AST" => new IdAminoAcidProperty(IUPAC.STANDARD)
    case "AVM" => new IdAminoAcidProperty(IUPAC.VERTEBRATE_MITOCHONDRIAL)
    case _ => new IdAminoAcidProperty(IUPAC.STANDARD)
  }
}
