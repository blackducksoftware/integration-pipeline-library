package com.synopsys.integration.pipeline.generic

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Stage

class ClosureStage extends Stage {
    private final Closure closure

    ClosureStage(String name, Closure closure) {
        super(name)
        this.closure = closure
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        closure.call()
    }
}
