package com.blackduck.integration.pipeline.jenkins

import hudson.model.BuildListener
import hudson.model.Cause
import jenkins.model.Jenkins
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.job.WorkflowRun

class JenkinsUpstreamBuild {
    private PipelineConfiguration pipelineConfiguration

    JenkinsUpstreamBuild(PipelineConfiguration pipelineConfiguration) {
        this.pipelineConfiguration = pipelineConfiguration
    }

    public String getUpstreamEnvironmentVariable(String variableName) {
        WorkflowRun workFlowRun = pipelineConfiguration.getScriptWrapper().currentBuild().getRunWrapper().getRawBuild() as WorkflowRun

        Cause.UpstreamCause upstreamCause = workFlowRun.getCause(Cause.UpstreamCause)

        if (upstreamCause != null) {
            Cause.UpstreamCause possiblyNull = getParentCause(upstreamCause)
            while (possiblyNull != null) {
                upstreamCause = possiblyNull
                possiblyNull = getParentCause(possiblyNull)
            }

            WorkflowRun highestUpstreamBuild = getWorkflowRun(upstreamCause)
            BuildListener buildListener = highestUpstreamBuild.getListener()

            return upstreamCause.getUpstreamRun().getEnvironment(buildListener)[variableName]
        } else {
            return null
        }
    }

    private static Cause.UpstreamCause getParentCause(Cause.UpstreamCause cause) {
        return getWorkflowRun(cause).getCause(Cause.UpstreamCause)
    }

    private static WorkflowRun getWorkflowRun(Cause.UpstreamCause cause) {
        String jobName = cause.getUpstreamProject()
        int buildNumber = cause.getUpstreamBuild()
        return Jenkins.get().getItemByFullName(jobName, WorkflowJob).getBuildByNumber(buildNumber)
    }
}
