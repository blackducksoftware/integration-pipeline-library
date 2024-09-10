package com.blackduck.integration.pipeline.model

import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

abstract class StageWrapper extends Wrapper {
    StageWrapper(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }
}
