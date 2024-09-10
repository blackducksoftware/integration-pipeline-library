package com.blackduck.integration.pipeline.scm

import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Stage

class GitStage extends Stage {
    public static final String DEFAULT_GIT_TOOL = 'Default'
    public static final boolean DEFAULT_GIT_CHANGELOG = false
    public static final boolean DEFAULT_GIT_POLL = false
    public static final String DEFAULT_BRANCH_NAME = 'origin/master'
    public static final String DEFAULT_CREDENTIALS_ID = 'integrations-github-pat'
    public static final String GITHUB_HTTPS = 'https://github.com'

    private final String url
    private String branch
    private String gitToolName = DEFAULT_GIT_TOOL
    private boolean changelog = DEFAULT_GIT_CHANGELOG
    private boolean poll = DEFAULT_GIT_POLL

    private String credentialsId = DEFAULT_CREDENTIALS_ID

    GitStage(PipelineConfiguration pipelineConfiguration, String stageName, String url, String branch) {
        super(pipelineConfiguration, stageName)
        this.url = url
        this.branch = (branch?.trim()) ? branch : DEFAULT_BRANCH_NAME
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getLogger().info("Pulling branch '${branch}' from repo '${url}'")
        Map<String, String> checkoutData = getPipelineConfiguration().getScriptWrapper().checkout(url, branch, gitToolName, changelog, poll, credentialsId)
        checkoutData.each { k, v -> getPipelineConfiguration().addToBuildDataMap(k, v) }
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

    String getCredentialsId() {
        return credentialsId
    }

    void setCredentialsId(String credentialsId) {
        this.credentialsId = credentialsId
    }

}
