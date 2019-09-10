package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.buildTool.gradle.GradleStage
import com.synopsys.integration.pipeline.buildTool.maven.MavenStage
import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.generic.ClosureStage
import com.synopsys.integration.pipeline.generic.ClosureStep
import com.synopsys.integration.pipeline.results.ArchiveStage
import com.synopsys.integration.pipeline.results.JacocoStage
import com.synopsys.integration.pipeline.results.JunitStage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.pipeline.setup.SetJdkStage
import org.jenkinsci.plugins.workflow.cps.CpsScript

class SimplePipeline extends Pipeline {

    SimplePipeline(CpsScript script) {
        super(script)
    }

    void addArchiveStage(String archiveFilePattern) {
        ArchiveStage archiveStage = new ArchiveStage(getScriptWrapper(), 'Archive Stage')
        archiveStage.setArchiveFilePattern(archiveFilePattern)
        addStage(archiveStage)
    }

    void addArchiveStage(String stageName, String archiveFilePattern) {
        ArchiveStage archiveStage = new ArchiveStage(getScriptWrapper(), stageName)
        archiveStage.setArchiveFilePattern(archiveFilePattern)
        addStage(archiveStage)
    }

    void addEmailPipelineWrapper(String recipientList) {
        addPipelineWrapper(new EmailPipelineWrapper(getPipelineLogger(), getScriptWrapper(), recipientList, getScriptWrapper().env().JOB_NAME, getScriptWrapper().env().BUILD_NUMBER, getScriptWrapper().env().BUILD_URL))
    }

    void addEmailPipelineWrapper(String wrapperName, String recipientList) {
        addPipelineWrapper(new EmailPipelineWrapper(getPipelineLogger(), getScriptWrapper(), wrapperName, recipientList, getScriptWrapper().env().JOB_NAME, getScriptWrapper().env().BUILD_NUMBER, getScriptWrapper().env().BUILD_URL))
    }

    void addGitStage(String url, String branch) {
        addStage(new GitStage(getScriptWrapper(), "Git Stage", url, branch))
    }

    void addGitStage(String stageName, String url, String branch) {
        addStage(new GitStage(getScriptWrapper(), stageName, url, branch))
    }


    void addGradleStage(String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(getScriptWrapper(), "Gradle Stage")
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        addStage(gradleStage)
    }

    void addGradleStage(String stageName, String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(getScriptWrapper(), stageName)
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        addStage(gradleStage)
    }

    void addJacocoStage() {
        JacocoStage jacocoStage = new JacocoStage(getScriptWrapper(), 'Jacoco Stage')
        addStage(jacocoStage)
    }

    void addJacocoStage(String stageName) {
        JacocoStage jacocoStage = new JacocoStage(getScriptWrapper(), stageName)
        addStage(jacocoStage)
    }

    void addJunitStage(String xmlFilePattern) {
        JunitStage junitStage = new JunitStage(getScriptWrapper(), 'Junit Stage')
        junitStage.setXmlFilePattern(xmlFilePattern)
        addStage(junitStage)
    }

    void addJunitStage(String stageName, String xmlFilePattern) {
        JunitStage junitStage = new JunitStage(getScriptWrapper(), stageName)
        junitStage.setXmlFilePattern(xmlFilePattern)
        addStage(junitStage)
    }


    void addMavenStage(String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(getScriptWrapper(), "Maven Stage")
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        addStage(mavenStage)
    }

    void addMavenStage(String stageName, String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(getScriptWrapper(), stageName)
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        addStage(mavenStage)
    }

    void addSetJdkStage() {
        addStage(new SetJdkStage(getScriptWrapper(), "Set JDK Stage"))
    }

    void addSetJdkStage(String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(getScriptWrapper(), "Set JDK Stage")
        setJdkStage.setJdkToolName(jdkToolName)
        addStage(setJdkStage)
    }

    void addSetJdkStage(String stageName, String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(getScriptWrapper(), stageName)
        setJdkStage.setJdkToolName(jdkToolName)
        addStage(setJdkStage)
    }

    void addStage(String stageName, Closure closure) {
        addStage(new ClosureStage(stageName, closure))
    }

    void addStep(Closure closure) {
        addStep(new ClosureStep(closure))
    }

}
