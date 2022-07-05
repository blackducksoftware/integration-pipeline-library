package com.synopsys.integration.pipeline.versioning

import com.synopsys.integration.model.GithubBranchModel
import com.synopsys.integration.pipeline.exception.GitHubReleaseException
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.exception.PrepareForReleaseException
import com.synopsys.integration.pipeline.scm.GitStage
import com.synopsys.integration.utilities.GithubBranchParser
import org.apache.commons.lang3.StringUtils
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

import java.text.SimpleDateFormat

class GithubReleaseStage2 extends Stage{
    public static String RELEASE_FILE = 'release.json'
    //public static String ASSET_FILE = 'assets.json'
    private String releaseOwner
    private String releaseRepo
    private String releaseTagName
    private String releaseTargetCommitish
    private String releaseName
    private String releaseBody
    private String githubToken

    GithubReleaseStage2 (PipelineConfiguration pipelineConfiguration, String stageName, String releaseOwner, String releaseRepo) {
        super(pipelineConfiguration, stageName)
        this.releaseOwner = releaseOwner
        this.releaseRepo = releaseRepo

    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        try {
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date())
            setReleaseTagName(timeStamp)
            //setting branch
            setReleaseTargetCommitish("main")
            setReleaseBody("Auto Release")

            getPipelineConfiguration().getLogger().info("anything")
            String stringCommandLines = "curl -s -X POST -H \"Accept: application/vnd.github.v3+json\" -H \"Authorization: token ${getGithubToken()}\" https://api.github.com/repos/${getReleaseOwner()}/${getReleaseRepo()}/releases -d '{\"tag_name\":\"${getReleaseTagName()}\", \"target_commitish\":\"${getReleaseTargetCommitish()}\", \"name\":\"${getReleaseTagName()}\", \"body\":\"${getReleaseBody()}\", \"draft\":false, \"prerelease\":false, \"generate_release_notes\":false}'" //-o release.json"

            getPipelineConfiguration().getScriptWrapper().executeCommandWithHttpStatusCheck(stringCommandLines, "201", RELEASE_FILE)
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

    String getGithubToken() {
        return githubToken
    }

    void setGithubToken(String githubToken) {
        this.githubToken = githubToken
    }
}

