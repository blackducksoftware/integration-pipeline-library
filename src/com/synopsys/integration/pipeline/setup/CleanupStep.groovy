package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Step

class CleanupStep extends Step {
    CleanupStep() {}

    @Override
    void run() throws PipelineException, Exception {
        getScriptWrapper().deleteDir()
    }
}
