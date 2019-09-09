package com.synopsys.integration.pipeline.generic

import com.cloudbees.groovy.cps.NonCPS
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Step
import org.jenkinsci.plugins.workflow.cps.CpsScript

class ClosureStep extends Step {
    private final CpsScript closure

    ClosureStep(CpsScript closure) {
        this.closure = closure
    }

    @NonCPS
    @Override
    void run() throws PipelineException, Exception {
        closure.run()
    }
}