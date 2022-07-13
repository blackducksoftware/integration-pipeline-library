package com.synopsys.integration.pipeline.versioning


import com.synopsys.integration.pipeline.exception.GitHubReleaseException
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

import java.text.SimpleDateFormat
import static groovy.io.FileType.FILES

class GithubReleaseStage extends Stage{
    public static String RELEASE_FILE = 'release.json'
    public static String BUILD_FILE = 'build_data.json'
    //public static String ASSET_FILE = 'assets.json'
    private String releaseOwner
    private String releaseRepo
    private String releaseTagName
    private String releaseTargetCommitish
    private String releaseName
    private String releaseBody
    private String githubToken
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
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date())
            setReleaseTagName(getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(GithubReleaseStageLegacy.GITHUB_RELEASE_VERSION))
            setReleaseBody("Released from Jenkins " + timeStamp)

            getPipelineConfiguration().getLogger().info("anything")
            getPipelineConfiguration().getLogger().info("hello1" + getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RemoveSnapshotStage.RELEASE_COMMIT_HASH))

            targetCommitish = getPipelineConfiguration().getScriptWrapper().getJenkinsProperty(RemoveSnapshotStage.RELEASE_COMMIT_HASH)
            String stringCommandLines = "curl -s -X POST -H \"Accept: application/vnd.github.v3+json\" https://api.github.com/repos/${getReleaseOwner()}/${getReleaseRepo()}/releases -d '{\"tag_name\":\"${getReleaseTagName()}\", \"target_commitish\":\"${getTargetCommitish()}\", \"name\":\"${getReleaseTagName()}\", \"body\":\"${getReleaseBody()}\", \"draft\":false, \"prerelease\":false, \"generate_release_notes\":false}'" //-o release.json"

            getPipelineConfiguration().getScriptWrapper().executeCommandWithHttpStatusCheck(stringCommandLines, "201", RELEASE_FILE, githubCredentialsId, pipelineConfiguration)
            getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().readJsonFile(RELEASE_FILE) as String)

            //testing
            new File("/build/libs/").eachFileRecurse(FILES) {
                getPipelineConfiguration().getLogger().info("file ")
                getPipelineConfiguration().getLogger().info("file " + it)
                if(it.name.endsWith('.jar') && it.name.startsWith('release-test-')) {
                    getPipelineConfiguration().getLogger().info(it.name)
                }
            }

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

    String getGithubToken() {
        return githubToken
    }

    String getTargetCommitish() {
        return targetCommitish
    }

}

