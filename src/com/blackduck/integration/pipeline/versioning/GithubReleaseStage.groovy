package com.blackduck.integration.pipeline.versioning


import com.blackduck.integration.pipeline.exception.GitHubReleaseException
import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Stage

import java.text.SimpleDateFormat

class GithubReleaseStage extends Stage {
    public static String RELEASE_FILE = 'release.json'
    private String releaseOwner
    private String releaseRepo
    private String releaseTagName
    private String releaseTargetCommitish
    private String releaseName
    private String releaseBody
    private String githubCredentialsId
    private String targetCommitish

    GithubReleaseStage(PipelineConfiguration pipelineConfiguration, String stageName, String releaseOwner, String releaseRepo, String githubCredentialsId) {
        super(pipelineConfiguration, stageName)
        this.releaseOwner = releaseOwner
        this.releaseRepo = releaseRepo
        this.githubCredentialsId = githubCredentialsId
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        try {
            String timeStamp = new SimpleDateFormat("MMM d, yyyy HH:mm:ss").format(new Date())
            setReleaseTagName(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(GithubReleaseStageLegacy.GITHUB_RELEASE_VERSION))
            setReleaseBody("Released from Jenkins on " + timeStamp)

            targetCommitish = getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RemoveSnapshotStage.RELEASE_COMMIT_HASH)
            //TODO phase out curl command for direct Github API calls
            String stringCommandLines = "curl -s -X POST -H \"Accept: application/vnd.github.v3+json\" https://api.github.com/repos/${getReleaseOwner()}/${getReleaseRepo()}/releases -d '{\"tag_name\":\"${getReleaseTagName()}\", \"target_commitish\":\"${getTargetCommitish()}\", \"name\":\"${getReleaseTagName()}\", \"body\":\"${getReleaseBody()}\"'"

            getPipelineConfiguration().getScriptWrapper().executeCommandWithHttpStatusCheck(stringCommandLines, 201, RELEASE_FILE, githubCredentialsId, pipelineConfiguration)
            getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().readJsonFile(RELEASE_FILE) as String)

        } catch (Exception e) {
            throw new GitHubReleaseException("Failed to run the GitHub auto release ${e.getMessage()}")
        }
    }

    String getReleaseOwner() {
        return releaseOwner
    }

    String getReleaseRepo() {
        return releaseRepo
    }

    String getReleaseTagName() {
        return releaseTagName
    }

    void setReleaseTagName(String releaseTagName) {
        this.releaseTagName = releaseTagName
    }

    String getReleaseTargetCommitish() {
        return releaseTargetCommitish
    }

    void setReleaseTargetCommitish(String releaseTargetCommitish) {
        this.releaseTargetCommitish = releaseTargetCommitish
    }

    String getReleaseName() {
        return releaseName
    }

    void setReleaseName(String releaseName) {
        this.releaseName = releaseName
    }

    String getReleaseBody() {
        return releaseBody
    }

    void setReleaseBody(String releaseBody) {
        this.releaseBody = releaseBody
    }

    String getTargetCommitish() {
        return targetCommitish
    }

}

