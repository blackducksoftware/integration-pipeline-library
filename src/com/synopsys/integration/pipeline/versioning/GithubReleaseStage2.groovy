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
    public static String ASSET_FILE = 'assets.json'
    private String releaseOwner
    private String releaseRepo
    private String releaseTagName
    private String releaseTargetCommitish
    private String releaseName
    private String releaseBody
    private String githubToken

    GithubReleaseStage2 (PipelineConfiguration pipelineConfiguration, String stageName) {
        super(pipelineConfiguration, stageName)
    }




    @Override
    void stageExecution() throws PipelineException, Exception {
        try {
            setReleaseOwner("github848")
            setReleaseRepo("REPO")
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date())
            setReleaseTagName(timeStamp)
            //setting branch
            setReleaseTargetCommitish("main")
            setReleaseName("Bob")
            setReleaseBody("Testing from pipeline -- with HTTP check 2")

            getPipelineConfiguration().getLogger().info("anything")
            String stringCommandLines = "curl -s -X POST -H \"Accept: application/vnd.github.v3+json\" -H \"Authorization: token ${getGithubToken()}\" https://api.github.com/repos/${getReleaseOwner()}/${getReleaseRepo()}/releases -d '{\"tag_name\":\"${getReleaseTagName()}\", \"target_commitish\":\"${getReleaseTargetCommitish()}\", \"name\":\"${getReleaseTagName()}\", \"body\":\"${getReleaseBody()}\", \"draft\":false, \"prerelease\":false, \"generate_release_notes\":false}'" //-o release.json"

            getPipelineConfiguration().getScriptWrapper().executeCommandWithHttpStatusCheck(stringCommandLines, "201", RELEASE_FILE)
            getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().readJsonFile(RELEASE_FILE) as String)

            String assetCommandLines = "curl -X POST -H \"Authorization: token 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\" -H \"Accept: application/vnd.github.v3+json\" -H \"Content-Type: \$(file -b --mime-type \"build/lib/release-test-0.1.134-SNAPSHOT.jar\")\" -H \"Content-Length: \$(wc -c <\"build/lib/release-test-0.1.134-SNAPSHOT.jar\" | xargs)\" -T \"build/lib/release-test-0.1.134-SNAPSHOT.jar\" \"https://uploads.github.com/repos/github848/REPO/releases/70680724/assets?name=testgroovy.txt\" | cat"
            //getPipelineConfiguration().getScriptWrapper().executeCommandWithHttpStatusCheck(assetCommandLines, "201", ASSET_FILE)
            //getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().readJsonFile(ASSET_FILE) as String)

        } catch (Exception e) {
            throw new GitHubReleaseException("Failed to run the GitHub auto release ${e.getMessage()}")
        }
    }

    String getReleaseOwner() {
        return releaseOwner
    }

    void setReleaseOwner(String releaseOwner) {
        this.releaseOwner = releaseOwner
    }

    String getReleaseRepo() {
        return releaseRepo
    }

    void setReleaseRepo(String releaseRepo) {
        this.releaseRepo = releaseRepo
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

