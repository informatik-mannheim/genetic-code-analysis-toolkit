/*
 * Copyright [2014] [Mannheim University of Applied Sciences]
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
package bio.gcat.gui.editor;

import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import bio.gcat.nucleic.Tuple;

public interface NucleicListener extends EventListener {
	public void tuplesInsert(NucleicEvent event);
	public void tuplesRemoved(NucleicEvent event);
	public void tuplesUndoableChange(NucleicEvent event);
	
	public void optionsChange(NucleicEvent event);
	
	public static class NucleicEvent extends EventObject {
		private static final long serialVersionUID = 1l;
		
		private Collection<Tuple> tuples;
		private NucleicOptions options,oldOptions;
		
		public NucleicEvent(Object source, Collection<Tuple> tuples) {
			super(source);
			this.tuples = tuples;
		}
		public NucleicEvent(Object source, NucleicOptions options) {
			super(source);
			this.options = options;
		}
		public NucleicEvent(Object source, NucleicOptions options, NucleicOptions oldOptions) {
			this(source,options);
			this.oldOptions = oldOptions;
		}
		
		public Collection<Tuple> getTuples() { return tuples; }
		public NucleicOptions getOptions() { return options; }
		public NucleicOptions getOldOptions() { return oldOptions; }
	}
}