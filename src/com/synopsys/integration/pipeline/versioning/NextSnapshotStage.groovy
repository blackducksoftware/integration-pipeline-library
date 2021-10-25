package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
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

    private String gitToolName = GitStage.DEFAULT_GIT_TOOL

    NextSnapshotStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, boolean runQARelease, String buildTool, String exe, String branch) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease;
        this.runQARelease = runQARelease;
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

        String version = projectUtils.getProjectVersion()
        getPipelineConfiguration().getLogger().info("Post release updating the Project version '${version}'. Release: ${runRelease}, QA release: ${runQARelease}")

        String newVersion = projectUtils.increaseSemver(runRelease, runQARelease)
        if (!newVersion.equals(version)) {
            getPipelineConfiguration().getLogger().info("Using the next snapshot post release. ${newVersion}")
            def commitMessage = "Using the next snapshot post release ${newVersion}"
            String gitPath = getPipelineConfiguration().getScriptWrapper().tool(gitToolName)

            getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gitPath} commit -a -m \"${commitMessage}\"")

            GithubBranchParser githubBranchParser = new GithubBranchParser()
            GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

            getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gitPath} push ${githubBranchModel.getRemote()} ${githubBranchModel.getBranchName()}")
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
