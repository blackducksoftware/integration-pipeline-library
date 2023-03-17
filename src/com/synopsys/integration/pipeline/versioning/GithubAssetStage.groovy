package com.synopsys.integration.pipeline.versioning


import com.synopsys.integration.pipeline.exception.GitHubReleaseException
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import org.apache.commons.lang3.StringUtils

class GithubAssetStage extends Stage {
    private String githubCredentialsId
    private String glob

    GithubAssetStage(PipelineConfiguration pipelineConfiguration, String stageName, String glob, String githubCredentialsId) {
        super(pipelineConfiguration, stageName)
        this.glob = glob
        this.githubCredentialsId = githubCredentialsId
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        try {
            //taking the upload URL out of the json file from creating the release, and deleting the part at the end we don't want
            String uploadUrl = (getPipelineConfiguration().getScriptWrapper().readJsonFile(GithubReleaseStage.RELEASE_FILE)["upload_url"] as String)
            uploadUrl = StringUtils.substringBeforeLast(uploadUrl, '{')

            def files = getPipelineConfiguration().getScriptWrapper().findFileGlob(glob)
            //throwing if no files matching glob pattern are found
            if (files.length == 0) {
                throw new Exception("no files found matching input " + glob)
            }
            //taking the path of each file and uploading to the release
            for (File file : files) {
                String assetName = file.path
                //TODO phase out curl command for direct Github API calls
                String assetCommandLines = "curl -s -X POST -H \"Accept: application/vnd.github.v3+json\" -H \"Content-Type: \$(file -b --mime-type \"${assetName}\")\" -H \"Content-Length: \$(wc -c <\"${assetName}\" | xargs)\" -T \"${assetName}\" \"${uploadUrl}?name=\$(basename ${assetName})\""
                assetName = "asset-" + StringUtils.substringAfterLast(assetName, '/') + ".json"
                getPipelineConfiguration().getScriptWrapper().executeCommandWithHttpStatusCheck(assetCommandLines, 201, assetName, githubCredentialsId, pipelineConfiguration)
            }

        } catch (Exception e) {
            throw new GitHubReleaseException("Failed to run the GitHub auto release ${e.getMessage()}")
        }
    }

}

