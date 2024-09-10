package com.blackduck.integration.pipeline.results

import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Stage

class JacocoStage extends Stage {
    private LinkedHashMap jacocoOptions = [changeBuildStatus: false]

    JacocoStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().jacoco(jacocoOptions)
    }

    LinkedHashMap getJacocoOptions() {
        return jacocoOptions
    }

    void setJacocoOptions(final LinkedHashMap jacocoOptions) {
        this.jacocoOptions = jacocoOptions
    }
}
