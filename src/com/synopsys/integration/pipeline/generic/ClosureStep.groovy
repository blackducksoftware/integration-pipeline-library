package com.synopsys.integration.pipeline.generic


import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.Step

class ClosureStep extends Step {
    private final JenkinsScriptWrapper scriptWrapper
    private final Closure closure

    ClosureStep(JenkinsScriptWrapper scriptWrapper, Closure closure) {
        this.scriptWrapper = scriptWrapper
        this.closure = closure
    }

    @Override
    void run() throws PipelineException, Exception {
        //FIXME not working yet
        closure.call()
    }
}
