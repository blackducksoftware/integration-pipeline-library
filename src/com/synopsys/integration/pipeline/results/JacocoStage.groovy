package com.synopsys.integration.pipeline.results

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class JacocoStage extends Stage {
    private LinkedHashMap jacocoOptions = [
            changeBuildStatus: false
            ,classPattern: '**/build/classes/java/main'
    ]

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
