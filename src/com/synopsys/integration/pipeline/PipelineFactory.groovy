package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.buildTool.gradle.GradleStage
import com.synopsys.integration.pipeline.buildTool.maven.MavenStage
import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.jenkins.ScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger

class PipelineFactory implements Serializable {
    final PipelineLogger pipelineLogger
    final ScriptWrapper scriptWrapper

    PipelineFactory(Object script) {
        scriptWrapper = new ScriptWrapper(script)
        this.pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
    }

    EmailPipelineWrapper createEmailPipelineWrapper(String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    EmailPipelineWrapper createEmailPipelineWrapper(String wrapperName, String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, wrapperName, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    GradleStage createGradleStage(String gradleExe, String gradleOptions) {
        pipelineLogger.info("Setting the gradle stage name to 'Gradle Stage' ")
        return new GradleStage(scriptWrapper, "Gradle Stage", gradleExe, gradleOptions)
    }

    GradleStage createGradleStage(String stageName, String gradleExe, String gradleOptions) {
        return new GradleStage(scriptWrapper, stageName, gradleExe, gradleOptions)
    }

    MavenStage createMavenStageDefaultTool(String mavenOptions) {
        return new MavenStage(scriptWrapper, "Maven Stage", MavenStage.DEFAULT_MAVEN_TOOL_NAME, mavenOptions)
    }

    MavenStage createMavenStageDefaultTool(String stageName, String mavenOptions) {
        return new MavenStage(scriptWrapper, stageName, MavenStage.DEFAULT_MAVEN_TOOL_NAME, mavenOptions)
    }

    MavenStage createMavenStage(String mavenToolName, String mavenOptions) {
        return new MavenStage(scriptWrapper, "Maven Stage", mavenToolName, mavenOptions)
    }

    MavenStage createMavenStage(String stageName, String mavenToolName, String mavenOptions) {
        return new MavenStage(scriptWrapper, stageName, mavenToolName, mavenOptions)
    }

}
