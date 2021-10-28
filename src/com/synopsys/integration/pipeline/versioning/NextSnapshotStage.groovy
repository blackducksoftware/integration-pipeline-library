package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

class NextSnapshotStage extends SnapshotStage {
    NextSnapshotStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, boolean runQARelease, String buildTool, String exe, String branch, String url, String githubCredentialsId) {
        super(pipelineConfiguration, stageName, runRelease, runQARelease, buildTool, exe, branch, url, githubCredentialsId)
        setLoggingFlag("POST Release")
    }

    void generateAndSetNewVersion() {
        getPipelineConfiguration().getLogger().info("Incrementing to next snapshot version.")
        setNewVersion(projectUtils.increaseSemver(runRelease, runQARelease))
    }

    String getCommitMessage() {
        return "Using the next snapshot post release ${newVersion}"
    }

}
