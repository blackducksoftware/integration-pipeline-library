package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.exception.PrepareForReleaseException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.pipeline.utilities.ProjectUtils
import com.synopsys.integration.utilities.GithubBranchParser

class RemoveSnapshotStage extends Stage {
    private final boolean runRelease
    private final boolean runQARelease

    private final String buildTool
    private final String exe

    private final String branch

    private boolean checkAllDependencies = false
    private String gitToolName = GitStage.DEFAULT_GIT_TOOL

    RemoveSnapshotStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, boolean runQARelease, String buildTool, String exe, String branch) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease
        this.runQARelease = runQARelease
        this.buildTool = buildTool
        this.exe = exe
        this.branch = branch
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        if (!runRelease && !runQARelease) {
            getPipelineConfiguration().getLogger().info("Skipping the ${this.getClass().getSimpleName()} because this is not a release.")
            return
        }
        ProjectUtils projectUtils = new ProjectUtils(getPipelineConfiguration().getLogger(), getPipelineConfiguration().getScriptWrapper())
        projectUtils.initialize(buildTool, exe)
        boolean hasSnapshotDependencies = projectUtils.checkForSnapshotDependencies(checkAllDependencies)
        if (hasSnapshotDependencies) {
            String errorMessage = "Failing release preparation because of ${buildTool} SNAPSHOT dependencies"
            throw new PrepareForReleaseException(errorMessage)
        }
        String version = projectUtils.getProjectVersion()
        getPipelineConfiguration().getLogger().info("Updating the Project version '${version}'. Release: ${runRelease}, QA release: ${runQARelease}")

        getPipelineConfiguration().getLogger().info("Removing SNAPSHOT from the Project Version")
        String newVersion = projectUtils.updateVersionForRelease(runRelease, runQARelease)

        if (!newVersion.equals(version)) {
            getPipelineConfiguration().getLogger().debug("Commiting the release ${newVersion}")
            String gitPath = getPipelineConfiguration().getScriptWrapper().tool(gitToolName)

            getPipelineConfiguration().getScriptWrapper().executeCommand("${gitPath} commit -am \"Release ${newVersion}\"")

            GithubBranchParser githubBranchParser = new GithubBranchParser()
            GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

            getPipelineConfiguration().getScriptWrapper().executeCommand("${gitPath} push ${githubBranchModel.getRemote()} ${githubBranchModel.getBranchName()}")
            getPipelineConfiguration().getLogger().debug("Pushing release to branch ${branch}")
        }
        getPipelineConfiguration().getScriptWrapper().setJenkinsProperty('GITHUB_RELEASE_VERSION', newVersion)
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

}
