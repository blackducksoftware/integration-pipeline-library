package com.synopsys.integration.pipeline.scm

import com.cloudbees.groovy.cps.NonCPS
import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.utilities.GithubBranchParser
import hudson.model.BuildListener
import hudson.model.Cause
import hudson.model.Hudson
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun

class GitStage extends Stage {
    public static final String DEFAULT_GIT_TOOL = 'Default'
    public static final boolean DEFAULT_GIT_CHANGELOG = false
    public static final boolean DEFAULT_GIT_POLL = false
    public static final String DEFAULT_BRANCH_NAME = 'origin/master'

    private final String url
    private String branch
    private String branchSource
    private String gitToolName = DEFAULT_GIT_TOOL
    private boolean changelog = DEFAULT_GIT_CHANGELOG
    private boolean poll = DEFAULT_GIT_POLL

    GitStage(PipelineConfiguration pipelineConfiguration, String stageName, String url, String branch) {
        super(pipelineConfiguration, stageName)
        this.url = url
        this.branch = branch
        this.branchSource = 'constructor'
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getLogger().info("branch is set from ${branchSource}")
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

    void determineAndSetBranch() {
        WorkflowRun currentBuild = pipelineConfiguration.getScriptWrapper().currentBuild().getRunWrapper().getRawBuild() as WorkflowRun
        Cause.UpstreamCause initiatingUpstreamCause = determineUpstreamCause(currentBuild)

        if (null != initiatingUpstreamCause) {
            WorkflowRun build = getBuild(initiatingUpstreamCause)
            BuildListener buildListener = build.getListener()
            String branchFromCause = initiatingUpstreamCause.getUpstreamRun().getEnvironment(buildListener)['BRANCH']

            if (branchFromCause?.trim()) {
                setBranch(branchFromCause)
                setBranchSource('upstream build ' + build.toString())
            } else if (branch?.trim()) {
                setBranch(DEFAULT_BRANCH_NAME)
                setBranchSource('default setting')
            }
        }
    }

    private Cause.UpstreamCause determineUpstreamCause(WorkflowRun build) {
        Cause.UpstreamCause currentUpstreamCause = build.getCause(Cause.UpstreamCause)
        def nextUpstreamCause = null

        if (null != currentUpstreamCause) {
            WorkflowRun upstreamBuild = getBuild(currentUpstreamCause)
            nextUpstreamCause = determineUpstreamCause(upstreamBuild)
        }
        return (null != nextUpstreamCause) ? nextUpstreamCause : currentUpstreamCause
    }

    @NonCPS
    private static WorkflowRun getBuild(Cause.UpstreamCause cause) {
        Hudson hudson = Jenkins.get() as Hudson
        String jobName = cause.getUpstreamProject()
        int buildNumber = cause.getUpstreamBuild()
        WorkflowJob workflowJob = hudson.getItemByFullName(jobName, WorkflowJob)
        WorkflowRun workflowRun = workflowJob.getBuildByNumber(buildNumber)
        return workflowRun
    }

    String getBranch() {
        return branch
    }

    void setBranch(String branch) {
        this.branch = branch
    }

    String getBranchSource() {
        return branchSource
    }

    void setBranchSource(String branchSource) {
        this.branchSource = branchSource
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
