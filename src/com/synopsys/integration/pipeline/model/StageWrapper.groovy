package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

abstract class StageWrapper extends Wrapper {
    StageWrapper(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }
}
