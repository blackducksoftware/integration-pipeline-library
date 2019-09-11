package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.ProjectUtils
import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.utilities.GithubBranchParser

class NextSnapshotStage extends Stage {
    private final JenkinsScriptWrapper scriptWrapper
    private final PipelineLogger logger
    private final String buildTool
    private final String exe

    private final String branch

    private String gitToolName = GitStage.DEFAULT_GIT_TOOL

    NextSnapshotStage(JenkinsScriptWrapper scriptWrapper, PipelineLogger logger, String stageName, String buildTool, String exe, String branch) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
        this.logger = logger
        this.buildTool = buildTool
        this.exe = exe
        this.branch = branch
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        ProjectUtils projectUtils = new ProjectUtils()
        projectUtils.initialize(scriptWrapper.getScript(), buildTool, exe)
        String newVersion = projectUtils.increaseSemver()
        if (newVersion.contains('-SNAPSHOT')) {
            logger.info("Using the next snapshot post release. ${newVersion}")
            def commitMessage = "Using the next snapshot post release ${newVersion}"
            String gitPath = scriptWrapper.tool(gitToolName)

            scriptWrapper.executeCommand("${gitPath} commit -a -m \"${commitMessage}\"")

            GithubBranchParser githubBranchParser = new GithubBranchParser()
            GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

            scriptWrapper.executeCommand("${gitPath} push ${githubBranchModel.getRemote()} ${githubBranchModel.getBranchName()}")
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
