package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.SimplePipeline
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.exception.PrepareForReleaseException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.pipeline.utilities.ProjectUtils
import com.synopsys.integration.utilities.GithubBranchParser

class RemoveSnapshotStage extends Stage {
    public static final String RELEASE_COMMIT_HASH = 'RELEASE_COMMIT_HASH'

    private final boolean runRelease
    private final boolean runQARelease

    private final String buildTool
    private final String exe

    public static String branch
    private final String url
    private final String githubCredentialsId

    private boolean checkAllDependencies = false
    private String gitToolName = GitStage.DEFAULT_GIT_TOOL

    RemoveSnapshotStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, boolean runQARelease, String buildTool, String exe, String branch, String url, String githubCredentialsId) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease
        this.runQARelease = runQARelease
        this.buildTool = buildTool
        this.exe = exe
        this.branch = branch
        this.url = url
        this.githubCredentialsId = githubCredentialsId
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        PipelineLogger pipelineLogger = getPipelineConfiguration().getLogger()
        JenkinsScriptWrapper jenkinsScriptWrapper = getPipelineConfiguration().getScriptWrapper()

        if (!runRelease && !runQARelease) {
            pipelineLogger.info("Skipping the ${this.getClass().getSimpleName()} because this is not a release.")
            return
        }

        pipelineLogger.debug("Initializing ${buildTool}")
        ProjectUtils projectUtils = new ProjectUtils(pipelineLogger, jenkinsScriptWrapper)
        projectUtils.initialize(buildTool, exe)

        pipelineLogger.info("Checking snapshot dependencies")
        boolean hasSnapshotDependencies = projectUtils.checkForSnapshotDependencies(checkAllDependencies)
        if (hasSnapshotDependencies) {
            String errorMessage = "Failing release preparation because of ${buildTool} SNAPSHOT dependencies"
            throw new PrepareForReleaseException(errorMessage)
        }

        String version = projectUtils.getProjectVersion()
        pipelineLogger.info("Updating the Project version '${version}'. Release: ${runRelease}, QA release: ${runQARelease}")

        pipelineLogger.info("Removing SNAPSHOT from the Project Version")
        String newVersion = projectUtils.updateVersionForRelease(runRelease, runQARelease)
        String gitPath = jenkinsScriptWrapper.tool(gitToolName)

        if (!newVersion.equals(version)) {
            def commitMessage = "Release ${newVersion}"
            pipelineLogger.info(commitMessage)
            jenkinsScriptWrapper.executeCommandWithException("${gitPath} commit -a -m \"${commitMessage}\"")

            pipelineLogger.info("Pushing ${newVersion} to branch ${branch}")
            if (url.startsWith(GitStage.GITHUB_HTTPS)) {
                jenkinsScriptWrapper.executeGitPushToGithub(pipelineConfiguration, url, githubCredentialsId, gitPath)
            } else {
                GithubBranchParser githubBranchParser = new GithubBranchParser()
                GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)
                jenkinsScriptWrapper.executeCommandWithException("${gitPath} push ${githubBranchModel.getRemote()} ${githubBranchModel.getBranchName()}")
            }
        }

        String commitHash = jenkinsScriptWrapper.executeCommand("${gitPath} rev-parse HEAD", true).trim()
        pipelineConfiguration.addToBuildDataMap(RELEASE_COMMIT_HASH, commitHash)

        jenkinsScriptWrapper.setJenkinsProperty(GithubReleaseStageLegacy.GITHUB_RELEASE_VERSION, newVersion)
        jenkinsScriptWrapper.setJenkinsProperty(RELEASE_COMMIT_HASH, commitHash)
    }

    String getBuildTool() {
        return buildTool
    }

    String getExe() {
        return exe
    }

    String getBranch() {
        return branch
    }

    String getUrl() {
        return url
    }

    boolean getCheckAllDependencies() {
        return checkAllDependencies
    }

    void setCheckAllDependencies(final boolean checkAllDependencies) {
        this.checkAllDependencies = checkAllDependencies
    }

    String getGitToolName() {
        return gitToolName
    }

    void setGitToolName(final String gitToolName) {
        this.gitToolName = gitToolName
    }

    String getGithubCredentialsId() {
        return githubCredentialsId
    }

}
