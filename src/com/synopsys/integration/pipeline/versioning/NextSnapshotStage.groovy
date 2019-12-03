package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.pipeline.versioning.utilities.GradleUtils
import com.synopsys.integration.pipeline.versioning.utilities.ProjectUtils
import com.synopsys.integration.utilities.GithubBranchParser

class NextSnapshotStage extends Stage {
    private final boolean runRelease

    private final String buildTool
    private final String exe

    private final String branch

    private String gitToolName = GitStage.DEFAULT_GIT_TOOL

    NextSnapshotStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, String buildTool, String exe, String branch) {
        super(pipelineConfiguration, stageName)
        this.runRelease = runRelease;
        this.buildTool = buildTool
        this.exe = exe
        this.branch = branch
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        if (!runRelease) {
            getPipelineConfiguration().getLogger().info("Skipping the ${this.getClass().getSimpleName()} because this is not a release.")
            return
        }
        ProjectUtils projectUtils = new ProjectUtils(getPipelineConfiguration().getLogger(), getPipelineConfiguration().getScriptWrapper())
        projectUtils.initialize(buildTool, exe)
        String newVersion = projectUtils.increaseSemver()

        if (projectUtils instanceof GradleUtils) {
            GradleUtils gradleUtils = (GradleUtils) projectUtils
            gradleUtils.updateCommonGradlePluginVersion(false)
        }

        if (newVersion.contains('-SNAPSHOT')) {
            getPipelineConfiguration().getLogger().info("Using the next snapshot post release. ${newVersion}")
            def commitMessage = "Using the next snapshot post release ${newVersion}"
            String gitPath = getPipelineConfiguration().getScriptWrapper().tool(gitToolName)

            getPipelineConfiguration().getScriptWrapper().executeCommand("${gitPath} commit -a -m \"${commitMessage}\"")

            GithubBranchParser githubBranchParser = new GithubBranchParser()
            GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

            getPipelineConfiguration().getScriptWrapper().executeCommand("${gitPath} push ${githubBranchModel.getRemote()} ${githubBranchModel.getBranchName()}")
        } else {
            getPipelineConfiguration().getLogger().warn("Could not update the version to the next SNAPSHOT version.")
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
