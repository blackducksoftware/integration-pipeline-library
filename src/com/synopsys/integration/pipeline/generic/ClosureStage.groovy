package com.synopsys.integration.pipeline.generic

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class ClosureStage extends Stage {
    private final Closure closure

    ClosureStage(PipelineConfiguration pipelineConfiguration, String name, Closure closure) {
        super(pipelineConfiguration, name)
        this.closure = closure
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().closure(closure)
    }
}
