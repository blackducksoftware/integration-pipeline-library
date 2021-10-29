package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.pipeline.utilities.ProjectUtils
import com.synopsys.integration.utilities.GithubBranchParser

class NextSnapshotStage extends Stage {
    private final boolean runRelease
    private final boolean runQARelease

    private final String buildTool
    private final String exe

    private final String branch
    private final String url
    private final String githubCredentialsId

    private String gitToolName = GitStage.DEFAULT_GIT_TOOL

    NextSnapshotStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, boolean runQARelease, String buildTool, String exe, String branch, String url, String githubCredentialsId) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease;
        this.runQARelease = runQARelease;
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

        String version = projectUtils.getProjectVersion()
        pipelineLogger.info("Post release updating the Project version '${version}'. Release: ${runRelease}, QA release: ${runQARelease}")

        pipelineLogger.info("Updating to next SNAPSHOT version")
        String newVersion = projectUtils.increaseSemver(runRelease, runQARelease)

        if (!newVersion.equals(version)) {
            String gitPath = jenkinsScriptWrapper.tool(gitToolName)

            def commitMessage = "Using the next snapshot post release ${newVersion}"
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
