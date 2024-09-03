package com.blackduck.integration.pipeline.generic


import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

class ClosureStep extends com.blackduck.integration.pipeline.model.Step {
    private final Closure closure

    ClosureStep(PipelineConfiguration pipelineConfiguration, Closure closure) {
        super(pipelineConfiguration)
        this.closure = closure
    }

    @Override
    void run() throws com.blackduck.integration.pipeline.exception.PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().closure(closure)
    }
}
