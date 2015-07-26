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
package de.hsma.gentool;

import java.util.prefs.Preferences;

public class Option extends Parameter {
	public Option(String key,String label,Type type,Object value) {
		super(key,label,type,value);
	}
	public Option(String key,String label,double value,double minimum,double maximum,double step) {
		super(key,label,minimum,value,maximum,step);
	}
	public Option(String key,String label,int value,int minimum,int maximum,int step) {
		super(key,label,minimum,value,maximum,step);
	}
	public Option(String key,String label,Object value,Object[] options,String... labels) {
		super(key,label,value,options,labels);
	}
	
	public Object readPreference(Preferences preferences) {
		switch(type) {
		case BOOLEAN: return preferences.getBoolean(key,(Boolean)value);
		case DECIMAL: return preferences.getDouble(key,(Double)value);
		case NUMBER: return preferences.getInt(key,(Integer)value);
		default: return preferences.get(key,value.toString()); }
	}
	
	public void storePreference(Preferences preferences,Object value) {
		if(type.checkType(value)) switch(type) {
			case BOOLEAN: preferences.putBoolean(key,(Boolean)value); break;
			case DECIMAL: preferences.putDouble(key,((Number)value).doubleValue()); break;
			case NUMBER: preferences.putInt(key,((Number)value).intValue()); break;
			default: preferences.put(key,value.toString()); }
	}
}