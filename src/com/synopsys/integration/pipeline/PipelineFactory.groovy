package com.synopsys.integration.pipeline


import com.synopsys.integration.pipeline.buildTool.gradle.GradleStage
import com.synopsys.integration.pipeline.buildTool.maven.MavenStage
import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.generic.ClosureStage
import com.synopsys.integration.pipeline.generic.ClosureStep
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

    EmailPipelineWrapper emailPipelineWrapper(String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    EmailPipelineWrapper emailPipelineWrapper(String wrapperName, String recipientList) {
        return new EmailPipelineWrapper(pipelineLogger, scriptWrapper, wrapperName, recipientList, scriptWrapper.env().JOB_NAME, scriptWrapper.env().BUILD_NUMBER, scriptWrapper.env().BUILD_URL)
    }

    SetJdkStage setJdkStage() {
        return new SetJdkStage(scriptWrapper, "Set JDK Stage")
    }

    SetJdkStage setJdkStage(String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(scriptWrapper, "Set JDK Stage")
        setJdkStage.setJdkToolName(jdkToolName)
        return setJdkStage
    }

    SetJdkStage setJdkStage(String stageName, String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(scriptWrapper, stageName)
        setJdkStage.setJdkToolName(jdkToolName)
        return setJdkStage
    }

    GitStage gitStage(String url, String branch) {
        return new GitStage(scriptWrapper, "Git Stage", url, branch)
    }

    GitStage gitStage(String stageName, String url, String branch) {
        return new GitStage(scriptWrapper, stageName, url, branch)
    }

    GradleStage gradleStageDefaults() {
        return new GradleStage(scriptWrapper, "Gradle Stage")
    }

    GradleStage gradleStageWithOptions(String gradleOptions) {
        GradleStage gradleStage = new GradleStage(scriptWrapper, "Gradle Stage")
        gradleStage.setGradleOptions(gradleOptions)
        return gradleStage
    }

    GradleStage gradleStageWithExe(String gradleExe) {
        GradleStage gradleStage = new GradleStage(scriptWrapper, "Gradle Stage")
        gradleStage.setGradleExe(gradleExe)
        return gradleStage
    }

    GradleStage gradleStage(String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(scriptWrapper, "Gradle Stage")
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        return gradleStage
    }

    GradleStage gradleStage(String stageName, String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(scriptWrapper, stageName)
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        return gradleStage
    }

    MavenStage mavenStageDefaults() {
        return new MavenStage(scriptWrapper, "Maven Stage")
    }

    MavenStage mavenStageDefaultTool(String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, "Maven Stage")
        mavenStage.setMavenOptions(mavenOptions)
        return mavenStage
    }

    MavenStage mavenStageDefaultTool(String stageName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, stageName)
        mavenStage.setMavenOptions(mavenOptions)
        return mavenStage
    }

    MavenStage mavenStage(String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, "Maven Stage")
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        return mavenStage
    }

    MavenStage mavenStage(String stageName, String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(scriptWrapper, stageName)
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        return mavenStage
    }

    ClosureStage stage(String stageName, CpsScript closure) {
        return new ClosureStage(stageName, closure)
    }

    ClosureStep step(CpsScript closure) {
        return new ClosureStep(closure)
    }

}
