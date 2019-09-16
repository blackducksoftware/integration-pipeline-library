package com.synopsys.integration.pipeline.buildTool

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Stage

class MavenStage extends Stage {
    static final String DEFAULT_MAVEN_TOOL_NAME = 'maven-3'
    static final String DEFAULT_MAVEN_OPTIONS = '-U clean package deploy'

    String mavenToolName = DEFAULT_MAVEN_TOOL_NAME
    String mavenOptions = DEFAULT_MAVEN_OPTIONS

    MavenStage(String stageName) {
        super(stageName)
    }


    @Override
    void stageExecution() throws PipelineException, Exception {
        String mvnHome = getScriptWrapper().tool(mavenToolName)

        getScriptWrapper().executeCommand("${mvnHome}/bin/mvn ${mavenOptions}")
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
