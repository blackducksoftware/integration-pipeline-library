package com.blackduck.integration.pipeline


import com.blackduck.integration.pipeline.buildTool.GradleStage
import com.blackduck.integration.pipeline.buildTool.MavenStage
import com.blackduck.integration.pipeline.generic.ClosureStage
import com.blackduck.integration.pipeline.generic.ClosureStep
import com.blackduck.integration.pipeline.results.ArchiveStage
import com.blackduck.integration.pipeline.results.JacocoStage
import com.blackduck.integration.pipeline.results.JunitStageWrapper
import com.blackduck.integration.pipeline.utilities.GradleUtils
import com.blackduck.integration.pipeline.versioning.GithubAssetStage
import com.blackduck.integration.pipeline.versioning.GithubReleaseStage
import com.blackduck.integration.pipeline.versioning.GithubReleaseStageLegacy
import com.blackduck.integration.pipeline.versioning.NextSnapshotStage
import com.blackduck.integration.pipeline.versioning.RemoveSnapshotStage
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
            pipeline.setUrl(url)
        }

        com.blackduck.integration.pipeline.scm.GitStage gitStage = pipeline.addGitStage(url, gitBranch, gitPolling)
        gitStage.setChangelog(true)
        pipeline.setGithubCredentialsId(gitStage.getCredentialsId())

        pipeline.addApiTokenStage()

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

    com.blackduck.integration.pipeline.setup.CleanupStep addCleanupStep() {
        com.blackduck.integration.pipeline.setup.CleanupStep cleanupStep = new com.blackduck.integration.pipeline.setup.CleanupStep(getPipelineConfiguration())
        addStep(cleanupStep)
        return cleanupStep
    }

    com.blackduck.integration.pipeline.setup.CleanupStep addCleanupStep(String relativeDirectory) {
        com.blackduck.integration.pipeline.setup.CleanupStep cleanupStep = new com.blackduck.integration.pipeline.setup.CleanupStep(getPipelineConfiguration(), relativeDirectory)
        addStep(cleanupStep)
        return cleanupStep
    }

    com.blackduck.integration.pipeline.tools.DetectStage addDetectStage(String detectCommand) {
        return addDetectStage('Detect', detectCommand)
    }

    com.blackduck.integration.pipeline.tools.DetectStage addDetectStage(String stageName, String detectCommand) {
        detectCommand = detectCommand + ' --detect.project.codelocation.unmap=true --detect.tools.excluded=SIGNATURE_SCAN --detect.force.success=true'

        com.blackduck.integration.pipeline.tools.DetectStage detectStage = new com.blackduck.integration.pipeline.tools.DetectStage(getPipelineConfiguration(), stageName, getJenkinsProperty(HUB_DETECT_URL), detectCommand)
        return addCommonStage(detectStage)
    }

    com.blackduck.integration.pipeline.tools.DetectStage addDetectPopStage() {
        return addDetectPopStage("")
    }

    com.blackduck.integration.pipeline.tools.DetectStage addDetectPopStage(String detectCommand) {
        return addDetectPopStage("", detectCommand)
    }

    com.blackduck.integration.pipeline.tools.DetectStage addDetectPopStage(String stageNameSuffix, String detectCommand) {
        com.blackduck.integration.pipeline.tools.DetectStage detectStage = new com.blackduck.integration.pipeline.tools.DetectStage(getPipelineConfiguration(), "Detect " + stageNameSuffix, getJenkinsProperty(HUB_DETECT_URL), detectCommand)
        detectStage.addDetectParameters(com.blackduck.integration.pipeline.tools.DetectStage.DEFAULT_DETECT_SETTINGS)
        detectStageSigBDHub(detectStage)
        return addCommonStage(detectStage)
    }

    com.blackduck.integration.pipeline.tools.DetectStage addDetectPopSourceStage() {
        return addDetectPopSourceStage("")
    }

    com.blackduck.integration.pipeline.tools.DetectStage addDetectPopSourceStage(String detectCommand) {
        return addDetectPopStage('source', detectCommand)
    }

    com.blackduck.integration.pipeline.tools.DetectStage addDetectPopDockerStage(String imageName) {
        return addDetectPopDockerStage(imageName, "")
    }

    ArrayList<com.blackduck.integration.pipeline.tools.DetectStage> addDetectPopDockerStages(ArrayList<String> imageNames) {
        return addDetectPopDockerStages(imageNames, "")
    }

    ArrayList<com.blackduck.integration.pipeline.tools.DetectStage> addDetectPopDockerStages(ArrayList<String> imageNames, String detectCommand) {
        ArrayList<com.blackduck.integration.pipeline.tools.DetectStage> detectStages = []
        imageNames.each { imageName -> detectStages << addDetectPopDockerStage(imageName, detectCommand) }
        return detectStages
    }

    com.blackduck.integration.pipeline.tools.DetectStage addDetectPopDockerStage(String imageName, String detectCommand) {
        com.blackduck.integration.pipeline.tools.DockerImage dockerImage = new com.blackduck.integration.pipeline.tools.DockerImage(pipelineConfiguration, imageName)
        com.blackduck.integration.pipeline.tools.DetectStage detectDockerStage = addDetectPopStage(dockerImage.getBdProjectName(), detectCommand)
        detectDockerStage.setDockerImage(dockerImage)
        return detectDockerStage
    }

    void detectStageSigBDHub(com.blackduck.integration.pipeline.tools.DetectStage detectStage) {
        detectStage.setBlackduckConnection(getJenkinsProperty(SIG_BD_HUB_SERVER_URL), getJenkinsProperty(SIG_BD_HUB_API_TOKEN))
    }

    void detectStageHubBDPop(com.blackduck.integration.pipeline.tools.DetectStage detectStage) {
        detectStage.setBlackduckConnection(getJenkinsProperty(HUB_BDS_POP_SERVER_URL), getJenkinsProperty(ENG_HUB_PRD_TOKEN))
    }

    com.blackduck.integration.pipeline.email.EmailPipelineWrapper addEmailPipelineWrapper(String recipientList) {
        com.blackduck.integration.pipeline.email.EmailPipelineWrapper emailPipelineWrapper = new com.blackduck.integration.pipeline.email.EmailPipelineWrapper(getPipelineConfiguration(), recipientList, getJenkinsProperty(JOB_NAME), getJenkinsProperty(BUILD_NUMBER), getJenkinsProperty(BUILD_URL))
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    com.blackduck.integration.pipeline.email.EmailPipelineWrapper addEmailPipelineWrapper(String wrapperName, String recipientList) {
        com.blackduck.integration.pipeline.email.EmailPipelineWrapper emailPipelineWrapper = new com.blackduck.integration.pipeline.email.EmailPipelineWrapper(getPipelineConfiguration(), wrapperName, recipientList, getJenkinsProperty(JOB_NAME), getJenkinsProperty(BUILD_NUMBER), getJenkinsProperty(BUILD_URL))
        emailPipelineWrapper.setRelativeDirectory(commonRunDirectory)
        addPipelineWrapper(emailPipelineWrapper)
        return emailPipelineWrapper
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacy(String branch) {
        return addGithubReleaseStageLegacy('Legacy Github Release', branch)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacy(String stageName, String branch) {
        GithubReleaseStageLegacy githubReleaseStage = new GithubReleaseStageLegacy(getPipelineConfiguration(), stageName, getJenkinsBooleanProperty(RUN_RELEASE), branch)
        return addCommonStage(githubReleaseStage)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacyByFile(String branch, String artifactFile) {
        return addGithubReleaseStageLegacyByFile('Legacy Github Release', branch, artifactFile)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacyByFile(String stageName, String branch, String artifactFile) {
        GithubReleaseStageLegacy githubReleaseStageLegacy = new GithubReleaseStageLegacy(getPipelineConfiguration(), stageName, getJenkinsBooleanProperty(RUN_RELEASE), artifactFile, branch)
        return addCommonStage(githubReleaseStageLegacy)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacyByPattern(String branch, String artifactPattern, String artifactDirectory) {
        return addGithubReleaseStageLegacyByPattern('Legacy Github Release', branch, artifactPattern, artifactDirectory)
    }

    GithubReleaseStageLegacy addGithubReleaseStageLegacyByPattern(String stageName, String branch, String artifactPattern, String artifactDirectory) {
        GithubReleaseStageLegacy githubReleaseStageLegacy = new GithubReleaseStageLegacy(getPipelineConfiguration(), stageName, getJenkinsBooleanProperty(RUN_RELEASE), artifactPattern, artifactDirectory, branch)
        return addCommonStage(githubReleaseStageLegacy)
    }

    GithubReleaseStage addGithubReleaseStage() {
        GithubReleaseStage githubReleaseStage = new GithubReleaseStage(getPipelineConfiguration(), 'Github Release', releaseOwner, releaseRepo, githubCredentialsId)
        return addCommonStage(githubReleaseStage)
    }

    GithubReleaseStage addGithubReleaseStage(String glob) {
        GithubReleaseStage githubReleaseStage = addGithubReleaseStage()
        addGithubAssetStage(glob)
        return githubReleaseStage
    }

    GithubAssetStage addGithubAssetStage(String glob) {
        GithubAssetStage githubAssetStage = new GithubAssetStage(getPipelineConfiguration(), 'Github Asset Release', glob, githubCredentialsId)
        return addCommonStage(githubAssetStage)
    }

    com.blackduck.integration.pipeline.scm.GitStage addGitStage(String url, String branch, boolean gitPolling) {
        return addGitStage('Git', url, branch, gitPolling)
    }

    com.blackduck.integration.pipeline.scm.GitStage addGitStage(String stageName, String url, String branch, boolean gitPolling) {
        com.blackduck.integration.pipeline.scm.GitStage gitStage = new com.blackduck.integration.pipeline.scm.GitStage(getPipelineConfiguration(), stageName, url, branch)
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
        return addSetCleanedGradleVersionStage(com.blackduck.integration.pipeline.tools.DetectStage.DETECT_PROJECT_VERSION_NAME_OVERRIDE)
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

    JunitStageWrapper addJunitStageWrapper(com.blackduck.integration.pipeline.model.Stage stage, LinkedHashMap junitOptions) {
        return addJunitStageWrapper(stage, 'Junit', junitOptions)
    }

    JunitStageWrapper addJunitStageWrapper(com.blackduck.integration.pipeline.model.Stage stage, String stageName, LinkedHashMap junitOptions) {
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
        return addRemoveSnapshotStage('Remove Snapshot', buildTool, exe, branch, '')
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String buildTool, String exe, String branch, String buildCommand) {
        addRemoveSnapshotStage('Remove Snapshot', buildTool, exe, branch, buildCommand)
    }

    RemoveSnapshotStage addRemoveSnapshotStage(String stageName, String buildTool, String exe, String branch, String buildCommand) {
        boolean runRelease = getJenkinsBooleanProperty(RUN_RELEASE)
        boolean runQARelease = getJenkinsBooleanProperty(RUN_QA_BUILD)
        RemoveSnapshotStage removeSnapshotStage = new RemoveSnapshotStage(getPipelineConfiguration(), stageName, runRelease, runQARelease, buildTool, exe, buildCommand, branch, getUrl(), getGithubCredentialsId())
        return addCommonStage(removeSnapshotStage)
    }

    com.blackduck.integration.pipeline.setup.SetJdkStage addSetJdkStage() {
        com.blackduck.integration.pipeline.setup.SetJdkStage setJdkStage = new com.blackduck.integration.pipeline.setup.SetJdkStage(getPipelineConfiguration(), 'Set JDK')
        return addCommonStage(setJdkStage)
    }

    com.blackduck.integration.pipeline.setup.SetJdkStage addSetJdkStage(String jdkToolName) {
        return addSetJdkStage('Set JDK', jdkToolName)
    }

    com.blackduck.integration.pipeline.setup.SetJdkStage addSetJdkStage(String stageName, String jdkToolName) {
        com.blackduck.integration.pipeline.setup.SetJdkStage setJdkStage = new com.blackduck.integration.pipeline.setup.SetJdkStage(getPipelineConfiguration(), stageName)
        setJdkStage.setJdkToolName(jdkToolName)
        return addCommonStage(setJdkStage)
    }

    com.blackduck.integration.pipeline.setup.ApiTokenStage addApiTokenStage() {
        com.blackduck.integration.pipeline.setup.ApiTokenStage apiTokenStage = new com.blackduck.integration.pipeline.setup.ApiTokenStage(getPipelineConfiguration(), 'Black Duck Api Token')
        return addCommonStage(apiTokenStage)
    }

    com.blackduck.integration.pipeline.tools.PublishToGCR addPublishToGCR(String gcrRepo) {
        com.blackduck.integration.pipeline.tools.PublishToGCR publishToGCR = new com.blackduck.integration.pipeline.tools.PublishToGCR(getPipelineConfiguration(), 'Publish Images to GCR', gcrRepo)
        return addCommonStage(publishToGCR)
    }

    ClosureStage addStage(String stageName, Closure closure) {
        return addCommonStage(new ClosureStage(getPipelineConfiguration(), stageName, closure))
    }

    ClosureStep addStep(Closure closure) {
        return addCommonStep(new ClosureStep(getPipelineConfiguration(), closure))
    }

    String determineGitBranch(String branch) {
        com.blackduck.integration.pipeline.scm.GitBranch gitBranch = new com.blackduck.integration.pipeline.scm.GitBranch(getPipelineConfiguration(), branch)
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

    private <T extends com.blackduck.integration.pipeline.model.Stage> T addCommonStage(T stage) {
        stage.setRelativeDirectory(commonRunDirectory)
        addStage(stage)
        return stage
    }

    private <T extends com.blackduck.integration.pipeline.model.Step> T addCommonStep(T step) {
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
        String removeHttp = null
        if (url.contains('https')) {
            removeHttp = StringUtils.substringAfterLast(url, '//')
        } else if (url.contains(':')) {
            removeHttp = StringUtils.substringAfterLast(url, ':')
        }

        removeHttp = StringUtils.substringBeforeLast(removeHttp, '.')
        String[] urlParameters = removeHttp.split("/")

        assert urlParameters.size() > 2: "Could not correctly split url to determine Owner and Repo: " + url
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