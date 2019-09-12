package com.synopsys.integration.pipeline.scm

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.utilities.GithubBranchParser

class GitStage extends Stage {
    public static final String DEFAULT_GIT_TOOL = 'Default'
    public static final boolean DEFAULT_GIT_CHANGELOG = false
    public static final boolean DEFAULT_GIT_POLL = false


    private final JenkinsScriptWrapper scriptWrapper
    private final PipelineLogger logger
    private final String url
    private final String branch
    private String gitToolName = DEFAULT_GIT_TOOL
    private boolean changelog = DEFAULT_GIT_CHANGELOG
    private boolean poll = DEFAULT_GIT_POLL


    GitStage(JenkinsScriptWrapper scriptWrapper, PipelineLogger logger, String stageName, String url, String branch) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
        this.logger = logger
        this.url = url
        this.branch = branch
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        logger.info("Pulling branch '${branch}' from repo '${url}")
        scriptWrapper.checkout(url, branch, gitToolName, changelog, poll)

        String gitPath = scriptWrapper.tool(gitToolName)

        GithubBranchParser githubBranchParser = new GithubBranchParser()
        GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

        // Need to do this because Jenkins checks out a detached HEAD
        scriptWrapper.executeCommandWithException("${gitPath} checkout ${githubBranchModel.getBranchName()}")
        // Do a hard reset in order to clear out any local changes/commits
        scriptWrapper.executeCommandWithException("${gitPath} reset --hard ${githubBranchModel.getBranchName()}")
    }

    String getGitToolName() {
        return gitToolName
    }

    void setGitToolName(final String gitToolName) {
        this.gitToolName = gitToolName
    }

    boolean getChangelog() {
        return changelog
    }

    void setChangelog(final boolean changelog) {
        this.changelog = changelog
    }

    boolean getPoll() {
        return poll
    }

    void setPoll(final boolean poll) {
        this.poll = poll
    }
}
