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
import com.synopsys.integration.pipeline.tools.DockerImage
import com.synopsys.integration.pipeline.utilities.GradleUtils
import com.synopsys.integration.pipeline.versioning.GithubAssetStage
import com.synopsys.integration.pipeline.versioning.GithubReleaseStageLegacy
import com.synopsys.integration.pipeline.versioning.GithubReleaseStage
import com.synopsys.integration.pipeline.versioning.NextSnapshotStage
import com.synopsys.integration.pipeline.versioning.RemoveSnapshotStage
import org.apache.commons.lang3.BooleanUtils
import org.apache.commons.lang3.StringUtils
import org.jenkinsci.plugins.workflow.cps.CpsScript

class SimplePipeline extends Pipeline {
    public static final String GRADLE_BUILD_TOOL = 'gradle'
    public static final String MAVEN_BUILD_TOOL = 'maven'

    public static final String PROJECT_VERSION = 'PROJECT_VERSION'

    public static final String RUN_RELEASE = 'RUN_RELEASE'
    public static final String RUN_QA_BUILD = 'RELEASE_QA_BUILD'

    public static final String BUILD_NUMBER = 'BUILD_NUMBER'
    public static final String JOB_NAME = 'JOB_NAME'
    public static final String BUILD_URL = 'BUILD_URL'
    public static final String HUB_DETECT_URL = 'HUB_DETECT_URL'

    public static final String SIG_BD_HUB_SERVER_URL = 'SIG_BD_HUB_SERVER_URL'
    public static final String SIG_BD_HUB_API_TOKEN = 'SIG_BD_HUB_API_TOKEN'
    public static final String HUB_BDS_POP_SERVER_URL = 'HUB_BDS_POP_SERVER_URL'
    public static final String ENG_HUB_PRD_TOKEN = 'ENG_HUB_PRD_TOKEN'

    static SimplePipeline COMMON_PIPELINE(CpsScript script, String branch, String relativeDirectory, String url, String jdkToolName, boolean gitPolling) {
        return COMMON_PIPELINE(script, branch, relativeDirectory, url, jdkToolName, gitPolling, false)
    }

    static SimplePipeline COMMON_PIPELINE(CpsScript script, String branch, String relativeDirectory, String url, String jdkToolName, boolean gitPolling, boolean isPopBuild) {
        SimplePipeline pipeline = new SimplePipeline(script, relativeDirectory)
        pipeline.addCleanupStep(relativeDirectory)
        pipeline.addSetJdkStage(jdkToolName)

        String gitBranch = branch

        if (isPopBuild) {
            pipeline.setDirectoryFromUrl(url)
        } else {
            gitBranch = pipeline.determineGitBranch(branch)
            pipeline.setDirectoryFromBranch(gitBranch)
        }

        GitStage gitStage = pipeline.addGitStage(url, gitBranch, gitPolling)
        gitStage.setChangelog(true)

        pipeline.addApiTokenStage()

        pipeline.setUrl(url)
        pipeline.setDirectoryFromBranch(branch)
        pipeline.setGithubCredentialsId(gitStage.getCredentialsId())

        return pipeline
    }

    public String commonRunDirectory
    public String url
    public String githubCredentialsId
    public String releaseOwner
    public String releaseRepo

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

    DetectStage addDetectPopStage() {
        return addDetectPopStage("")
    }

    DetectStage addDetectPopStage(String detectCommand) {
        return addDetectPopStage("", detectCommand)
    }

    DetectStage addDetectPopStage(String stageNameSuffix, String detectCommand) {
        DetectStage detectStage = new DetectStage(getPipelineConfiguration(), "Detect " + stageNameSuffix, getJenkinsProperty(HUB_DETECT_URL), detectCommand)
        detectStage.addDetectParameters(DetectStage.DEFAULT_DETECT_SETTINGS)
        detectStageSigBDHub(detectStage)
        return addCommonStage(detectStage)
    }

