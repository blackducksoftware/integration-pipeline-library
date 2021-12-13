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
        String buildUser = retrieveStringFromEnv("BUILDER_SERVICE_USER_NAME")
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("sudo chown -R ${buildUser}:${buildUser} .")
        getPipelineConfiguration().getScriptWrapper().deleteDir()
    }
}
