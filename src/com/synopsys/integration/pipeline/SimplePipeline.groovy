package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.buildTool.GradleStage
import com.synopsys.integration.pipeline.buildTool.MavenStage
import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.generic.ClosureStage
import com.synopsys.integration.pipeline.generic.ClosureStep
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.results.ArchiveStage
import com.synopsys.integration.pipeline.results.JacocoStage
import com.synopsys.integration.pipeline.results.JunitStageWrapper
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.pipeline.setup.CleanupStep
import com.synopsys.integration.pipeline.setup.SetJdkStage
import com.synopsys.integration.pipeline.tools.DetectStage
import com.synopsys.integration.pipeline.versioning.GithubReleaseStage
import com.synopsys.integration.pipeline.versioning.NextSnapshotStage
import com.synopsys.integration.pipeline.versioning.RemoveSnapshotStage
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
        ArchiveStage archiveStage = new ArchiveStage(getScriptWrapper(), 'Archive')
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

    CleanupStep addCleanupStep() {
        CleanupStep cleanupStep = new CleanupStep(getScriptWrapper())
        addStep(cleanupStep)
        return cleanupStep
    }

    DetectStage addDetectStage(String detectCommand) {
        detectCommand = detectCommand + ' --detect.project.codelocation.unmap=true --detect.blackduck.signature.scanner.disabled=true --detect.force.success=true'

        DetectStage detectStage = new DetectStage(getScriptWrapper(), 'Detect', getScriptWrapper().env().HUB_DETECT_URL, detectCommand)
        detectStage.setRelativeDirectory(commonRunDirectory)
        addStage(detectStage)
        return detectStage
    }

    DetectStage addDetectStage(String stageName, String detectCommand) {
        detectCommand = detectCommand + ' --detect.project.codelocation.unmap=true --detect.blackduck.signature.scanner.disabled=true --detect.force.success=true'

        DetectStage detectStage = new DetectStage(getScriptWrapper(), stageName, getScriptWrapper().env().HUB_DETECT_URL, detectCommand)
        detectStage.setRelativeDirectory(commonRunDirectory)
        addStage(detectStage)
        return detectStage
    }

    EmailPipelineWrapper addEmailPipelineWrapper(String recipientList) {
        EmailPipelineWrapper emailPipelineWrapper = new EmailPipelineWrapper(getScriptWrapper(), getPipelineLogger(), recipientList, getScriptWrapper().env().JOB_NAME, getScriptWrapper().env().BUILD_NUMBER, getScriptWrapper().env().BUILD_URL)
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    EmailPipelineWrapper addEmailPipelineWrapper(String wrapperName, String recipientList) {
        EmailPipelineWrapper emailPipelineWrapper = new EmailPipelineWrapper(getScriptWrapper(), getPipelineLogger(), wrapperName, recipientList, getScriptWrapper().env().JOB_NAME, getScriptWrapper().env().BUILD_NUMBER, getScriptWrapper().env().BUILD_URL)
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    GithubReleaseStage addGithubReleaseStage(String branch) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getScriptWrapper(), getPipelineLogger(), 'GitHub Release', Boolean.valueOf(getScriptWrapper().env().RUN_RELEASE), branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStage(String stageName, String branch) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getScriptWrapper(), getPipelineLogger(), stageName, Boolean.valueOf(getScriptWrapper().env().RUN_RELEASE), branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStageByFile(String artifactFile, String branch) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getScriptWrapper(), getPipelineLogger(), 'GitHub Release', artifactFile, branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStageByFile(String stageName, String artifactFile, String branch) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getScriptWrapper(), getPipelineLogger(), stageName, artifactFile, branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStageByPattern(String artifactPattern, String artifactDirectory, String branch) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getScriptWrapper(), getPipelineLogger(), 'GitHub Release', artifactPattern, artifactDirectory, branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStageByPattern(String stageName, String artifactPattern, String artifactDirectory, String branch) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getScriptWrapper(), getPipelineLogger(), stageName, artifactPattern, artifactDirectory, branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GitStage addGitStage(String url, String branch) {
        GitStage gitStage = new GitStage(getScriptWrapper(), getPipelineLogger(), "Git", url, branch)
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
        GradleStage gradleStage = new GradleStage(getScriptWrapper(), "Gradle")
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

    JacocoStage addJacocoStage(LinkedHashMap jacocoOptions) {
        JacocoStage jacocoStage = new JacocoStage(getScriptWrapper(), 'Jacoco')
        jacocoStage.setJacocoOptions(jacocoOptions)
        jacocoStage.setRelativeDirectory(commonRunDirectory)
        addStage(jacocoStage)
        return jacocoStage
    }

    JacocoStage addJacocoStage(String stageName, LinkedHashMap jacocoOptions) {
        JacocoStage jacocoStage = new JacocoStage(getScriptWrapper(), stageName)
        jacocoStage.setJacocoOptions(jacocoOptions)
        jacocoStage.setRelativeDirectory(commonRunDirectory)
        addStage(jacocoStage)
        return jacocoStage
    }

    JunitStageWrapper addJunitStageWrapper(Stage stage, LinkedHashMap junitOptions) {
        JunitStageWrapper junitStageWrapper = new JunitStageWrapper(getScriptWrapper(), 'Junit')
        junitStageWrapper.setJunitOptions(junitOptions)
        junitStageWrapper.setRelativeDirectory(commonRunDirectory)
        stage.addStageWrapper(junitStageWrapper)
        return junitStageWrapper
    }

    JunitStageWrapper addJunitStageWrapper(Stage stage, String stageName, LinkedHashMap junitOptions) {
        JunitStageWrapper junitStageWrapper = new JunitStageWrapper(getScriptWrapper(), stageName)
        junitStageWrapper.setJunitOptions(junitOptions)
        junitStageWrapper.setRelativeDirectory(commonRunDirectory)
        stage.addStageWrapper(junitStageWrapper)
        return junitStageWrapper
    }


    MavenStage addMavenStage(String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(getScriptWrapper(), "Maven")
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

    NextSnapshotStage addNextSnapshotStage(String buildTool, String exe, String branch) {
        NextSnapshotStage nextSnapshotStage = new NextSnapshotStage(getScriptWrapper(), getPipelineLogger(), 'Next Snapshot', Boolean.valueOf(getScriptWrapper().env().RUN_RELEASE), buildTool, exe, branch)
        nextSnapshotStage.setRelativeDirectory(commonRunDirectory)
        addStage(nextSnapshotStage)
        return nextSnapshotStage
    }

    NextSnapshotStage addNextSnapshotStage(String stageName, String buildTool, String exe, String branch) {
        NextSnapshotStage nextSnapshotStage = new NextSnapshotStage(getScriptWrapper(), getPipelineLogger(), stageName, Boolean.valueOf(getScriptWrapper().env().RUN_RELEASE), buildTool, exe, branch)
        nextSnapshotStage.setRelativeDirectory(commonRunDirectory)
        addStage(nextSnapshotStage)
        return nextSnapshotStage
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String buildTool, String exe, String branch) {
        RemoveSnapshotStage removeSnapshotStage = new RemoveSnapshotStage(getScriptWrapper(), getPipelineLogger(), 'Remove Snapshot', Boolean.valueOf(getScriptWrapper().env().RUN_RELEASE), buildTool, exe, branch)
        removeSnapshotStage.setRelativeDirectory(commonRunDirectory)
        addStage(removeSnapshotStage)
        return removeSnapshotStage
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String stageName, String buildTool, String exe, String branch) {
        RemoveSnapshotStage removeSnapshotStage = new RemoveSnapshotStage(getScriptWrapper(), getPipelineLogger(), stageName, Boolean.valueOf(getScriptWrapper().env().RUN_RELEASE), buildTool, exe, branch)
        removeSnapshotStage.setRelativeDirectory(commonRunDirectory)
        addStage(removeSnapshotStage)
        return removeSnapshotStage
    }

    SetJdkStage addSetJdkStage() {
        SetJdkStage setJdkStage = new SetJdkStage(getScriptWrapper(), "Set JDK")
        setJdkStage.setRelativeDirectory(commonRunDirectory)
        addStage(setJdkStage)
        return setJdkStage
    }

    SetJdkStage addSetJdkStage(String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(getScriptWrapper(), "Set JDK")
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
