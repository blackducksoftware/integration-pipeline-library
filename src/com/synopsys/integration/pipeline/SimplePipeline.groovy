package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.buildTool.GradleStage
import com.synopsys.integration.pipeline.buildTool.MavenStage
import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.generic.ClosureStage
import com.synopsys.integration.pipeline.generic.ClosureStep
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.model.Step
import com.synopsys.integration.pipeline.results.ArchiveStage
import com.synopsys.integration.pipeline.results.JacocoStage
import com.synopsys.integration.pipeline.results.JunitStageWrapper
import com.synopsys.integration.pipeline.scm.GitBranch
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.pipeline.setup.ApiTokenStage
import com.synopsys.integration.pipeline.setup.CleanupStep
import com.synopsys.integration.pipeline.setup.SetJdkStage
import com.synopsys.integration.pipeline.tools.DetectStage
import com.synopsys.integration.pipeline.versioning.GithubReleaseStage
import com.synopsys.integration.pipeline.versioning.NextSnapshotStage
import com.synopsys.integration.pipeline.versioning.RemoveSnapshotStage
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.jenkinsci.plugins.workflow.cps.CpsScript

class SimplePipeline extends Pipeline {
    // These tool names need to match the ones defined in Jenkins in the global tool config //
    public static final String JAVA_11 = 'jdk-11.0.7'
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

    static SimplePipeline COMMON_PIPELINE(CpsScript script, String branch, String relativeDirectory, String url, String jdkToolName, boolean gitPolling) {
        SimplePipeline pipeline = new SimplePipeline(script)
        pipeline.addCleanupStep(relativeDirectory)
        pipeline.addSetJdkStage(jdkToolName)
        String gitBranch = pipeline.determineGitBranch(branch)
        pipeline.setDirectoryFromBranch(gitBranch)
        GitStage gitStage = pipeline.addGitStage(url, gitBranch, gitPolling)
        gitStage.setChangelog(true)
        pipeline.addApiTokenStage()

        return pipeline
    }

    public String commonRunDirectory

    SimplePipeline(CpsScript script) {
        this(script, '.')
    }

    SimplePipeline(CpsScript script, String commonRunDirectory) {
        super(script)
        this.commonRunDirectory = commonRunDirectory
    }

    ArchiveStage addArchiveStage(String archiveFilePattern) {
        return addArchiveStage('Archive', archiveFilePattern)
    }

    ArchiveStage addArchiveStage(String stageName, String archiveFilePattern) {
        ArchiveStage archiveStage = new ArchiveStage(getPipelineConfiguration(), stageName)
        archiveStage.setArchiveFilePattern(archiveFilePattern)
        return addCommonStage(archiveStage)
    }

    CleanupStep addCleanupStep() {
        CleanupStep cleanupStep = new CleanupStep(getPipelineConfiguration())
        addStep(cleanupStep)
        return cleanupStep
    }

    CleanupStep addCleanupStep(String relativeDirectory) {
        CleanupStep cleanupStep = new CleanupStep(getPipelineConfiguration(), relativeDirectory)
        addStep(cleanupStep)
        return cleanupStep
    }

    DetectStage addDetectStage(String detectCommand) {
        return addDetectStage('Detect', detectCommand)
    }

    DetectStage addDetectStage(String stageName, String detectCommand) {
        detectCommand = detectCommand + ' --detect.project.codelocation.unmap=true --detect.tools.excluded=SIGNATURE_SCAN --detect.force.success=true'

        DetectStage detectStage = new DetectStage(getPipelineConfiguration(), stageName, getJenkinsProperty(HUB_DETECT_URL), detectCommand)
        return addCommonStage(detectStage)
    }

