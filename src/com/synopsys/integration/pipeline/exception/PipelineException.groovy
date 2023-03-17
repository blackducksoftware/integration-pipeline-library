package com.synopsys.integration.pipeline.exception

class PipelineException extends Exception {
    PipelineException() {
        super()
    }

    PipelineException(final String message, final Throwable cause) {
        super(message, cause)
    }

    PipelineException(final String message) {
        super(message)
    }

    PipelineException(final Throwable cause) {
        super(cause)
    }

}
