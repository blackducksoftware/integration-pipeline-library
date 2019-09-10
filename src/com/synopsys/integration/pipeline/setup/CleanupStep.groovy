package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.Step

class CleanupStep extends Step {
    private final JenkinsScriptWrapper scriptWrapper

    CleanupStep(final JenkinsScriptWrapper scriptWrapper) {
        this.scriptWrapper = scriptWrapper
    }

    @Override
    void run() throws PipelineException, Exception {
        this.scriptWrapper.executeCommand('rm -rf *')
    }
}
