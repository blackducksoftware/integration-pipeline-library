package com.synopsys.integration.pipeline.tools

import com.synopsys.integration.pipeline.SimplePipeline
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class DetectStage extends Stage {
    public static final String DEFAULT_DETECT_PROPERTIES = "--detect.gradle.excluded.configurations=test* --detect.gradle.configuration.types.excluded=UNRESOLVED --detect.blackduck.signature.scanner.arguments=\\\"--exclude /gradle/ --exclude /src/test/resources/\\\""
    public static final String DETECT_PROJECT_VERSION_NAME = '--detect.project.version.name'

    private String detectCommand
    private String blackduckConnection
    private DockerImage dockerImage
    private final String detectURL
    private String defaultParameters = DEFAULT_DETECT_PROPERTIES
    private String bdUploadVersionVariableName = SimplePipeline.BD_UPLOAD_VERSION

    DetectStage(PipelineConfiguration pipelineConfiguration, String stageName, String detectURL, String detectCommand) {
        super(pipelineConfiguration, stageName)
        this.detectURL = detectURL
        this.detectCommand = detectCommand
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        // This is done because setting of the version is done during the pipeline execution.
        // But, because of the duplicate param bug, the default parameters is set prior to execution
        if (!Objects.isNull(dockerImage)) {
            updateDetectCommand(DockerImage.DEFAULT_IMAGE_VERSION, dockerImage.getDockerVersionFromEnvironment())
        }

        String combinedDetectParameters = "${blackduckConnection} ${getDetectCommand()} ${getDefaultParameters()}"
        String detectProjectVersionName = pipelineConfiguration.scriptWrapper.getJenkinsProperty(bdUploadVersionVariableName)

        if (null != detectProjectVersionName) {
            combinedDetectParameters = removeDetectProjectVersionName(combinedDetectParameters)
            combinedDetectParameters += " ${DETECT_PROJECT_VERSION_NAME}=${detectProjectVersionName}"
        } else {
            pipelineConfiguration.getLogger().info("Did not find environment variable set for ${bdUploadVersionVariableName}. Not setting or overriding --detect.project.version.name for detect execution")
        }

        def commandLines = []
        commandLines.add("#!/bin/bash")
        commandLines.add("bash <(curl -s ${detectURL}) ${combinedDetectParameters}")
        getPipelineConfiguration().getScriptWrapper().executeCommandWithCatchError(commandLines.join(" \n"))
    }

    String getDetectCommand() {
        return detectCommand
    }

    String getDetectURL() {
        return detectURL
    }

    void setBlackduckConnection(String bdUrl, String bdApiToken) {
        blackduckConnection = "--blackduck.url=" + bdUrl.trim() + " --blackduck.api.token=" + bdApiToken.trim()
    }

    String getBlackduckConnection() {
        return blackduckConnection
    }

    void addDetectParameters(String detectArgs) {
        detectCommand = detectCommand.trim() + " " + detectArgs.trim()
    }

    private void updateDetectCommand(String searchToken, String replaceToken) {
        detectCommand = detectCommand.replaceAll(searchToken, replaceToken)
    }

    String getDockerImage() {
        return dockerImage
    }

    void setDockerImage(DockerImage dockerImage) {
        this.dockerImage = dockerImage
        addDockerImageOptions()
    }

    String getDefaultParameters() {
        return defaultParameters
    }

    void setDefaultParameters(boolean includeDefaultParameters) {
        if (includeDefaultParameters) {
            this.defaultParameters = DEFAULT_DETECT_PROPERTIES
        } else {
            this.defaultParameters = ""
        }
    }

    private void addDockerImageOptions() {
        if (dockerImage) {
            String dockerImageOptions = dockerImage.getDockerDetectParams()
            addDetectParameters(dockerImageOptions)
        }
    }

    String getBdUploadVersionVariableName() {
        return bdUploadVersionVariableName
    }

    void setBdUploadVersionVariableName(String bdUploadVersionVariableName) {
        this.bdUploadVersionVariableName = bdUploadVersionVariableName
    }

    private String removeDetectProjectVersionName(String inputDetectCommand) {
        if (!inputDetectCommand.contains(DETECT_PROJECT_VERSION_NAME)) {
            return inputDetectCommand
        }

        String detectProjectVersionNameSetting = inputDetectCommand.substring(inputDetectCommand.indexOf(DETECT_PROJECT_VERSION_NAME))
        int nextConfigurationSetting = detectProjectVersionNameSetting.indexOf(' --')
        if (nextConfigurationSetting != -1) {
            detectProjectVersionNameSetting = detectProjectVersionNameSetting.substring(0, nextConfigurationSetting)
        }

        String newDetectCommand = inputDetectCommand.replace(detectProjectVersionNameSetting, '')

        if (newDetectCommand.indexOf(DETECT_PROJECT_VERSION_NAME) != -1) {
            throw new IllegalArgumentException("Detect command had multiple occurrences of " + DETECT_PROJECT_VERSION_NAME)
        } else {
            pipelineConfiguration.getLogger().info("Successfully removed ${DETECT_PROJECT_VERSION_NAME} from the Detect command.")
        }

        return newDetectCommand
    }
}
