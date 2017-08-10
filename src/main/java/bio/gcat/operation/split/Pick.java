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
package bio.gcat.operation.split;

import static bio.gcat.Help.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.Utilities;
import bio.gcat.Parameter.Type;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="pick", icon="basket_remove") @Cataloged(group="Split Sequence")
@Parameter.Annotation(key="pattern",label="Split",type=Type.TEXT)
@Parameter.Annotation(key="regex",label="Regex",type=Type.BOOLEAN,value=Utilities.TRUE)
@Documented(title="Pick", category={OPERATIONS,SPLITS}, resource="help/operation/split/pick.html")
public class Pick implements Split {	
 	@Override public List<Collection<Tuple>> split(Collection<Tuple> tuples,Object... values) { return split(tuples, (String)values[0], (Boolean)values[1]); }
	public List<Collection<Tuple>> split(Collection<Tuple> tuples,String pattern,boolean regex) {
		try {
			String string = Tuple.joinTuples(tuples);
			Matcher matcher = Pattern.compile(regex?pattern:Pattern.quote(pattern),Pattern.CASE_INSENSITIVE).matcher(string);
			Collection<Tuple> set = new ArrayList<>(), complement = new ArrayList<>(); int lastEnd = 0;
			while(matcher.find()) {
				set.addAll(Tuple.splitTuples(matcher.group()));
				complement.addAll(Tuple.splitTuples(string.substring(lastEnd,matcher.start())));
				lastEnd = matcher.end();
			}
			complement.addAll(Tuple.splitTuples(string.substring(lastEnd)));
			return Split.asList(set,complement);
		}	catch(PatternSyntaxException e) { return null; }
		/*Pattern patternObject = Pattern.compile(regex?pattern:Pattern.quote(pattern),Pattern.CASE_INSENSITIVE);
		return tuples.stream().collect(Collectors.partitioningBy(tuple->patternObject.matcher(tuple.toString()).matches())).values();*/
	}
}