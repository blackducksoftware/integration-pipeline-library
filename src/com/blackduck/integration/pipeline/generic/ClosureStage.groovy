package com.blackduck.integration.pipeline.generic

import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Stage

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
