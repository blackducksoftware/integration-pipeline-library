package com.synopsys.integration.pipeline.tools

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class DetectStage extends Stage {
    public static final String DEFAULT_DETECT_PROPERTIES = "--detect.gradle.excluded.configurations=test* --detect.gradle.configuration.types.excluded=UNRESOLVED --detect.blackduck.signature.scanner.arguments=\\\"--exclude /gradle/ --exclude /src/test/resources/\\\""

    private String detectCommand
    private String blackduckConnection
    private DockerImage dockerImage
    private final String detectURL
    private String defaultParameters = DEFAULT_DETECT_PROPERTIES

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

        def commandLines = []
        commandLines.add("#!/bin/bash")
        commandLines.add("bash <(curl -s ${detectURL}) ${blackduckConnection} ${getDetectCommand()} ${getDefaultParameters()}")
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
}
