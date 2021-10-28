package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.utilities.ProjectUtils

class NextSnapshotStage extends SnapshotStage {
    NextSnapshotStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, boolean runQARelease, String buildTool, String exe, String branch, String url, String githubCredentialsId) {
        super(pipelineConfiguration, stageName, runRelease, runQARelease, buildTool, exe, branch, url, githubCredentialsId, "POST Release", false)
    }

    void generateAndSetNewVersion(ProjectUtils projectUtils) {
        getPipelineConfiguration().getLogger().info("Incrementing to next snapshot version.")
        setNewVersion(projectUtils.increaseSemver(runRelease, runQARelease))
    }

    String getCommitMessage() {
        return "Using the next snapshot post release ${newVersion}"
    }

}
