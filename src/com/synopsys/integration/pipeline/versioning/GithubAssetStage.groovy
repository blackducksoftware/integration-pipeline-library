package com.synopsys.integration.pipeline.versioning


import com.synopsys.integration.pipeline.exception.GitHubReleaseException
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import org.jenkinsci.plugins.workflow.cps.CpsScript
import org.apache.commons.lang3.StringUtils

class GithubAssetStage extends Stage{
    public static String ASSET_FILE = 'assets.json'
    private String githubCredentialsId
    private String glob
    private String[] assetNames
    final CpsScript script

    GithubAssetStage(PipelineConfiguration pipelineConfiguration, String stageName, String glob, String githubCredentialsId, final CpsScript script) {
        super(pipelineConfiguration, stageName)
        this.glob = glob
        this.githubCredentialsId = githubCredentialsId
        this.script = script
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        try {
            getPipelineConfiguration().getLogger().info("hello")
            getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().readJsonFile(GithubReleaseStage.RELEASE_FILE)["upload_url"] as String)

            //taking the upload URL out of the json file from creating the release, and deleting the part at the end we don't want
            String uploadUrl = (getPipelineConfiguration().getScriptWrapper().readJsonFile(GithubReleaseStage.RELEASE_FILE)["upload_url"] as String)
            uploadUrl = StringUtils.substringBeforeLast(uploadUrl, '{')

            //finding files which match glob pattern
            def files = script.findFiles(glob: glob)
            if (files.length == 0)
                throw new Exception("no files found matching input glob")
            assetNames = new String[files.length]
            for (int i = 0; i < files.length; i++) {
                //assetNames[i] = StringUtils.substringAfter(files[i].path, '/')
                assetNames = files[i].path
            }

            for (int i = 0; i < assetNames.length; i++) {
                String assetCommandLines = "curl -X POST -H \"Accept: application/vnd.github.v3+json\" -H \"Content-Type: \$(file -b --mime-type \"${getAssetName(i)}\")\" -H \"Content-Length: \$(wc -c <\"${getAssetName(i)}\" | xargs)\" -T \"${getAssetName(i)}\" \"${uploadUrl}?name=\$(basename ${getAssetName(i)})\""
                //getPipelineConfiguration().getLogger().info(assetCommandLines)
                getPipelineConfiguration().getScriptWrapper().executeCommandWithHttpStatusCheck(assetCommandLines, "201", ASSET_FILE, githubCredentialsId, pipelineConfiguration, getAssetName(i))
            }

            getPipelineConfiguration().getLogger().info("hello")
            //getPipelineConfiguration().getLogger().info(getPipelineConfiguration().getScriptWrapper().readJsonFile(GithubReleaseStage.BUILD_FILE)["GIT_LOCAL_BRANCH"] as String)

        } catch (Exception e) {
            throw new GitHubReleaseException("Failed to run the GitHub auto release ${e.getMessage()}")
        }
    }

    String getGithubCredentialsId() {
        return githubCredentialsId
    }

    String getAssetName(int i) {
        return assetNames[i]
    }
}

