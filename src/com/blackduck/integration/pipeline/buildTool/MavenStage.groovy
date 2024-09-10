package com.blackduck.integration.pipeline.buildTool

import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Stage

class MavenStage extends Stage {
    static final String DEFAULT_MAVEN_TOOL_NAME = 'maven-3'
    static final String DEFAULT_MAVEN_OPTIONS = '-U clean package deploy'

    String mavenToolName = DEFAULT_MAVEN_TOOL_NAME
    String mavenOptions = DEFAULT_MAVEN_OPTIONS

    MavenStage(PipelineConfiguration pipelineConfiguration, String stageName) {
        super(pipelineConfiguration, stageName)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        String mvnHome = getPipelineConfiguration().getScriptWrapper().tool(mavenToolName)

        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${mvnHome}/bin/mvn ${mavenOptions}")
    }

    String getMavenToolName() {
        return mavenToolName
    }

    void setMavenToolName(final String mavenToolName) {
        this.mavenToolName = mavenToolName
    }

    String getMavenOptions() {
        return mavenOptions
    }

    void setMavenOptions(final String mavenOptions) {
        this.mavenOptions = mavenOptions
    }

}
