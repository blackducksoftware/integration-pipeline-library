package com.synopsys.integration.pipeline.tools

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class DetectStage extends Stage {
    public static final String DEFAULT_DETECT_PROPERTIES = "--detect.gradle.excluded.configurations=test* --detect.blackduck.signature.scanner.arguments=\\\"--exclude /gradle/ --exclude /src/test/resources/\\\""

    private String detectCommand
    private String blackduckConnection
    private DockerImage dockerImage
    private final String detectURL
    private boolean includeDefaultParameters = true

    DetectStage(PipelineConfiguration pipelineConfiguration, String stageName, String detectURL, String detectCommand) {
        super(pipelineConfiguration, stageName)
        this.detectURL = detectURL
        this.detectCommand = detectCommand
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        addDockerImageOptions()

        if (includeDefaultParameters) {
            addDetectParameters(DEFAULT_DETECT_PROPERTIES)
        }

        def commandLines = []
        commandLines.add("#!/bin/bash")
        commandLines.add("bash <(curl -s ${detectURL}) ${blackduckConnection} ${detectCommand}")
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

    String getDockerImage() {
        return dockerImage
    }

    void setDockerImage(DockerImage dockerImage) {
        this.dockerImage = dockerImage
    }

    boolean getIncludeDefaultParameters() {
        return includeDefaultParameters
    }

    void setIncludeDefaultParameters(boolean includeDefaultParameters) {
        this.includeDefaultParameters = includeDefaultParameters
    }

    private void addDockerImageOptions() {
        if (dockerImage) {
            String dockerImageOptions = dockerImage.getDockerDetectParams()
            addDetectParameters(dockerImageOptions)
        }
    }
}
