package com.blackduck.integration.pipeline.results


import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

class JacocoStage extends com.blackduck.integration.pipeline.model.Stage {
    private LinkedHashMap jacocoOptions = [changeBuildStatus: false]

    JacocoStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws com.blackduck.integration.pipeline.exception.PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().jacoco(jacocoOptions)
    }

    LinkedHashMap getJacocoOptions() {
        return jacocoOptions
    }

    void setJacocoOptions(final LinkedHashMap jacocoOptions) {
        this.jacocoOptions = jacocoOptions
    }
}
