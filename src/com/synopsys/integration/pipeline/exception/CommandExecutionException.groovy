package com.synopsys.integration.pipeline.exception

class CommandExecutionException extends Exception {
    final int errorStatus

    CommandExecutionException(int errorStatus) {
        super()
        this.errorStatus = errorStatus
    }

    CommandExecutionException(int errorStatus, String message) {
        super(message)
        this.errorStatus = errorStatus
    }

    int getErrorStatus() {
        return errorStatus
    }
}
