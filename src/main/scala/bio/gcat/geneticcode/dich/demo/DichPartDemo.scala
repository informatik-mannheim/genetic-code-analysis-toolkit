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
package bio.gcat.geneticcode.dich.demo

import bio.gcat.geneticcode.dich.ct.ClassTable
import bio.gcat.geneticcode.dich.scan.ErrorScan
import bio.gcat.geneticcode.dich._
import org.junit.{Ignore, Test}

/**
 * @author Markus Gumbel (m.gumbel@hs-mannheim.de)
 *         (c) 2013 Markus Gumbel
 */
class DichPartDemo {

  @Test
  def rumerDich {
    val rumerDich = new DichProperty("Rumer") {

      def dich0(c: Compound) = RumerMapping.dich(c)

      def dich1(c: Compound) = RumerMapping.dich(c)

      def dich2(c: Compound) = RumerMapping.dich(c)
    }
    val ct1 = new ClassTable(List(RumerBDA), IUPAC.STANDARD, rumerDich)
    println(ct1.mkString)
  }

  @Test
  @Ignore
  def dich {
    printDich(DichPartitionRumer)
    printDich {
      new DichPartition {
        def dich0(c: Compound) = RumerMapping.dich(c)

        def dich1(c: Compound) = IdMapping.dich(c)

        def dich2(c: Compound) = IdMapping.dich(c)
      }
    }
  }

  def printDich(p: DichPartition) {
    println("Is dich. partition? " + p.isDichPartition)
    println(p.mkString)
  }
}
