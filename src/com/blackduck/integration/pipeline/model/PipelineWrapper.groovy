package com.blackduck.integration.pipeline.model

import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

abstract class PipelineWrapper extends Wrapper {
    PipelineWrapper(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }
}
