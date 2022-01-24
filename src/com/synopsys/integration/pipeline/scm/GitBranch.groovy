package com.synopsys.integration.pipeline.scm

import com.synopsys.integration.pipeline.jenkins.JenkinsUpstreamBuild
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

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
        JenkinsUpstreamBuild jenkinsUpstreamBuild = new JenkinsUpstreamBuild(pipelineConfiguration)
        String branchFromCause = jenkinsUpstreamBuild.getUpstreamEnvironmentVariable('BRANCH')

        if (branchFromCause?.trim()) {
            setBranch(branchFromCause)
            setBranchSource('upstream build')
        }

        pipelineConfiguration.getLogger().info("branch is set from ${branchSource} as ${branch}")

        return branch
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
