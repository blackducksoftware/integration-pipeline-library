package com.blackduck.integration.pipeline.tools

import com.cloudbees.groovy.cps.NonCPS
import com.blackduck.integration.pipeline.SimplePipeline
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

class DockerImage {
    public static final String DEFAULT_IMAGE_VERSION = '<DEFAULT_IMAGE_VERSION>'

    private PipelineConfiguration pipelineConfiguration
    private final String rawDockerImage
    private final boolean codeLocationNameAsImage
    private final String dockerImageOrg
    private final String dockerImageName
    private final String bdProjectName

    private String fullDockerImageName
    private String dockerImageVersion

    DockerImage(PipelineConfiguration pipelineConfiguration, String rawDockerImage, boolean codeLocationNameAsImage) {
        this.pipelineConfiguration = pipelineConfiguration
        this.rawDockerImage = rawDockerImage
        this.codeLocationNameAsImage = codeLocationNameAsImage

        int slashIndex = getSlashIndex()
        this.dockerImageOrg = rawDockerImage.substring(0, slashIndex)

        int dockerImageNameEndingIndex = rawDockerImage.indexOf(':')
        if (dockerImageNameEndingIndex == -1) {
            dockerImageNameEndingIndex = rawDockerImage.size()
        } else {
            this.dockerImageVersion = rawDockerImage.substring(dockerImageNameEndingIndex + 1)
        }
        this.dockerImageName = rawDockerImage.substring(slashIndex + 1, dockerImageNameEndingIndex)

        this.bdProjectName = dockerImageName + '-Docker'
    }

    @NonCPS
    private int getSlashIndex() {
        int slashIndex = rawDockerImage.indexOf('/')
        if (slashIndex == -1) {
            throw new IllegalArgumentException("The Docker Image provided must be in the format: ORG/IMAGE or ORG/IMAGE:VERSION + (${rawDockerImage})")
        }
        return slashIndex
    }

    String getDockerImageOrg() {
        return dockerImageOrg
    }

    String getDockerImageName() {
        return dockerImageName
    }

    String getBdProjectName() {
        return bdProjectName
    }

    String getDockerImageVersion() {
        return dockerImageVersion
    }

    String getFullDockerImageName() {
        return fullDockerImageName
    }

    String setFullDockerImageName() {
        setDockerImageVersion(getDockerVersionFromEnvironment())
        setFullDockerImageName(dockerImageOrg + '/' + dockerImageName + ':' + dockerImageVersion)
        return getFullDockerImageName()
    }

    void setFullDockerImageName(String fullDockerImageName) {
        this.fullDockerImageName = fullDockerImageName
    }

    private void setDockerImageVersion(String dockerImageVersion) {
        this.dockerImageVersion = dockerImageVersion
    }

    String getDockerVersionFromEnvironment() {
        String dockerImageVersion = getDockerImageVersion()?.trim()
        String projectVersion = pipelineConfiguration.getScriptWrapper().getJenkinsProperty(SimplePipeline.PROJECT_VERSION)?.trim()

        if (dockerImageVersion && !dockerImageVersion.equals(DEFAULT_IMAGE_VERSION)) {
            return dockerImageVersion
        } else if (projectVersion) {
            return projectVersion
        } else {
            return DEFAULT_IMAGE_VERSION
        }
    }

    String getCodeLocationNameAsImage(String version) {
        if (codeLocationNameAsImage) {
            pipelineConfiguration.getLogger().info("Using Detect option: detect.code.location.name")
            return ' --detect.code.location.name=' + dockerImageOrg + "_" + dockerImageName + '_' + version
        } else {
            return ''
        }
    }

    String getDockerDetectParams() {
        if (!dockerImageVersion?.trim()) {
            setDockerImageVersion(getDockerVersionFromEnvironment())
            pipelineConfiguration.getLogger().info("Using environment variable '${SimplePipeline.PROJECT_VERSION}' for docker image")
        }

        setFullDockerImageName(dockerImageOrg + '/' + dockerImageName + ':' + dockerImageVersion)

        return "--detect.docker.image=${fullDockerImageName} --detect.target.type=IMAGE --detect.project.name=${bdProjectName} --detect.project.version.name=${dockerImageVersion}"
    }
}
