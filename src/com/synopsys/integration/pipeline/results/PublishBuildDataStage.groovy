package com.synopsys.integration.pipeline.results

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class PublishBuildDataStage extends Stage {
    public static final String JSON_FILE_NAME = "build_data.json"

    private Map<String, String> buildDataMap

    PublishBuildDataStage(PipelineConfiguration pipelineConfiguration, String name, Map<String, String> buildDataMap) {
        super(pipelineConfiguration, name)
        this.buildDataMap = buildDataMap
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        buildDataMap.each { k, v -> getPipelineConfiguration().getLogger().info("${k}:${v}") }
    }

}
