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
    public String commonRunDirectory

    SimplePipeline(CpsScript script) {
        this(script, '.')
    }

    SimplePipeline(CpsScript script, String commonRunDirectory) {
        super(script)
        this.commonRunDirectory = commonRunDirectory
    }

    ArchiveStage addArchiveStage(String archiveFilePattern) {
        ArchiveStage archiveStage = new ArchiveStage(getScriptWrapper(), 'Archive Stage')
        archiveStage.setArchiveFilePattern(archiveFilePattern)
        archiveStage.setRelativeDirectory(commonRunDirectory)
        addStage(archiveStage)
        return archiveStage
    }

    ArchiveStage addArchiveStage(String stageName, String archiveFilePattern) {
        ArchiveStage archiveStage = new ArchiveStage(getScriptWrapper(), stageName)
        archiveStage.setArchiveFilePattern(archiveFilePattern)
        archiveStage.setRelativeDirectory(commonRunDirectory)
        addStage(archiveStage)
        return archiveStage
    }

    EmailPipelineWrapper addEmailPipelineWrapper(String recipientList) {
        EmailPipelineWrapper emailPipelineWrapper = new EmailPipelineWrapper(getPipelineLogger(), getScriptWrapper(), recipientList, getScriptWrapper().env().JOB_NAME, getScriptWrapper().env().BUILD_NUMBER, getScriptWrapper().env().BUILD_URL)
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    EmailPipelineWrapper addEmailPipelineWrapper(String wrapperName, String recipientList) {
        EmailPipelineWrapper emailPipelineWrapper = new EmailPipelineWrapper(getPipelineLogger(), getScriptWrapper(), wrapperName, recipientList, getScriptWrapper().env().JOB_NAME, getScriptWrapper().env().BUILD_NUMBER, getScriptWrapper().env().BUILD_URL)
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    GitStage addGitStage(String url, String branch) {
        GitStage gitStage = new GitStage(getScriptWrapper(), getPipelineLogger(), "Git Stage", url, branch)
        gitStage.setRelativeDirectory(commonRunDirectory)
        addStage(gitStage)
        return gitStage
    }

    GitStage addGitStage(String stageName, String url, String branch) {
        GitStage gitStage = new GitStage(getScriptWrapper(), getPipelineLogger(), stageName, url, branch)
        gitStage.setRelativeDirectory(commonRunDirectory)
        addStage(gitStage)
        return gitStage
    }


    GradleStage addGradleStage(String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(getScriptWrapper(), "Gradle Stage")
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        gradleStage.setRelativeDirectory(commonRunDirectory)
        addStage(gradleStage)
        return gradleStage
    }

    GradleStage addGradleStage(String stageName, String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(getScriptWrapper(), stageName)
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        gradleStage.setRelativeDirectory(commonRunDirectory)
        addStage(gradleStage)
        return gradleStage
    }

    JacocoStage addJacocoStage() {
        JacocoStage jacocoStage = new JacocoStage(getScriptWrapper(), 'Jacoco Stage')
        jacocoStage.setRelativeDirectory(commonRunDirectory)
        addStage(jacocoStage)
        return jacocoStage
    }

    JacocoStage addJacocoStage(String stageName) {
        JacocoStage jacocoStage = new JacocoStage(getScriptWrapper(), stageName)
        jacocoStage.setRelativeDirectory(commonRunDirectory)
        addStage(jacocoStage)
        return jacocoStage
    }

    JunitStage addJunitStage(String xmlFilePattern) {
        JunitStage junitStage = new JunitStage(getScriptWrapper(), 'Junit Stage')
        junitStage.setXmlFilePattern(xmlFilePattern)
        junitStage.setRelativeDirectory(commonRunDirectory)
        addStage(junitStage)
        return junitStage
    }

    JunitStage addJunitStage(String stageName, String xmlFilePattern) {
        JunitStage junitStage = new JunitStage(getScriptWrapper(), stageName)
        junitStage.setXmlFilePattern(xmlFilePattern)
        junitStage.setRelativeDirectory(commonRunDirectory)
        addStage(junitStage)
        return junitStage
    }


    MavenStage addMavenStage(String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(getScriptWrapper(), "Maven Stage")
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        mavenStage.setRelativeDirectory(commonRunDirectory)
        addStage(mavenStage)
        return mavenStage
    }

    MavenStage addMavenStage(String stageName, String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(getScriptWrapper(), stageName)
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        mavenStage.setRelativeDirectory(commonRunDirectory)
        addStage(mavenStage)
        return mavenStage
    }

    SetJdkStage addSetJdkStage() {
        SetJdkStage setJdkStage = new SetJdkStage(getScriptWrapper(), "Set JDK Stage")
        setJdkStage.setRelativeDirectory(commonRunDirectory)
        addStage(setJdkStage)
        return setJdkStage
    }

    SetJdkStage addSetJdkStage(String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(getScriptWrapper(), "Set JDK Stage")
        setJdkStage.setJdkToolName(jdkToolName)
        setJdkStage.setRelativeDirectory(commonRunDirectory)
        addStage(setJdkStage)
        return setJdkStage
    }

    SetJdkStage addSetJdkStage(String stageName, String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(getScriptWrapper(), stageName)
        setJdkStage.setJdkToolName(jdkToolName)
        setJdkStage.setRelativeDirectory(commonRunDirectory)
        addStage(setJdkStage)
        return setJdkStage
    }

    ClosureStage addStage(String stageName, Closure closure) {
        ClosureStage closureStage = new ClosureStage(stageName, closure)
        closureStage.setRelativeDirectory(commonRunDirectory)
        addStage(closureStage)
        return closureStage
    }

    ClosureStep addStep(Closure closure) {
        ClosureStep closureStep = new ClosureStep(closure)
        closureStep.setRelativeDirectory(commonRunDirectory)
        addStep(closureStep)
        return closureStep
    }

    String getCommonRunDirectory() {
        return commonRunDirectory
    }

    void setCommonRunDirectory(final String commonRunDirectory) {
        this.commonRunDirectory = commonRunDirectory
    }
}
