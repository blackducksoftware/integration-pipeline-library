package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.buildTool.gradle.GradleStage
import com.synopsys.integration.pipeline.buildTool.maven.MavenStage
import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.pipeline.setup.SetJdkStage
import org.jenkinsci.plugins.workflow.cps.CpsScript

class PipelineFactory implements Serializable {
    final PipelineLogger pipelineLogger
    final JenkinsScriptWrapper scriptWrapper

    PipelineFactory(CpsScript script) {
        scriptWrapper = new JenkinsScriptWrapper(script)
        this.pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
    }

    EmailPipelineWrapper createEmailPipelineWrapper(String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    EmailPipelineWrapper createEmailPipelineWrapper(String wrapperName, String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, wrapperName, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    SetJdkStage createSetJdkStage() {
        return new SetJdkStage(scriptWrapper, "Set JDK Stage")
    }

    SetJdkStage createSetJdkStage(String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(scriptWrapper, "Set JDK Stage")
        setJdkStage.setJdkToolName(jdkToolName)
        return setJdkStage
    }

    SetJdkStage createSetJdkStage(String stageName, String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(scriptWrapper, stageName)
        setJdkStage.setJdkToolName(jdkToolName)
        return setJdkStage
    }

    GitStage createGitStage(String url, String branch) {
        return new GitStage(scriptWrapper, "Git Stage", url, branch)
    }

    GitStage createGitStage(String stageName, String url, String branch) {
        return new GitStage(scriptWrapper, stageName, url, branch)
    }

    GradleStage createGradleStageDefaults() {
        return new GradleStage(scriptWrapper, "Gradle Stage")
    }

    GradleStage createGradleStageWithOptions(String gradleOptions) {
        GradleStage gradleStage = new GradleStage(scriptWrapper, "Gradle Stage")
        gradleStage.setGradleOptions(gradleOptions)
        return gradleStage
    }

    GradleStage createGradleStageWithExe(String gradleExe) {
        GradleStage gradleStage = new GradleStage(scriptWrapper, "Gradle Stage")
        gradleStage.setGradleExe(gradleExe)
        return gradleStage
    }

    GradleStage createGradleStage(String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(scriptWrapper, "Gradle Stage")
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        return gradleStage
    }

    GradleStage createGradleStage(String stageName, String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(scriptWrapper, stageName)
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        return gradleStage
    }

    MavenStage createMavenStageDefaults() {
        return new MavenStage(scriptWrapper, "Maven Stage")
    }

    MavenStage createMavenStageDefaultTool(String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, "Maven Stage")
        mavenStage.setMavenOptions(mavenOptions)
        return mavenStage
    }

    MavenStage createMavenStageDefaultTool(String stageName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, stageName)
        mavenStage.setMavenOptions(mavenOptions)
        return mavenStage
    }

    MavenStage createMavenStage(String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, "Maven Stage")
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        return mavenStage
    }

    MavenStage createMavenStage(String stageName, String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, stageName)
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        return mavenStage
    }

}
