package com.blackduck.integration.pipeline.generic

import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Step

class ClosureStep extends Step {
    private final Closure closure

    ClosureStep(PipelineConfiguration pipelineConfiguration, Closure closure) {
        super(pipelineConfiguration)
        this.closure = closure
    }

    @Override
    void run() throws PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().closure(closure)
    }
}
