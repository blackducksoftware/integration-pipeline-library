package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

class RemoveSnapshotStage extends SnapshotStage {
    RemoveSnapshotStage(PipelineConfiguration pipelineConfiguration, String stageName, boolean runRelease, boolean runQARelease, String buildTool, String exe, String branch, String url, String githubCredentialsId) {
        super(pipelineConfiguration, stageName, runRelease, runQARelease, buildTool, exe, branch, url, githubCredentialsId, "PRE Release", true)
    }

    void generateAndSetNewVersion() {
        getPipelineConfiguration().getLogger().info("Removing SNAPSHOT from the Project Version.")
        setNewVersion(projectUtils.updateVersionForRelease(runRelease, runQARelease))
        getPipelineConfiguration().getScriptWrapper().setJenkinsProperty('GITHUB_RELEASE_VERSION', newVersion)
    }

    String getCommitMessage() {
        return "Release ${newVersion}"
    }

}
