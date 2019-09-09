package com.synopsys.integration.pipeline.generic


import com.cloudbees.groovy.cps.impl.CpsClosure
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Stage

class ClosureStage extends Stage {
    private final CpsClosure closure

    ClosureStage(final String name, CpsClosure closure) {
        super(name)
        this.closure = closure
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        closure.run()
    }
}
