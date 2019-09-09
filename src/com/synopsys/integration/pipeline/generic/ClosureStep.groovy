package com.synopsys.integration.pipeline.generic


import com.cloudbees.groovy.cps.impl.CpsClosure
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Step

class ClosureStep extends Step {
    private CpsClosure closure

    ClosureStep(CpsClosure closure) {
        this.closure = closure
    }

    @Override
    void run() throws PipelineException, Exception {
        closure.run()
    }
}
