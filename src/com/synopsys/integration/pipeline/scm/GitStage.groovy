package com.synopsys.integration.pipeline.scm

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.utilities.GithubBranchParser

class GitStage extends Stage {
    public static final String DEFAULT_GIT_TOOL = 'Default'
    public static final boolean DEFAULT_GIT_CHANGELOG = false
    public static final boolean DEFAULT_GIT_POLL = false

    private final String url
    private final String branch
    private String gitToolName = DEFAULT_GIT_TOOL
    private boolean changelog = DEFAULT_GIT_CHANGELOG
    private boolean poll = DEFAULT_GIT_POLL

    GitStage(PipelineConfiguration pipelineConfiguration, String stageName, String url, String branch) {
        super(pipelineConfiguration, stageName)
        this.url = url
        this.branch = branch
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        poll = setPollFromEnvironment()

        getPipelineConfiguration().getLogger().info("Pulling branch '${branch}' from repo '${url}")
        getPipelineConfiguration().getScriptWrapper().checkout(url, branch, gitToolName, changelog, poll)

        String gitPath = getPipelineConfiguration().getScriptWrapper().tool(gitToolName)

        GithubBranchParser githubBranchParser = new GithubBranchParser()
        GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

        // Need to do this because Jenkins checks out a detached HEAD
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gitPath} checkout ${githubBranchModel.getBranchName()}")
        // Do a hard reset in order to clear out any local changes/commits
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gitPath} reset --hard ${githubBranchModel.getBranchName()}")
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

    private boolean setPollFromEnvironment() {
        def gitPolling = pipelineConfiguration.scriptWrapper.getJenkinsProperty('INT_GIT_POLLING')
        return gitPolling?.trim() ? Boolean.valueOf(gitPolling) : poll
    }
}
