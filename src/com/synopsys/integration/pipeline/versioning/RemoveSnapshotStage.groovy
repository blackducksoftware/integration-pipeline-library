package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.ProjectUtils
import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.exception.PrepareForReleaseException
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.utilities.GithubBranchParser

class RemoveSnapshotStage extends Stage {
    private final PipelineLogger logger
    private final boolean runRelease

    private final String buildTool
    private final String exe

    private final String branch

    private boolean checkAllDependencies = false
    private String gitToolName = GitStage.DEFAULT_GIT_TOOL

    RemoveSnapshotStage(PipelineLogger logger, String stageName, boolean runRelease, String buildTool, String exe, String branch) {
        super(stageName)
        this.logger = logger
        this.runRelease = runRelease
        this.buildTool = buildTool
        this.exe = exe
        this.branch = branch
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        if (!runRelease) {
            logger.info("Skipping the ${this.getClass().getSimpleName()} because this is not a release.")
            return
        }
        ProjectUtils projectUtils = new ProjectUtils()
        projectUtils.initialize(getScriptWrapper().getScript(), buildTool, exe)
        boolean hasSnapshotDependencies = projectUtils.checkForSnapshotDependencies(checkAllDependencies)
        if (hasSnapshotDependencies) {
            String errorMessage = "Failing release preparation because of ${buildTool} SNAPSHOT dependencies"
            throw new PrepareForReleaseException(errorMessage)
        }
        def version = projectUtils.getProjectVersion()
        if (version.contains('-SNAPSHOT')) {
            logger.info("Removing SNAPSHOT from the Project Version")
            String newVersion = projectUtils.removeSnapshotFromProjectVersion()
            logger.debug("Commiting the release ${newVersion}")
            String gitPath = getScriptWrapper().tool(gitToolName)

            getScriptWrapper().executeCommand("${gitPath} commit -am \"Release ${newVersion}\"")

            GithubBranchParser githubBranchParser = new GithubBranchParser()
            GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

            getScriptWrapper().executeCommand("${gitPath} push ${githubBranchModel.getRemote()} ${githubBranchModel.getBranchName()}")
            logger.debug("Pushing release to branch ${branch}")

            getScriptWrapper().env().GITHUB_RELEASE_VERSION = newVersion
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
