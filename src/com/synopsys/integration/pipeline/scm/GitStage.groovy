package com.synopsys.integration.pipeline.scm

import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class GitStage extends Stage {
    public static final String DEFAULT_GIT_TOOL = 'Default'
    public static final boolean DEFAULT_GIT_CHANGELOG = false
    public static final boolean DEFAULT_GIT_POLL = false


    private final JenkinsScriptWrapper scriptWrapper
    private final String url
    private final String branch
    private String gitToolName = DEFAULT_GIT_TOOL
    private boolean changelog = DEFAULT_GIT_CHANGELOG
    private boolean poll = DEFAULT_GIT_POLL


    GitStage(JenkinsScriptWrapper scriptWrapper, String stageName, String url, String branch) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
        this.url = url
        this.branch = branch
    }

    @Override
    void stageExecution() {
        PipelineLogger pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
        scriptWrapper.checkout(url, branch, gitToolName, changelog, poll)
        // Need to do this because Jenkins checks out a detached HEAD
        Object checkoutResult = scriptWrapper.executeCommand("git checkout ${branch}")
        pipelineLogger.info("checkoutResult ${checkoutResult}")
        // Do a hard reset in order to clear out any local changes/commits
        Object resetResult = scriptWrapper.executeCommand("git reset --hard ${branch}")
        pipelineLogger.info("resetResult ${resetResult}")
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
