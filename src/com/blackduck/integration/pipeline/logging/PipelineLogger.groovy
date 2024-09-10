package com.blackduck.integration.pipeline.logging

abstract class PipelineLogger implements Serializable {
    abstract void alwaysLog(String txt);

    abstract void info(String txt);

    abstract void error(Throwable t);

    abstract void error(String txt, Throwable t);

    abstract void error(String txt);

    abstract void warn(String txt);

    abstract void trace(String txt);

    abstract void trace(String txt, Throwable t);

    abstract void debug(String txt);

    abstract void debug(String txt, Throwable t);

    abstract void setLogLevel(LogLevel logLevel);

    abstract LogLevel getLogLevel();

}
