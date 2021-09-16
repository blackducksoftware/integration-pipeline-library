package com.synopsys.integration.pipeline.scm

import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import hudson.model.BuildListener
import hudson.model.Cause
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun

class GitBranch {
    private PipelineConfiguration pipelineConfiguration
    private String branch
    private String branchSource

    GitBranch(PipelineConfiguration pipelineConfiguration, String branch) {
        this.pipelineConfiguration = pipelineConfiguration
        this.branch = (branch?.trim()) ? branch : GitStage.DEFAULT_BRANCH_NAME
        this.branchSource = (branch?.trim()) ? 'constructor' : 'default setting'
    }

    String determineAndGetBranch() {
        WorkflowRun currentBuild = pipelineConfiguration.getScriptWrapper().currentBuild().getRunWrapper().getRawBuild() as WorkflowRun

        if (hasUpstreamCause(currentBuild)) {
            String branchFromCause = determineBranchFromUpstreamCause(currentBuild)

            if (branchFromCause?.trim()) {
                setBranch(branchFromCause)
                setBranchSource('upstream build')
            }
        }

        pipelineConfiguration.getLogger().info("branch is set from ${branchSource} as ${branch}")

        return branch
    }

    private static boolean hasUpstreamCause(WorkflowRun workflowRun) {
        return null != workflowRun.getCause(Cause.UpstreamCause)
    }

    private static String determineBranchFromUpstreamCause(WorkflowRun workflowRun) {
        Cause.UpstreamCause upstreamCause = workflowRun.getCause(Cause.UpstreamCause)

        Cause.UpstreamCause possiblyNull = getParentCause(upstreamCause)
        while (possiblyNull != null) {
            upstreamCause = possiblyNull
            possiblyNull = getParentCause(possiblyNull)
        }

        WorkflowRun highestUpstreamBuild = getWorkflowRun(upstreamCause)
        BuildListener buildListener = highestUpstreamBuild.getListener()

        return upstreamCause.getUpstreamRun().getEnvironment(buildListener)['BRANCH']
    }

    private static Cause.UpstreamCause getParentCause(Cause.UpstreamCause cause) {
        return getWorkflowRun(cause).getCause(Cause.UpstreamCause)
    }

    private static WorkflowRun getWorkflowRun(Cause.UpstreamCause cause) {
        String jobName = cause.getUpstreamProject()
        int buildNumber = cause.getUpstreamBuild()
        return Jenkins.get().getItemByFullName(jobName, WorkflowJob).getBuildByNumber(buildNumber)
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
}
