package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.buildTool.gradle.GradleStage
import com.synopsys.integration.pipeline.buildTool.maven.MavenStage
import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.jenkins.ScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger

class PipelineFactory implements Serializable {
    private final PipelineLogger pipelineLogger
    private final ScriptWrapper scriptWrapper

    public PipelineFactory(Object script) {
        scriptWrapper = new ScriptWrapper(script)
        this.pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
    }

    public EmailPipelineWrapper createEmailPipelineWrapper(String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    public EmailPipelineWrapper createEmailPipelineWrapper(String wrapperName, String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, wrapperName, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    public GradleStage createGradleStage(String gradleExe, String gradleOptions) {
        pipelineLogger.info("Setting the gradle stage name to 'Gradle Stage' ")
        GradleStage gradleStage = new GradleStage(scriptWrapper, gradleExe, gradleOptions)
        gradleStage.setName("Gradle Stage")
        return gradleStage
    }

    public GradleStage createGradleStage(String stageName, String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(scriptWrapper, stageName, gradleExe, gradleOptions)
        gradleStage.setName(stageName)
        return gradleStage
    }

    public MavenStage createMavenStageDefaultTool(String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, "Maven Stage", MavenStage.DEFAULT_MAVEN_TOOL_NAME, mavenOptions)
        mavenStage.setName("Maven Stage")
        return mavenStage
    }

    public MavenStage createMavenStageDefaultTool(String stageName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, stageName, MavenStage.DEFAULT_MAVEN_TOOL_NAME, mavenOptions)
        mavenStage.setName(stageName)
        return mavenStage
    }

    public MavenStage createMavenStage(String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, "Maven Stage", mavenToolName, mavenOptions)
        mavenStage.setName("Maven Stage")
        return mavenStage
    }

    public MavenStage createMavenStage(String stageName, String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, stageName, mavenToolName, mavenOptions)
        mavenStage.setName(stageName)
        return mavenStage
    }

}
