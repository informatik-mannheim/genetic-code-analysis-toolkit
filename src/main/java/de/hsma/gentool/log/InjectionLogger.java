package de.hsma.gentool.log;

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
		public default Logger getLogger() {
			Logger logger = injections.getOrDefault(this, new InjectionLogger());
			injections.put(this,logger);
			return logger;
		}
	}
}