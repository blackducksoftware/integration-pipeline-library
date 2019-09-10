package com.synopsys.integration.pipeline.generic


import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.Stage

class ClosureStage extends Stage {
    private final JenkinsScriptWrapper scriptWrapper
    private final Closure closure

    ClosureStage(JenkinsScriptWrapper scriptWrapper, final String name, Closure closure) {
        super(name)
        this.scriptWrapper = scriptWrapper
        this.closure = closure
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        //FIXME not working yet
        closure.call()
    }
}
