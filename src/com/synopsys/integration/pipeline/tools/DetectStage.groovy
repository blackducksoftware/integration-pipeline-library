package com.synopsys.integration.pipeline.tools


import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class DetectStage extends Stage {
    public static final String DEFAULT_DETECT_SETTINGS = '--blackduck.trust.cert=true --detect.docker.passthrough.service.timeout=960000 --blackduck.timeout=600'
    public static final String DEFAULT_DETECT_EXCLUSION_PROPERTIES = "--detect.gradle.excluded.configurations=test* --detect.gradle.configuration.types.excluded=UNRESOLVED --detect.blackduck.signature.scanner.arguments=\\\"--exclude /gradle/ --exclude /src/test/resources/\\\""
    public static final String DETECT_PROJECT_VERSION_NAME_PROPERTY = '--detect.project.version.name'
    public static final String DETECT_PROJECT_VERSION_NAME_OVERRIDE = 'DETECT_PROJECT_VERSION_NAME_OVERRIDE'
    public static final String DETECT_PROJECT_CODELOCATION_UNMAP_PROPERTY = '--detect.project.codelocation.unmap'
    public static final String DETECT_PROJECT_CODELOCATION_UNMAP_OVERRIDE = 'DETECT_PROJECT_CODELOCATION_UNMAP_OVERRIDE'

    private String detectCommand
    private String blackduckConnection
    private DockerImage dockerImage
    private final String detectURL
    private String defaultExclusionParameters = DEFAULT_DETECT_EXCLUSION_PROPERTIES

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

            getPipelineConfiguration().getScriptWrapper().executeCommandWithException("docker logout")

            String fullImageName = dockerImage.setFullDockerImageName()
            if (dockerImage.getDockerImageVersion().contains(DockerImage.DEFAULT_IMAGE_VERSION)) {
                throw new RuntimeException('Must either provide a version for the image, or include a stage which calls SimplePipeline.addSetGradleVersionStage')
            }

            getPipelineConfiguration().getScriptWrapper().executeCommandWithException("docker pull ${fullImageName}")
        }

        String combinedDetectParameters = "${blackduckConnection} ${getDetectCommand()} ${getDefaultExclusionParameters()}"

        // Override parameters already in Detect command if override variable set
        combinedDetectParameters = removeDetectPropertyFromCommand(combinedDetectParameters, DETECT_PROJECT_VERSION_NAME_PROPERTY, DETECT_PROJECT_VERSION_NAME_OVERRIDE, null)
        combinedDetectParameters = removeDetectPropertyFromCommand(combinedDetectParameters, DETECT_PROJECT_CODELOCATION_UNMAP_PROPERTY, DETECT_PROJECT_CODELOCATION_UNMAP_OVERRIDE, 'false')

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

    String getDefaultExclusionParameters() {
        return defaultExclusionParameters
    }

    void setDefaultExclusionParameters(boolean includeDefaultExclusionParameters) {
        if (includeDefaultExclusionParameters) {
            this.defaultExclusionParameters = DEFAULT_DETECT_EXCLUSION_PROPERTIES
        } else {
            this.defaultExclusionParameters = ""
        }
    }

    private void addDockerImageOptions() {
        if (dockerImage) {
            String dockerImageOptions = dockerImage.getDockerDetectParams()
            addDetectParameters(dockerImageOptions)
        }
    }

    private String removeDetectPropertyFromCommand(String inputDetectCommand, String detectProperty, String overrideVariableName, String valueIfOverrideNotFound) {
        String foundOverrideValue = pipelineConfiguration.scriptWrapper.getJenkinsProperty(overrideVariableName)

        if (null == foundOverrideValue && null == valueIfOverrideNotFound) {
            pipelineConfiguration.getLogger().info("Did not find environment variable set for ${overrideVariableName}. Not setting or overriding ${detectProperty} for detect execution")
            return inputDetectCommand
        } else if (null == foundOverrideValue && null != valueIfOverrideNotFound) {
            foundOverrideValue = valueIfOverrideNotFound
        }

        String newDetectCommand = inputDetectCommand
        if (inputDetectCommand.contains(detectProperty)) {
            String detectProjectVersionNameSetting = inputDetectCommand.substring(inputDetectCommand.indexOf(detectProperty))
            int nextConfigurationSetting = detectProjectVersionNameSetting.indexOf(' --')
            if (nextConfigurationSetting != -1) {
                detectProjectVersionNameSetting = detectProjectVersionNameSetting.substring(0, nextConfigurationSetting)
            }

            newDetectCommand = inputDetectCommand.replace(detectProjectVersionNameSetting, '')

            if (newDetectCommand.indexOf(detectProperty) != -1) {
                throw new IllegalArgumentException("Detect command had multiple occurrences of " + detectProperty)
            } else {
                pipelineConfiguration.getLogger().info("Successfully removed ${detectProperty} from the Detect command.")
            }
        }

        return newDetectCommand + " ${detectProperty}=${foundOverrideValue}"
    }
}