    DetectStage addDetectPopSourceStage() {
        return addDetectPopSourceStage("")
    }

    DetectStage addDetectPopSourceStage(String detectCommand) {
        return addDetectPopStage('source', detectCommand)
    }

    DetectStage addDetectPopDockerStage(String imageName) {
        return addDetectPopDockerStage(imageName, "")
    }

    ArrayList<DetectStage> addDetectPopDockerStages(ArrayList<String> imageNames) {
        return addDetectPopDockerStages(imageNames, "")
    }

    ArrayList<DetectStage> addDetectPopDockerStages(ArrayList<String> imageNames, String detectCommand) {
        ArrayList<DetectStage> detectStages = []
        imageNames.each { imageName -> detectStages << addDetectPopDockerStage(imageName, detectCommand) }
        return detectStages
    }

    DetectStage addDetectPopDockerStage(String imageName, String detectCommand) {
        DockerImage dockerImage = new DockerImage(pipelineConfiguration, imageName)
        DetectStage detectDockerStage = addDetectPopStage(dockerImage.getBdProjectName(), detectCommand)
        detectDockerStage.setDockerImage(dockerImage)
        return detectDockerStage
    }

    void detectStageSigBDHub(DetectStage detectStage) {
        detectStage.setBlackduckConnection(getJenkinsProperty(SIG_BD_HUB_SERVER_URL), getJenkinsProperty(SIG_BD_HUB_API_TOKEN))
    }

