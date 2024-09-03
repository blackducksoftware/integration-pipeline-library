package com.blackduck.integration.pipeline.results


import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

class PublishBuildDataStage extends com.blackduck.integration.pipeline.model.Stage {
    public static final String JSON_FILE_NAME = "build_data.json"

    private Map<String, String> buildDataMap

    PublishBuildDataStage(PipelineConfiguration pipelineConfiguration, String name, Map<String, String> buildDataMap) {
        super(pipelineConfiguration, name)
        this.buildDataMap = buildDataMap
    }

    @Override
    void stageExecution() throws com.blackduck.integration.pipeline.exception.PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().writeJsonFile(JSON_FILE_NAME, buildDataMap)
        getPipelineConfiguration().getScriptWrapper().archiveArtifacts(JSON_FILE_NAME)
    }

}
