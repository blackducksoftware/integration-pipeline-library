package com.synopsys.integration.pipeline.buildTool.maven

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.Stage

class MavenStage extends Stage {
    static final String DEFAULT_MAVEN_TOOL_NAME = 'maven-3'
    static final String DEFAULT_MAVEN_OPTIONS = '-U clean package deploy'

    final JenkinsScriptWrapper scriptWrapper
    String mavenToolName = DEFAULT_MAVEN_TOOL_NAME
    String mavenOptions = DEFAULT_MAVEN_OPTIONS

    MavenStage(JenkinsScriptWrapper scriptWrapper, String stageName) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
    }


    @Override
    void stageExecution() throws PipelineException, Exception {
        String mvnHome = scriptWrapper.tool(mavenToolName)

        scriptWrapper.executeCommand("${mvnHome}/bin/mvn ${mavenOptions}")
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
