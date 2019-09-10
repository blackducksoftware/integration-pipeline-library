package com.synopsys.integration.pipeline.generic

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Step

class ClosureStep extends Step {
    private final Closure closure

    ClosureStep(Closure closure) {
        this.closure = closure
    }

    @Override
    void run() throws PipelineException, Exception {
        closure.call()
    }
}
