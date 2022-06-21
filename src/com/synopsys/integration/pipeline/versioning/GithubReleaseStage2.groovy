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
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
            setReleaseTagName(timeStamp)
            setReleaseTargetCommitish("main")
            setReleaseName("Bob")
            setReleaseBody("Testing from pipeline")
            //println("Hello")

            getPipelineConfiguration().getLogger().info("anything")
            String stringCommandLines = "curl -X POST -H \"Accept: application/vnd.github.v3+json\" -H \"Authorization: token ${getGithubToken()}\" https://api.github.com/repos/github848/REPO/releases -d '{\"tag_name\":${getReleaseTagName()}, \"target_commitish\":\"main\", \"name\":${getReleaseTagName()}, \"body\":\"from the pipeline\", \"draft\":false, \"prerelease\":false, \"generate_release_notes\":false}'"

            def commandLines = []
            commandLines.add("#!/bin/bash")
            commandLines.add("bash <(${stringCommandLines})")

            def output = getPipelineConfiguration().getScriptWrapper().executeCommand(commandLines.join(" \n"), true)
            getPipelineConfiguration().getLogger().info(output)

            //def commandLines = ['curl', '-X', 'POST', '-H', 'Accept: application/vnd.github.v3+json', '-H', 'Authorization: token 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', 'https://api.github.com/repos/github848/REPO/releases', '-d', '{\"tag_name\":\"v1.0.8\", \"target_commitish\":\"main\", \"name\":\"v1.0.8\", \"body\":\"from groovy, joining as string\", \"draft\":false, \"prerelease\":false, \"generate_release_notes\":false}']
            //proc.join(" \n")
            //proc.execute()
            //getPipelineConfiguration().getScriptWrapper().executeCommandWithException(commandLines.join(" \n"))
            //def commandLines = []
            //commandLines.add("#!/bin/bash")
            //commandLines.add("bash <(curl -s https://github.com/blackducksoftware/integration-pipeline-library/tree/INTRELENG-117)")
            //getPipelineConfiguration().getScriptWrapper().executeCommandWithException(commandLines.join(" \n"))
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

    String getGithubToken() {
        return githubToken
    }

    void setGithubToken(String githubToken) {
        this.githubToken = githubToken
    }
}

