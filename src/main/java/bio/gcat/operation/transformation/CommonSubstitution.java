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
package bio.gcat.operation.transformation;

import static bio.gcat.Help.*;
import static bio.gcat.Utilities.*;
import static bio.gcat.nucleic.Base.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.log.Logger;
import bio.gcat.nucleic.Acid;
import bio.gcat.nucleic.Base;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;
import com.google.common.collect.ImmutableMap;

@Named(name="common substitution", icon="book_next") @Cataloged(group="Substitute Nucleotide Bases")
@Documented(title="Common Substitution", category={OPERATIONS,TRANSFORMATIONS}, resource="help/operation/transformation/common_substitution.html")
public class CommonSubstitution implements Transformation {
	public static final Map<String,Map<Base,Base>> SUBSTITUTIONS;
	
	private static final Parameter[] PARAMETERS;
	static {
		Map<String,Map<Base,Base>> substitutions = new LinkedHashMap<>();
		
		/* special substitutions */
		substitutions.put("id",ImmutableMap.of(
			ADENINE,ADENINE,
			CYTOSINE,CYTOSINE,
			GUANINE,GUANINE,
			URACIL,URACIL));
		substitutions.put("c",ImmutableMap.of(
			ADENINE,URACIL,
			CYTOSINE,GUANINE,
			GUANINE,CYTOSINE,
			URACIL,ADENINE));
		substitutions.put("p",ImmutableMap.of(
			ADENINE,GUANINE,
			CYTOSINE,URACIL,
			GUANINE,ADENINE,
			URACIL,CYTOSINE));
		substitutions.put("r",ImmutableMap.of(
			ADENINE,CYTOSINE,
			CYTOSINE,ADENINE,
			GUANINE,URACIL,
			URACIL,GUANINE));
		
		substitutions.put("\u03c0CG",ImmutableMap.of(
			ADENINE,ADENINE,
			CYTOSINE,GUANINE,
			GUANINE,CYTOSINE,
			URACIL,URACIL));
		substitutions.put("\u03c0AU",ImmutableMap.of(
			ADENINE,URACIL,
			CYTOSINE,CYTOSINE,
			GUANINE,GUANINE,
			URACIL,ADENINE));
		substitutions.put("\u03c0ACUG",ImmutableMap.of(
			ADENINE,CYTOSINE,
			CYTOSINE,URACIL,
			GUANINE,ADENINE,
			URACIL,GUANINE));
		substitutions.put("\u03c0AGUC",ImmutableMap.of(
			ADENINE,GUANINE,
			CYTOSINE,ADENINE,
			GUANINE,URACIL,
			URACIL,CYTOSINE));
		
		/* normal substitutions */
		substitutions.put("\u03c0AC",ImmutableMap.of(
			ADENINE,CYTOSINE,
			CYTOSINE,ADENINE,
			GUANINE,GUANINE,
			URACIL,URACIL));
		substitutions.put("\u03c0AG",ImmutableMap.of(
			ADENINE,GUANINE,
			CYTOSINE,CYTOSINE,
			GUANINE,ADENINE,
			URACIL,URACIL));
		substitutions.put("\u03c0UG",ImmutableMap.of(
			ADENINE,ADENINE,
			CYTOSINE,CYTOSINE,
			GUANINE,URACIL,
			URACIL,GUANINE));
		substitutions.put("\u03c0UC",ImmutableMap.of(
			ADENINE,ADENINE,
			CYTOSINE,URACIL,
			GUANINE,GUANINE,
			URACIL,CYTOSINE));
		substitutions.put("\u03c0AUCG",ImmutableMap.of(
			ADENINE,URACIL,
			CYTOSINE,GUANINE,
			GUANINE,ADENINE,
			URACIL,CYTOSINE));
		substitutions.put("\u03c0AUGC",ImmutableMap.of(
			ADENINE,URACIL,
			CYTOSINE,ADENINE,
			GUANINE,CYTOSINE,
			URACIL,GUANINE));
		substitutions.put("\u03c0UACG",ImmutableMap.of(
			ADENINE,CYTOSINE,
			CYTOSINE,GUANINE,
			GUANINE,URACIL,
			URACIL,ADENINE));
		substitutions.put("\u03c0UAGC",ImmutableMap.of(
			ADENINE,GUANINE,
			CYTOSINE,URACIL,
			GUANINE,CYTOSINE,
			URACIL,ADENINE));
		substitutions.put("\u03c0AUC",ImmutableMap.of(
			ADENINE,URACIL,
			CYTOSINE,ADENINE,
			GUANINE,GUANINE,
			URACIL,CYTOSINE));
		substitutions.put("\u03c0UAC",ImmutableMap.of(
			ADENINE,CYTOSINE,
			CYTOSINE,URACIL,
			GUANINE,GUANINE,
			URACIL,ADENINE));
		substitutions.put("\u03c0AUG",ImmutableMap.of(
			ADENINE,URACIL,
			CYTOSINE,CYTOSINE,
			GUANINE,ADENINE,
			URACIL,GUANINE));
		substitutions.put("\u03c0UAG",ImmutableMap.of(
			ADENINE,GUANINE,
			CYTOSINE,CYTOSINE,
			GUANINE,URACIL,
			URACIL,ADENINE));
		substitutions.put("\u03c0GUC",ImmutableMap.of(
			ADENINE,ADENINE,
			CYTOSINE,GUANINE,
			GUANINE,URACIL,
			URACIL,CYTOSINE));
		substitutions.put("\u03c0UGC",ImmutableMap.of(
			ADENINE,ADENINE,
			CYTOSINE,URACIL,
			GUANINE,CYTOSINE,
			URACIL,GUANINE));
		substitutions.put("\u03c0AGC",ImmutableMap.of(
			ADENINE,GUANINE,
			CYTOSINE,ADENINE,
			GUANINE,CYTOSINE,
			URACIL,URACIL));
		substitutions.put("\u03c0GAC",ImmutableMap.of(
			ADENINE,CYTOSINE,
			CYTOSINE,GUANINE,
			GUANINE,ADENINE,
			URACIL,URACIL));
		
		SUBSTITUTIONS = Collections.unmodifiableMap(substitutions);
		PARAMETERS = new Parameter[] {
			new Parameter("substituion", "Substituion", SUBSTITUTIONS.keySet().toArray()),
		};
	}
	
	public static Parameter[] getParameters() { return PARAMETERS; }

	@Override public Collection<Tuple> transform(Collection<Tuple> tuples,Object... values) { return transform(tuples,(String)values[0]); }
	public Collection<Tuple> transform(Collection<Tuple> tuples,String name) {
		Logger logger = getLogger();
		
		Acid acid;
		if((acid=Tuple.tuplesAcid(tuples))==null) {
			logger.log("Tuples with variable acids, can't transform.");
			return tuples; //tuples not all in same acid
		}
		
		Map<Base,Base> substitution = SUBSTITUTIONS.get(name);
		return substitution!=null?tuples.stream().map(tuple->
			new Tuple(substitute(tuple.toAcid(Acid.RNA).getBases(),substitution)).toAcid(acid))
				.collect(Collectors.toList()):tuples;
	}
}
