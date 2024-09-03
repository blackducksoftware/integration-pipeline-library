package com.blackduck.integration.pipeline.logging

class SilentPipelineLogger extends PipelineLogger {
    SilentPipelineLogger() {}

    @Override
    void alwaysLog(final String txt) {}

    @Override
    void info(final String txt) {}

    @Override
    void error(final Throwable t) {}

    @Override
    void error(final String txt, final Throwable t) {}

    @Override
    void error(final String txt) {}

    @Override
    void warn(final String txt) {}

    @Override
    void trace(final String txt) {}

    @Override
    void trace(final String txt, final Throwable t) {}

    @Override
    void debug(final String txt) {}

    @Override
    void debug(final String txt, final Throwable t) {}

    @Override
    void setLogLevel(final LogLevel logLevel) {}

    @Override
    LogLevel getLogLevel() {
        return LogLevel.INFO
    }
}
