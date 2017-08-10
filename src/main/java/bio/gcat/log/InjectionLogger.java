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
package bio.gcat.log;

import java.util.Map;
import java.util.WeakHashMap;

public class InjectionLogger implements Logger {
	private final static Map<Object,Logger> injections = new WeakHashMap<Object,Logger>();
	
	private Logger logger;
	
	public void injectLogger(Logger logger) { this.logger = logger; }
	
	public static <T> T injectLogger(Logger logger, T object) {
		if(logger!=null&&object instanceof InjectionLogger.Injectable)
			((InjectionLogger)((InjectionLogger.Injectable)object).getLogger()).injectLogger(logger);
		return object;
	}
	
	@Override public void log(String format,Object... arguments) {
		if(this.logger!=null) logger.log(format,arguments);
	}
	@Override public void log(String message,Throwable throwable) {
		if(this.logger!=null) logger.log(message,throwable);
	}
	
	public interface Injectable {
		default public Logger getLogger() {
			Logger logger = injections.getOrDefault(this, new InjectionLogger());
			injections.put(this,logger);
			return logger;
		}
	}
}