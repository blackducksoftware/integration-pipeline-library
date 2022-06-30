package com.synopsys.integration.pipeline.versioning


import com.synopsys.integration.pipeline.exception.GitHubReleaseException
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

import java.text.SimpleDateFormat

class GithubAssetStage extends Stage{
    public static String ASSET_FILE = 'assets.json'
    private String githubToken

    GithubAssetStage(PipelineConfiguration pipelineConfiguration, String stageName) {
        super(pipelineConfiguration, stageName)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        try {
            getPipelineConfiguration().getLogger().info("hello")
            getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().readJsonFile(GithubReleaseStage2.RELEASE_FILE)["upload_url"] as String)

            //taking the upload URL out of the json file from creating the release, and deleting the part at the end we don't want
            String uploadUrl = (getPipelineConfiguration().getScriptWrapper().readJsonFile(GithubReleaseStage2.RELEASE_FILE)["upload_url"] as String)
            uploadUrl = uploadUrl.substring(0, uploadUrl.length() - 13)

            String assetCommandLines = "curl -X POST -H \"Authorization: token ${getGithubToken()}\" -H \"Accept: application/vnd.github.v3+json\" -H \"Content-Type: \$(file -b --mime-type \"build/libs/release-test-0.1.134-SNAPSHOT.jar\")\" -H \"Content-Length: \$(wc -c <\"build/libs/release-test-0.1.134-SNAPSHOT.jar\" | xargs)\" -T \"build/libs/release-test-0.1.134-SNAPSHOT.jar\" \"${uploadUrl}?name=\$(basename build/libs/release-test-0.1.134-SNAPSHOT.jar)\""
            //getPipelineConfiguration().getLogger().info(assetCommandLines)
            getPipelineConfiguration().getScriptWrapper().executeCommandWithHttpStatusCheck(assetCommandLines, "201", ASSET_FILE)
            getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().readJsonFile(ASSET_FILE) as String)

        } catch (Exception e) {
            throw new GitHubReleaseException("Failed to run the GitHub auto release ${e.getMessage()}")
        }
    }

    String getGithubToken() {
        return githubToken
    }

    void setGithubToken(String githubToken) {
        this.githubToken = githubToken
    }
}

