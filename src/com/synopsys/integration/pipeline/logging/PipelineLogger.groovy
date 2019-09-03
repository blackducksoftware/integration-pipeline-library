package com.synopsys.integration.pipeline.logging

abstract class PipelineLogger {
    public abstract void alwaysLog(String txt);

    public abstract void info(String txt);

    public abstract void error(Throwable t);

    public abstract void error(String txt, Throwable t);

    public abstract void error(String txt);

    public abstract void warn(String txt);

    public abstract void trace(String txt);

    public abstract void trace(String txt, Throwable t);

    public abstract void debug(String txt);

    public abstract void debug(String txt, Throwable t);

    public abstract void setLogLevel(LogLevel logLevel);
    
    public abstract LogLevel getLogLevel();

}
