package com.synopsys.integration.pipeline.logging

enum LogLevel {
    OFF,
    ERROR,
    WARN,
    INFO,
    DEBUG,
    TRACE

    /**
     * Will return true if logLevel is loggable for this logLevel, false otherwise.*/
    boolean isLoggable(final LogLevel logLevel) {
        return this >= logLevel
    }

}
