package com.synopsys.integration.pipeline.generic

import com.cloudbees.groovy.cps.NonCPS
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Step

class ClosureStep extends Step {
    private Closure closure

    ClosureStep(Closure closure) {
        this.closure = closure
    }

    @NonCPS
    @Override
    void run() throws PipelineException, Exception {
        closure.run()
    }
}
