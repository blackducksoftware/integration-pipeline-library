package com.blackduck.integration.pipeline.generic


import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

class ClosureStage extends com.blackduck.integration.pipeline.model.Stage {
    private final Closure closure

    ClosureStage(PipelineConfiguration pipelineConfiguration, String name, Closure closure) {
        super(pipelineConfiguration, name)
        this.closure = closure
    }

    @Override
    void stageExecution() throws com.blackduck.integration.pipeline.exception.PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().closure(closure)
    }
}
