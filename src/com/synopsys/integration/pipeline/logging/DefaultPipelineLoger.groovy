package com.synopsys.integration.pipeline.logging

import com.synopsys.integration.pipeline.jenkins.ScriptWrapper

class DefaultPipelineLoger extends PipelineLogger {
    private LogLevel currentLogLevel = LogLevel.INFO
    private final ScriptWrapper scriptWrapper

    public DefaultPipelineLoger(ScriptWrapper scriptWrapper) {
        this.scriptWrapper = scriptWrapper
    }

    private void doLog(LogLevel logLevel, String txt, Throwable t) {
        if (currentLogLevel.isLoggable(logLevel)) {
            if (null != txt) {
                StringBuilder sb = new StringBuilder()
                if (logLevel != LogLevel.OFF) {
                    sb.append(logLevel.toString())
                    sb.append("  ")
                }
                sb.append(txt)
                scriptWrapper.println(sb.toString())
            }
            if (null != t) {
                StringBuilder sb = new StringBuilder()
                if (logLevel != LogLevel.OFF) {
                    sb.append(logLevel.toString())
                    sb.append("  ")
                }
                StringWriter sw = new StringWriter()
                PrintWriter pw = new PrintWriter(sw)
                t.printStackTrace(pw)
                sb.append(sw.toString())
                scriptWrapper.println(sb.toString())
            }
        }
    }

    @Override
    void alwaysLog(final String txt) {
        doLog(LogLevel.OFF, txt, null)
    }

    @Override
    void info(final String txt) {
        doLog(LogLevel.INFO, txt, null)
    }

    @Override
    void error(final Throwable t) {
        doLog(LogLevel.ERROR, null, t)
    }

    @Override
    void error(final String txt, final Throwable t) {
        doLog(LogLevel.ERROR, txt, t)
    }

    @Override
    void error(final String txt) {
        doLog(LogLevel.ERROR, txt, null)
    }

    @Override
    void warn(final String txt) {
        doLog(LogLevel.WARN, txt, null)
    }

    @Override
    void trace(final String txt) {
        doLog(LogLevel.TRACE, txt, null)
    }

    @Override
    void trace(final String txt, final Throwable t) {
        doLog(LogLevel.TRACE, txt, t)
    }

    @Override
    void debug(final String txt) {
        doLog(LogLevel.DEBUG, txt, null)
    }

    @Override
    void debug(final String txt, final Throwable t) {
        doLog(LogLevel.DEBUG, txt, t)
    }

    @Override
    void setLogLevel(final LogLevel logLevel) {
        currentLogLevel = logLevel
    }

    @Override
    LogLevel getLogLevel() {
        return currentLogLevel
    }
}
