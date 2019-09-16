package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.ProjectUtils
import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.utilities.GithubBranchParser

class NextSnapshotStage extends Stage {
    private final PipelineLogger logger
    private final boolean runRelease

    private final String buildTool
    private final String exe

    private final String branch

    private String gitToolName = GitStage.DEFAULT_GIT_TOOL

    NextSnapshotStage(PipelineLogger logger, String stageName, boolean runRelease, String buildTool, String exe, String branch) {
        super(stageName)
        this.logger = logger
        this.runRelease = runRelease;
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
        String newVersion = projectUtils.increaseSemver()
        if (newVersion.contains('-SNAPSHOT')) {
            logger.info("Using the next snapshot post release. ${newVersion}")
            def commitMessage = "Using the next snapshot post release ${newVersion}"
            String gitPath = getScriptWrapper().tool(gitToolName)

            getScriptWrapper().executeCommand("${gitPath} commit -a -m \"${commitMessage}\"")

            GithubBranchParser githubBranchParser = new GithubBranchParser()
            GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

            getScriptWrapper().executeCommand("${gitPath} push ${githubBranchModel.getRemote()} ${githubBranchModel.getBranchName()}")
        } else {
            logger.warn("Could not update the version to the next SNAPSHOT version.")
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

    String getGitToolName() {
        return gitToolName
    }

    void setGitToolName(final String gitToolName) {
        this.gitToolName = gitToolName
    }

}
