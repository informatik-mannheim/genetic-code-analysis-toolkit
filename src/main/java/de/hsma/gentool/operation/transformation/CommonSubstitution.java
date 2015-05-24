package de.hsma.gentool.operation.transformation;

import static de.hsma.gentool.Utilities.*;
import static de.hsma.gentool.nucleic.Base.*;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.hsma.gentool.Parameter;
import de.hsma.gentool.nucleic.Base;
import de.hsma.gentool.nucleic.Tuple;
import de.hsma.gentool.operation.Cataloged;
import de.hsma.gentool.operation.Named;

@Named(name="common substitution") @Cataloged(group="Transformations")
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
		Map<Base,Base> substitution = SUBSTITUTIONS.get(name);
		return substitution!=null?Lists.newArrayList(Iterables.transform(tuples,new Function<Tuple,Tuple>() {
			@Override public Tuple apply(Tuple tuple) { return new Tuple(substitute(tuple.getBases(),substitution)); }
		})):tuples;
	}
}
