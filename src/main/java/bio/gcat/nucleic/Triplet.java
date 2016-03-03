/*
 * Copyright [2016] [Mannheim University of Applied Sciences]
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
package bio.gcat.nucleic;

public class Triplet extends Tuple {
	public Triplet(String string) {
		super(string);
		if(string.length()!=3)
			throw new IllegalArgumentException("A base triplet must consist of 3 nuclebases.");
	}
	public Triplet(Base[] bases) {
		super(bases);
		if(bases.length!=3)
			throw new IllegalArgumentException("A base triplet must consist of 3 nuclebases.");
	}
}
