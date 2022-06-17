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

class GithubReleaseStage2 extends Stage{
    private String releaseOwner
    private String releaseRepo
    private String releaseTagName
    private String releaseTargetCommitish
    private String releaseName
    private String releaseBody

    GithubReleaseStage2 (PipelineConfiguration pipelineConfiguration, String stageName) {
        super(pipelineConfiguration, stageName)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        try {
            setReleaseOwner("github848")
            setReleaseRepo("REPO")
            setReleaseTagName("v1.0.5")
            setReleaseTargetCommitish("main")
            setReleaseName("Bob")
            setReleaseBody("Testing from pipeline")
            println("Hello")

            def commandLines = ['curl', '-X', 'POST', '-H', 'Accept: application/vnd.github.v3+json', '-H', 'Authorization: token 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', 'https://api.github.com/repos/github848/REPO/releases', '-d', '{\"tag_name\":\"v1.0.2\", \"target_commitish\":\"main\", \"name\":\"v1.0.1\", \"body\":\"from groovy\", \"draft\":false, \"prerelease\":false, \"generate_release_notes\":false}']

            getPipelineConfiguration().getScriptWrapper().executeCommandWithException(commandLines)
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
        this.releaseName = releaseBody
    }
}