    EmailPipelineWrapper addEmailPipelineWrapper(String recipientList) {
        EmailPipelineWrapper emailPipelineWrapper = new EmailPipelineWrapper(getPipelineConfiguration(), recipientList, getJenkinsProperty(JOB_NAME), getJenkinsProperty(BUILD_NUMBER), getJenkinsProperty(BUILD_URL))
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    EmailPipelineWrapper addEmailPipelineWrapper(String wrapperName, String recipientList) {
        EmailPipelineWrapper emailPipelineWrapper = new EmailPipelineWrapper(getPipelineConfiguration(), wrapperName, recipientList, getJenkinsProperty(JOB_NAME), getJenkinsProperty(BUILD_NUMBER), getJenkinsProperty(BUILD_URL))
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    GithubReleaseStage addGithubReleaseStage(String branch) {
        return addGithubReleaseStage('GitHub Release', branch)
    }

    GithubReleaseStage addGithubReleaseStage(String stageName, String branch) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), stageName, getJenkinsBooleanProperty(RUN_RELEASE), branch)
        return addCommonStage(githubReleaseStage)
    }

    GithubReleaseStage addGithubReleaseStageByFile(String branch, String artifactFile) {
        return addGithubReleaseStageByFile('GitHub Release', branch, artifactFile)
    }

    GithubReleaseStage addGithubReleaseStageByFile(String stageName, String branch, String artifactFile) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), stageName, getJenkinsBooleanProperty(RUN_RELEASE), artifactFile, branch)
        return addCommonStage(githubReleaseStage)
    }

    GithubReleaseStage addGithubReleaseStageByPattern(String branch, String artifactPattern, String artifactDirectory) {
        return addGithubReleaseStageByPattern('GitHub Release', branch, artifactPattern, artifactDirectory)
    }

    GithubReleaseStage addGithubReleaseStageByPattern(String stageName, String branch, String artifactPattern, String artifactDirectory) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), stageName, getJenkinsBooleanProperty(RUN_RELEASE), artifactPattern, artifactDirectory, branch)
        return addCommonStage(githubReleaseStage)
    }

    GitStage addGitStage(String url, String branch, boolean gitPolling) {
        return addGitStage('Git', url, branch, gitPolling)
    }

    GitStage addGitStage(String stageName, String url, String branch, boolean gitPolling) {
        GitStage gitStage = new GitStage(getPipelineConfiguration(), stageName, url, branch)
        gitStage.setPoll(gitPolling)
        return addCommonStage(gitStage)
    }

    GradleStage addGradleStage(String gradleExe, String gradleOptions) {
        return addGradleStage('Gradle', gradleExe, gradleOptions)
    }

    GradleStage addGradleStage(String stageName, String gradleExe, String gradleOptions) {
        GradleStage gradleStage = new GradleStage(getPipelineConfiguration(), stageName)
        gradleStage.setGradleExe(gradleExe)
        gradleStage.setGradleOptions(gradleOptions)
        return addCommonStage(gradleStage)
    }

    JacocoStage addJacocoStage(LinkedHashMap jacocoOptions) {
        return addJacocoStage('Jacoco', jacocoOptions)
    }

    JacocoStage addJacocoStage(String stageName, LinkedHashMap jacocoOptions) {
        JacocoStage jacocoStage = new JacocoStage(getPipelineConfiguration(), stageName)
        jacocoStage.setJacocoOptions(jacocoOptions)
        return addCommonStage(jacocoStage)
    }

    JunitStageWrapper addJunitStageWrapper(Stage stage, LinkedHashMap junitOptions) {
        return addJunitStageWrapper(stage, 'Junit', junitOptions)
    }

    JunitStageWrapper addJunitStageWrapper(Stage stage, String stageName, LinkedHashMap junitOptions) {
        JunitStageWrapper junitStageWrapper = new JunitStageWrapper(getPipelineConfiguration(), stageName)
        junitStageWrapper.setJunitOptions(junitOptions)
        junitStageWrapper.setRelativeDirectory(commonRunDirectory)
        stage.addStageWrapper(junitStageWrapper)
        return junitStageWrapper
    }

    MavenStage addMavenStage(String mavenToolName, String mavenOptions) {
        return addMavenStage('Maven', mavenToolName, mavenOptions)
    }

    MavenStage addMavenStage(String stageName, String mavenToolName, String mavenOptions) {
        MavenStage mavenStage = new MavenStage(getPipelineConfiguration(), stageName)
        mavenStage.setMavenOptions(mavenOptions)
        mavenStage.setMavenToolName(mavenToolName)
        return addCommonStage(mavenStage)
    }

    NextSnapshotStage addNextSnapshotStage(String buildTool, String exe, String branch) {
        addNextSnapshotStage('Next Snapshot', buildTool, exe, branch)
    }

    NextSnapshotStage addNextSnapshotStage(String stageName, String buildTool, String exe, String branch) {
        boolean runRelease = getJenkinsBooleanProperty(RUN_RELEASE)
        boolean runQARelease = getJenkinsBooleanProperty(RUN_QA_BUILD)
        NextSnapshotStage nextSnapshotStage = new NextSnapshotStage(getPipelineConfiguration(), stageName, runRelease, runQARelease, buildTool, exe, branch)
        return addCommonStage(nextSnapshotStage)
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String buildTool, String exe, String branch) {
        return addRemoveSnapshotStage('Remove Snapshot', buildTool, exe, branch)
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String stageName, String buildTool, String exe, String branch) {
        boolean runRelease = getJenkinsBooleanProperty(RUN_RELEASE)
        boolean runQARelease = getJenkinsBooleanProperty(RUN_QA_BUILD)
        RemoveSnapshotStage removeSnapshotStage = new RemoveSnapshotStage(getPipelineConfiguration(), stageName, runRelease, runQARelease, buildTool, exe, branch)
        return addCommonStage(removeSnapshotStage)
    }

    SetJdkStage addSetJdkStage() {
        SetJdkStage setJdkStage = new SetJdkStage(getPipelineConfiguration(), 'Set JDK')
        return addCommonStage(setJdkStage)
    }

    SetJdkStage addSetJdkStage(String jdkToolName) {
        return addSetJdkStage('Set JDK', jdkToolName)
    }

    SetJdkStage addSetJdkStage(String stageName, String jdkToolName) {
        SetJdkStage setJdkStage = new SetJdkStage(getPipelineConfiguration(), stageName)
        setJdkStage.setJdkToolName(jdkToolName)
        return addCommonStage(setJdkStage)
    }

    ApiTokenStage addApiTokenStage() {
        ApiTokenStage apiTokenStage = new ApiTokenStage(getPipelineConfiguration(), 'Black Duck Api Token')
        return addCommonStage(apiTokenStage)
    }

    ClosureStage addStage(String stageName, Closure closure) {
        return addCommonStage(new ClosureStage(getPipelineConfiguration(), stageName, closure))
    }

    ClosureStep addStep(Closure closure) {
        return addCommonStep(new ClosureStep(getPipelineConfiguration(), closure))
    }

    String determineGitBranch(String branch) {
        GitBranch gitBranch = new GitBranch(getPipelineConfiguration(), branch)
        return gitBranch.determineAndGetBranch()
    }

    private String getJenkinsProperty(String propertyName) {
        Objects.requireNonNull(propertyName, "You must provide a property name. Property: '${propertyName}'")

        String result = getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(propertyName)
        return StringUtils.trimToEmpty(result)
    }

    private boolean getJenkinsBooleanProperty(String propertyName) {
        String result = getJenkinsProperty(propertyName)
        return BooleanUtils.toBooleanDefaultIfNull(Boolean.valueOf(result), Boolean.FALSE)
    }

    private <T extends Stage> T addCommonStage(T stage) {
        stage.setRelativeDirectory(commonRunDirectory)
        addStage(stage)
        return stage
    }

    private <T extends Step> T addCommonStep(T step) {
        step.setRelativeDirectory(commonRunDirectory)
        addStep(step)
        return step
    }

    String getCommonRunDirectory() {
        return commonRunDirectory
    }

    void setCommonRunDirectory(final String commonRunDirectory) {
        this.commonRunDirectory = commonRunDirectory
    }

    void setDirectoryFromBranch(final String branch) {
        String directory = branch
        if (branch.contains('/')) {
            directory = StringUtils.substringAfterLast(branch, '/')
        }
        this.commonRunDirectory = directory
    }
}
