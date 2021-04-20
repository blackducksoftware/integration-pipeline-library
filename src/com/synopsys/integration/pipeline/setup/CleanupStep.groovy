package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Step

class CleanupStep extends Step {
    CleanupStep(PipelineConfiguration pipelineConfiguration) {
        super(pipelineConfiguration)
    }

    CleanupStep(PipelineConfiguration pipelineConfiguration, String relativeDirectory) {
        super(pipelineConfiguration, relativeDirectory)
    }

    @Override
    void run() throws PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().deleteDir()
    }
}
