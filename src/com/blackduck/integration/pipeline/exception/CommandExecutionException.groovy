package com.blackduck.integration.pipeline.exception

class CommandExecutionException extends PipelineException {
    final int errorStatus

    CommandExecutionException(int errorStatus) {
        super()
        this.errorStatus = errorStatus
    }

    CommandExecutionException(int errorStatus, String message) {
        super(message)
        this.errorStatus = errorStatus
    }

    CommandExecutionException(String message, Exception e) {
        super(message, e)
        this.errorStatus = -1
    }

    int getErrorStatus() {
        return errorStatus
    }
}
