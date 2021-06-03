package com.synopsys.integration.pipeline.scm

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.utilities.GithubBranchParser
import hudson.model.BuildListener
import hudson.model.Cause.UpstreamCause
import hudson.model.Hudson
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun

class GitStage extends Stage {
    public static final String DEFAULT_GIT_TOOL = 'Default'
    public static final boolean DEFAULT_GIT_CHANGELOG = false
    public static final boolean DEFAULT_GIT_POLL = false
    public static final String COMMIT_HASH = 'COMMIT_HASH'

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
        getPipelineConfiguration().getLogger().info("DANA GET CAUSE")
        getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().currentBuild().getUpstreamCause().toString())
        getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().currentBuild().getUpstreamCause().getClass().name)

        getPipelineConfiguration().getLogger().info("Pulling branch '${branch}' from repo '${url}'")
        getPipelineConfiguration().getScriptWrapper().checkout(url, branch, gitToolName, changelog, poll)

        String gitPath = getPipelineConfiguration().getScriptWrapper().tool(gitToolName)

        GithubBranchParser githubBranchParser = new GithubBranchParser()
        GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)

        // Need to do this because Jenkins checks out a detached HEAD
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gitPath} checkout ${githubBranchModel.getBranchName()}")
        // Do a hard reset in order to clear out any local changes/commits
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${gitPath} reset --hard ${githubBranchModel.getBranchName()}")

        // Set COMMIT_HASH in environment to allow downstream jobs to use the same code
        String commitHash = getPipelineConfiguration().getScriptWrapper().executeCommand("git rev-parse HEAD", true).trim()
        getPipelineConfiguration().getScriptWrapper().setJenkinsProperty(COMMIT_HASH, commitHash)
        dana()
    }

    String determineBranch() {

        String commitHash = ''
        UpstreamCause upstreamCause = getPipelineConfiguration().getScriptWrapper().currentBuild().getUpstreamCause()

        if (null != upstreamCause) {
            int upstreamBuildNumber = upstreamCause.getUpstreamBuild()
            String upstreamJobName = upstreamCause.getUpstreamProject()
            BuildListener buildListener = Jenkins.get()
                    .getItemByFullName(upstreamJobName, WorkflowJob)
                    .getBuildByNumber(upstreamBuildNumber)
                    .getListener()
            commitHash = upstreamCause.getUpstreamRun().getEnvironment(buildListener)[COMMIT_HASH]
        }

        Jenkins.get().getItemByFullName(upstreamJobName, WorkflowJob).builds.getLastBuild().getEnvironment(buildListener)

        if (commitHash?.trim()) {
            return commitHash
        } else {
            GithubBranchParser githubBranchParser = new GithubBranchParser()
            GithubBranchModel githubBranchModel = githubBranchParser.parseBranch(branch)
            return githubBranchModel.getBranchName()
        }
    }

    void dana() {
        UpstreamCause upstreamCause = getPipelineConfiguration().getScriptWrapper().currentBuild().getUpstreamCause()

        if (null != upstreamCause) {
            int upstreamBuildNumber = upstreamCause.getUpstreamBuild()
            String upstreamJobName = upstreamCause.getUpstreamProject()

            Hudson hudson = Jenkins.get() as Hudson
            WorkflowJob workflowJob = hudson.getItemByFullName(upstreamJobName, WorkflowJob)
            WorkflowRun workflowRun = workflowJob.getBuildByNumber(upstreamBuildNumber)
            BuildListener buildListener = workflowRun.getListener()

            getPipelineConfiguration().getLogger().info("LIB:: " + hudson.getClass().name) //hudson.model.Hudson
            getPipelineConfiguration().getLogger().info("LIB:: " + workflowJob.getClass().name) //org.jenkinsci.plugins.workflow.job.WorkflowJob
            getPipelineConfiguration().getLogger().info("LIB:: " + workflowRun.getClass().name) //org.jenkinsci.plugins.workflow.job.WorkflowRun
            getPipelineConfiguration().getLogger().info("LIB:: " + buildListener.getClass().name) //hudson.model.StreamBuildListener
            getPipelineConfiguration().getLogger().info("LIB:: " + upstreamCause.getUpstreamRun().getClass().name) //org.jenkinsci.plugins.workflow.job.WorkflowRun
            getPipelineConfiguration().getLogger().info("LIB:: " + upstreamCause.getUpstreamRun().getClass().name) //org.jenkinsci.plugins.workflow.job.WorkflowRun

            getPipelineConfiguration().getLogger().info("LIB:: " + upstreamCause.getUpstreamRun().getEnvironment(buildListener)[COMMIT_HASH].getClass().name) //org.codehaus.groovy.runtime.NullObject

            getPipelineConfiguration().getLogger().info("LIB:: " + workflowJob.getBuildByNumber(upstreamBuildNumber).getClass().name) //org.jenkinsci.plugins.workflow.job.WorkflowRun
            getPipelineConfiguration().getLogger().info("LIB:: " + workflowJob.builds.getClass().name) //hudson.util.RunList
            getPipelineConfiguration().getLogger().info("LIB:: " + workflowJob.builds.getLastBuild().getClass().name) //org.jenkinsci.plugins.workflow.job.WorkflowRun
            getPipelineConfiguration().getLogger().info("LIB:: " + workflowJob.getBuildByNumber(upstreamBuildNumber).getEnvironment(buildListener).getClass().name) //hudson.EnvVars
            getPipelineConfiguration().getLogger().info("LIB:: " + workflowJob.getBuildByNumber(upstreamBuildNumber).getEnvironment(buildListener)[COMMIT_HASH]) //null
            getPipelineConfiguration().getLogger().info("LIB:: " + workflowJob.getBuildByNumber(upstreamBuildNumber).getEnvironment(buildListener).collect().getClass().name) //java.util.ArrayList

            workflowJob.getBuildByNumber(upstreamBuildNumber).getEnvironment(buildListener).collect().each { it -> getPipelineConfiguration().getLogger().info("LIB:: " + it.toString())
            }

        }
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
