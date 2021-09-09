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
    public static final String DEFAULT_BRANCH_NAME = 'origin/master'

    private final String url
    private String branch
    private String gitToolName = DEFAULT_GIT_TOOL
    private boolean changelog = DEFAULT_GIT_CHANGELOG
    private boolean poll = DEFAULT_GIT_POLL

    GitStage(PipelineConfiguration pipelineConfiguration, String stageName, String url, String branch) {
        super(pipelineConfiguration, stageName)
        this.url = url
        this.branch = (branch?.trim()) ? branch : DEFAULT_BRANCH_NAME
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getLogger().info("Pulling branch '${branch}' from repo '${url}'")
        getPipelineConfiguration().getScriptWrapper().checkout(url, branch, gitToolName, changelog, poll)

        String gitPath = getPipelineConfiguration().getScriptWrapper().tool(gitToolName)

        GithubBranchParser githubBranchParser = new GithubBranchParser()
        GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

        // Need to do this because Jenkins checks out a detached HEAD
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gitPath} checkout ${githubBranchModel.getBranchName()}")
        // Do a hard reset in order to clear out any local changes/commits
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gitPath} reset --hard ${githubBranchModel.getBranchName()}")
    }

    String getBranch() {
        return branch
    }

    void setBranch(String branch) {
        this.branch = branch
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
