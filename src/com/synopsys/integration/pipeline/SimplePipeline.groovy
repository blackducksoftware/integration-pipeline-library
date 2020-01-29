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
    // These tool names need to match the ones defined in Jenkins in the global tool config //
    public static final String JAVA_11 = 'OpenJDK 11'
    public static final String JAVA_8 = 'jdk8'
    //////////////////////////////////////////////////////////////////////////////////////////


    public static final String GRADLE_BUILD_TOOL = 'gradle'
    public static final String MAVEN_BUILD_TOOL = 'maven'

    public static final String RUN_RELEASE = 'RUN_RELEASE'
    public static final String RUN_QA_BUILD = 'RELEASE_QA_BUILD'

    public static final String BUILD_NUMBER = 'BUILD_NUMBER'
    public static final String JOB_NAME = 'JOB_NAME'
    public static final String BUILD_URL = 'BUILD_URL'
    public static final String HUB_DETECT_URL = 'HUB_DETECT_URL'

    public String commonRunDirectory

    SimplePipeline(CpsScript script) {
        this(script, '.')
    }

    SimplePipeline(CpsScript script, String commonRunDirectory) {
        super(script)
        this.commonRunDirectory = commonRunDirectory
    }

    ArchiveStage addArchiveStage(String archiveFilePattern) {
        ArchiveStage archiveStage = new ArchiveStage(getPipelineConfiguration(), 'Archive')
        archiveStage.setArchiveFilePattern(archiveFilePattern)
        archiveStage.setRelativeDirectory(commonRunDirectory)
        addStage(archiveStage)
        return archiveStage
    }

    ArchiveStage addArchiveStage(String stageName, String archiveFilePattern) {
        ArchiveStage archiveStage = new ArchiveStage(getPipelineConfiguration(), stageName)
        archiveStage.setArchiveFilePattern(archiveFilePattern)
        archiveStage.setRelativeDirectory(commonRunDirectory)
        addStage(archiveStage)
        return archiveStage
    }

    CleanupStep addCleanupStep() {
        CleanupStep cleanupStep = new CleanupStep(getPipelineConfiguration())
        addStep(cleanupStep)
        return cleanupStep
    }

    DetectStage addDetectStage(String detectCommand) {
        detectCommand = detectCommand + ' --detect.project.codelocation.unmap=true --detect.tools.excluded=SIGNATURE_SCAN --detect.force.success=true'

        DetectStage detectStage = new DetectStage(getPipelineConfiguration(), 'Detect', getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(HUB_DETECT_URL), detectCommand)
        detectStage.setRelativeDirectory(commonRunDirectory)
        addStage(detectStage)
        return detectStage
    }

    DetectStage addDetectStage(String stageName, String detectCommand) {
        detectCommand = detectCommand + ' --detect.project.codelocation.unmap=true --detect.tools.excluded=SIGNATURE_SCAN --detect.force.success=true'

        DetectStage detectStage = new DetectStage(getPipelineConfiguration(), stageName, getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(HUB_DETECT_URL), detectCommand)
        detectStage.setRelativeDirectory(commonRunDirectory)
        addStage(detectStage)
        return detectStage
    }

    EmailPipelineWrapper addEmailPipelineWrapper(String recipientList) {
        EmailPipelineWrapper emailPipelineWrapper = new EmailPipelineWrapper(getPipelineConfiguration(), recipientList, getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(JOB_NAME), getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(BUILD_NUMBER), getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(BUILD_URL))
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    EmailPipelineWrapper addEmailPipelineWrapper(String wrapperName, String recipientList) {
        EmailPipelineWrapper emailPipelineWrapper = new EmailPipelineWrapper(getPipelineConfiguration(), wrapperName, recipientList, getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(JOB_NAME), getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(BUILD_NUMBER), getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(BUILD_URL))
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    GithubReleaseStage addGithubReleaseStage(String branch) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), 'GitHub Release', Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE)), branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStage(String stageName, String branch) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), stageName, Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE)), branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStageByFile(String branch, String artifactFile) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), 'GitHub Release', Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE)), artifactFile, branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStageByFile(String stageName, String branch, String artifactFile) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), stageName, Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE)), artifactFile, branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStageByPattern(String branch, String artifactPattern, String artifactDirectory) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), 'GitHub Release', Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE)), artifactPattern, artifactDirectory, branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GithubReleaseStage addGithubReleaseStageByPattern(String stageName, String branch, String artifactPattern, String artifactDirectory) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), stageName, Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE)), artifactPattern, artifactDirectory, branch)
        githubReleaseStage.setRelativeDirectory(commonRunDirectory)
        addStage(githubReleaseStage)
        return githubReleaseStage
    }

    GitStage addGitStage(String url, String branch) {
        GitStage gitStage = new GitStage(getPipelineConfiguration(), "Git", url, branch)
        gitStage.setRelativeDirectory(commonRunDirectory)
        addStage(gitStage)
        return gitStage
    }

    GitStage addGitStage(String stageName, String url, String branch) {
        GitStage gitStage = new GitStage(getPipelineConfiguration(), stageName, url, branch)
        gitStage.setRelativeDirectory(commonRunDirectory)
        addStage(gitStage)
        return gitStage
    }


    GradleStage addGradleStage(String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(getPipelineConfiguration(), "Gradle")
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        gradleStage.setRelativeDirectory(commonRunDirectory)
        addStage(gradleStage)
        return gradleStage
    }

    GradleStage addGradleStage(String stageName, String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(getPipelineConfiguration(), stageName)
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        gradleStage.setRelativeDirectory(commonRunDirectory)
        addStage(gradleStage)
        return gradleStage
    }

    JacocoStage addJacocoStage(LinkedHashMap jacocoOptions) {
        JacocoStage jacocoStage = new JacocoStage(getPipelineConfiguration(), 'Jacoco')
        jacocoStage.setJacocoOptions(jacocoOptions)
        jacocoStage.setRelativeDirectory(commonRunDirectory)
        addStage(jacocoStage)
        return jacocoStage
    }

    JacocoStage addJacocoStage(String stageName, LinkedHashMap jacocoOptions) {
        JacocoStage jacocoStage = new JacocoStage(getPipelineConfiguration(), stageName)
        jacocoStage.setJacocoOptions(jacocoOptions)
        jacocoStage.setRelativeDirectory(commonRunDirectory)
        addStage(jacocoStage)
        return jacocoStage
    }

    JunitStageWrapper addJunitStageWrapper(Stage stage, LinkedHashMap junitOptions) {
        JunitStageWrapper junitStageWrapper = new JunitStageWrapper(getPipelineConfiguration(), 'Junit')
        junitStageWrapper.setJunitOptions(junitOptions)
        junitStageWrapper.setRelativeDirectory(commonRunDirectory)
        stage.addStageWrapper(junitStageWrapper)
        return junitStageWrapper
    }

    JunitStageWrapper addJunitStageWrapper(Stage stage, String stageName, LinkedHashMap junitOptions) {
        JunitStageWrapper junitStageWrapper = new JunitStageWrapper(getPipelineConfiguration(), stageName)
        junitStageWrapper.setJunitOptions(junitOptions)
        junitStageWrapper.setRelativeDirectory(commonRunDirectory)
        stage.addStageWrapper(junitStageWrapper)
        return junitStageWrapper
    }


    MavenStage addMavenStage(String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(getPipelineConfiguration(), "Maven")
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        mavenStage.setRelativeDirectory(commonRunDirectory)
        addStage(mavenStage)
        return mavenStage
    }

    MavenStage addMavenStage(String stageName, String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(getPipelineConfiguration(), stageName)
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        mavenStage.setRelativeDirectory(commonRunDirectory)
        addStage(mavenStage)
        return mavenStage
    }

    NextSnapshotStage addNextSnapshotStage(String buildTool, String exe, String branch) {
        boolean runRelease = Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE))
        boolean runQARelease = Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_QA_BUILD))

        NextSnapshotStage nextSnapshotStage = new NextSnapshotStage(getPipelineConfiguration(), 'Next Snapshot', runRelease, runQARelease, buildTool, exe, branch)
        nextSnapshotStage.setRelativeDirectory(commonRunDirectory)
        addStage(nextSnapshotStage)
        return nextSnapshotStage
    }

    NextSnapshotStage addNextSnapshotStage(String stageName, String buildTool, String exe, String branch) {
        boolean runRelease = Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE))
        boolean runQARelease = Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_QA_BUILD))
        NextSnapshotStage nextSnapshotStage = new NextSnapshotStage(getPipelineConfiguration(), stageName, runRelease, runQARelease, buildTool, exe, branch)
        nextSnapshotStage.setRelativeDirectory(commonRunDirectory)
        addStage(nextSnapshotStage)
        return nextSnapshotStage
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String buildTool, String exe, String branch) {
        boolean runRelease = Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE))
        boolean runQARelease = Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_QA_BUILD))
        RemoveSnapshotStage removeSnapshotStage = new RemoveSnapshotStage(getPipelineConfiguration(), 'Remove Snapshot', runRelease, runQARelease, buildTool, exe, branch)
        removeSnapshotStage.setRelativeDirectory(commonRunDirectory)
        addStage(removeSnapshotStage)
        return removeSnapshotStage
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String stageName, String buildTool, String exe, String branch) {
        boolean runRelease = Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_RELEASE))
        boolean runQARelease = Boolean.valueOf(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RUN_QA_BUILD))
        RemoveSnapshotStage removeSnapshotStage = new RemoveSnapshotStage(getPipelineConfiguration(), stageName, runRelease, runQARelease, buildTool, exe, branch)
        removeSnapshotStage.setRelativeDirectory(commonRunDirectory)
        addStage(removeSnapshotStage)
        return removeSnapshotStage
    }

    SetJdkStage addSetJdkStage() {
        SetJdkStage setJdkStage = new SetJdkStage(getPipelineConfiguration(), "Set JDK")
        setJdkStage.setRelativeDirectory(commonRunDirectory)
        addStage(setJdkStage)
        return setJdkStage
    }

    SetJdkStage addSetJdkStage(String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(getPipelineConfiguration(), "Set JDK")
        setJdkStage.setJdkToolName(jdkToolName)
        setJdkStage.setRelativeDirectory(commonRunDirectory)
        addStage(setJdkStage)
        return setJdkStage
    }

    SetJdkStage addSetJdkStage(String stageName, String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(getPipelineConfiguration(), stageName)
        setJdkStage.setJdkToolName(jdkToolName)
        setJdkStage.setRelativeDirectory(commonRunDirectory)
        addStage(setJdkStage)
        return setJdkStage
    }

    ClosureStage addStage(String stageName, Closure closure) {
        ClosureStage closureStage = new ClosureStage(getPipelineConfiguration(), stageName, closure)
        closureStage.setRelativeDirectory(commonRunDirectory)
        addStage(closureStage)
        return closureStage
    }

    ClosureStep addStep(Closure closure) {
        ClosureStep closureStep = new ClosureStep(getPipelineConfiguration(), closure)
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