    void detectStageHubBDPop(DetectStage detectStage) {
        detectStage.setBlackduckConnection(getJenkinsProperty(HUB_BDS_POP_SERVER_URL), getJenkinsProperty(ENG_HUB_PRD_TOKEN))
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

    GithubReleaseStageLegacy addGithubReleaseStageLegacy(String branch) {
        return addGithubReleaseStageLegacy('GitHub Release', branch)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacy(String stageName, String branch) {
        GithubReleaseStageLegacy githubReleaseStage = new GithubReleaseStageLegacy(getPipelineConfiguration(), stageName, getJenkinsBooleanProperty(RUN_RELEASE), branch)
        return addCommonStage(githubReleaseStage)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacyByFile(String branch, String artifactFile) {
        return addGithubReleaseStageLegacyByFile('GitHub Release', branch, artifactFile)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacyByFile(String stageName, String branch, String artifactFile) {
        GithubReleaseStageLegacy githubReleaseStageLegacy = new GithubReleaseStageLegacy(getPipelineConfiguration(), stageName, getJenkinsBooleanProperty(RUN_RELEASE), artifactFile, branch)
        return addCommonStage(githubReleaseStageLegacy)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacyByPattern(String branch, String artifactPattern, String artifactDirectory) {
        return addGithubReleaseStageLegacyByPattern('GitHub Release', branch, artifactPattern, artifactDirectory)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacyByPattern(String stageName, String branch, String artifactPattern, String artifactDirectory) {
        GithubReleaseStageLegacy githubReleaseStageLegacy = new GithubReleaseStageLegacy(getPipelineConfiguration(), stageName, getJenkinsBooleanProperty(RUN_RELEASE), artifactPattern, artifactDirectory, branch)
        return addCommonStage(githubReleaseStageLegacy)
    }

    GithubReleaseStage addGithubReleaseStage(String stageName) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), stageName, releaseOwner, releaseRepo, githubCredentialsId)
        return addCommonStage(githubReleaseStage)
    }

    GithubReleaseStage addGithubReleaseStage(String stageName, String[] assetNames) {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), stageName, releaseOwner, releaseRepo, githubCredentialsId)
        if (assetNames.length > 0)
            addGithubAssetStage("Add Github Attachments", assetNames)
        return addCommonStage(githubReleaseStage)
    }

    GithubAssetStage addGithubAssetStage(String stageName, String[] assetNames) {
        GithubAssetStage githubAssetStage = new GithubAssetStage(getPipelineConfiguration(), stageName, assetNames, githubCredentialsId)
        return addCommonStage(githubAssetStage)
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

    ClosureStage addSetGradleVersionStage() {
        return addSetGradleVersionStage(PROJECT_VERSION)
    }

    ClosureStage addSetGradleVersionStage(String gradleVariableName) {
        return addSetGradleVersionStage('./gradlew', gradleVariableName)
    }

    ClosureStage addSetGradleVersionStage(String gradleExe, String gradleVariableName) {
        Closure setGradleVersion = {
            GradleUtils gradleUtils = new GradleUtils(getLogger(), getScriptWrapper(), gradleExe)
            String gradleVersion = gradleUtils.getProjectVersion()
            getScriptWrapper().setJenkinsProperty(gradleVariableName, gradleVersion)
            getLogger().info("${gradleVariableName} set as ${gradleVersion}")
        }
        return addStage("Set ${gradleVariableName}", setGradleVersion)
    }

    ClosureStage addSetCleanedGradleVersionStage() {
        return addSetCleanedGradleVersionStage(DetectStage.DETECT_PROJECT_VERSION_NAME_OVERRIDE)
    }

    ClosureStage addSetCleanedGradleVersionStage(String gradleVariableName) {
        return addSetCleanedGradleVersionStage('./gradlew', gradleVariableName)
    }

    ClosureStage addSetCleanedGradleVersionStage(String gradleExe, String gradleVariableName) {
        Closure setGradleVersion = {
            GradleUtils gradleUtils = new GradleUtils(getLogger(), getScriptWrapper(), gradleExe)
            String gradleVersion = gradleUtils.getCleanedProjectVersion()
            getScriptWrapper().setJenkinsProperty(gradleVariableName, gradleVersion)
            getLogger().info("${gradleVariableName} set as ${gradleVersion}")
        }
        return addStage("Set ${gradleVariableName}", setGradleVersion)
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
        NextSnapshotStage nextSnapshotStage = new NextSnapshotStage(getPipelineConfiguration(), stageName, runRelease, runQARelease, buildTool, exe, branch, getUrl(), getGithubCredentialsId())
        return addCommonStage(nextSnapshotStage)
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String buildTool, String exe, String branch) {
        return addRemoveSnapshotStage('Remove Snapshot', buildTool, exe, branch)
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String stageName, String buildTool, String exe, String branch) {
        boolean runRelease = getJenkinsBooleanProperty(RUN_RELEASE)
        boolean runQARelease = getJenkinsBooleanProperty(RUN_QA_BUILD)
        RemoveSnapshotStage removeSnapshotStage = new RemoveSnapshotStage(getPipelineConfiguration(), stageName, runRelease, runQARelease, buildTool, exe, branch, getUrl(), getGithubCredentialsId())
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

    String getUrl() {
        return url
    }

    void setUrl(final String url) {
        this.url = url
        String removeHttp = url.substring(8, url.length())
        if (removeHttp.substring(removeHttp.length() - 4, removeHttp.length()) == '.git')
            removeHttp = removeHttp.substring(0, removeHttp.length() - 4)
        String[] urlParameters = removeHttp.split("/")
        releaseOwner = urlParameters[1]
        releaseRepo = urlParameters[2]
    }

    String getGithubCredentialsId() {
        return githubCredentialsId
    }

    void setGithubCredentialsId(String githubCredentialsId) {
        this.githubCredentialsId = githubCredentialsId
    }

    void setDirectoryFromBranch(final String branch) {
        String directory = branch
        if (branch.contains('/')) {
            directory = StringUtils.substringAfterLast(branch, '/')
        }
        this.commonRunDirectory = directory
    }

    void setDirectoryFromUrl(String url) {
        String inputUrl = StringUtils.substringAfterLast(url, '/')
        inputUrl = StringUtils.substringBeforeLast(inputUrl, '.')
        this.commonRunDirectory = inputUrl
    }
}
