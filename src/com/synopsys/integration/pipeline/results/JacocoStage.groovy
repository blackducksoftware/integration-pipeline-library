package com.synopsys.integration.pipeline.results

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Stage

class JacocoStage extends Stage {
    private LinkedHashMap jacocoOptions = [changeBuildStatus: false]

    JacocoStage(String name) {
        super(name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getScriptWrapper().jacoco(jacocoOptions)
    }

    LinkedHashMap getJacocoOptions() {
        return jacocoOptions
    }

    void setJacocoOptions(final LinkedHashMap jacocoOptions) {
        this.jacocoOptions = jacocoOptions
    }
}
