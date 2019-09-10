package com.synopsys.integration.pipeline.results

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.Stage

class JacocoStage extends Stage {
    private JenkinsScriptWrapper scriptWrapper
    private Object jacocoOptions = [changeBuildStatus: false]

    JacocoStage(JenkinsScriptWrapper scriptWrapper, String name) {
        super(name)
        this.scriptWrapper = scriptWrapper
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        scriptWrapper.jacoco(jacocoOptions)
    }

    Object getJacocoOptions() {
        return jacocoOptions
    }

    void setJacocoOptions(final Object jacocoOptions) {
        this.jacocoOptions = jacocoOptions
    }
}
