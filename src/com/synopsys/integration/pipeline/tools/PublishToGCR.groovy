package com.synopsys.integration.pipeline.tools

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class PublishToGCR extends Stage {
    private String fileGlob = 'build/**/docker-compose.yml'
    private String delimiter = ':::'
    private String gcrRepo

    PublishToGCR(PipelineConfiguration pipelineConfiguration, String stageName, String gcrRepo) {
        super(pipelineConfiguration, stageName)
        this.gcrRepo = gcrRepo
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        String imageAndShaForBuildTrigger = ''
        Set<String> dockerImageNames = []
        JenkinsScriptWrapper jenkinsScriptWrapper = getPipelineConfiguration().getScriptWrapper()
        PipelineLogger logger = getPipelineConfiguration().getLogger()

        // Identify all the orchestration files
        def files = jenkinsScriptWrapper.findFileGlob(fileGlob)
        if (files.length == 0) {
            throw new Exception("No files found matching input " + fileGlob)
        }
        logger.info("Found ${files.length} orchestration files matching ${fileGlob}")

        // Parse orchestration files to get the image name
        for (File composeFile : files) {
            def fileAsYaml = jenkinsScriptWrapper.readYamlFile(composeFile.path)
            for (def service : fileAsYaml.services) {
                dockerImageNames << service.getValue().get('image')
            }
        }
        logger.info("Found ${dockerImageNames.size()} unique docker image names within orchestration")

        // Loop all image names to get the sha
        for (imageName in dockerImageNames) {
            jenkinsScriptWrapper.executeCommandWithException("docker pull ${imageName}")

            // Get and check the sha
            String shaFromImage = jenkinsScriptWrapper.executeCommand("docker inspect --format='{{index .RepoDigests 0}}' ${imageName}", true).trim()
            if (!shaFromImage?.trim()) {
                throw new Exception("Could not get sha for image " + imageName)
            }

            // Put together the string used to trigger the downstream job
            if (imageAndShaForBuildTrigger?.trim()) {
                imageAndShaForBuildTrigger += ','
            }
            imageAndShaForBuildTrigger += imageName
            imageAndShaForBuildTrigger += delimiter
            imageAndShaForBuildTrigger += shaFromImage
        }

        // Trigger build
        logger.alwaysLog("Pushing to repo ${gcrRepo}")
        logger.info("Pushing data: ${imageAndShaForBuildTrigger}")
        jenkinsScriptWrapper.triggerPushToGCR(imageAndShaForBuildTrigger, gcrRepo)
    }

    void setFileGlob(String fileGlob) {
        this.fileGlob = fileGlob
    }

    void setDelimiter(String delimiter) {
        this.delimiter = delimiter
    }

    void setGcrRepo(String gcrRepo) {
        this.gcrRepo = gcrRepo
    }
}
