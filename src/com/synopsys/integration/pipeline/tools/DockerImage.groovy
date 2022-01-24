package com.synopsys.integration.pipeline.tools

import com.cloudbees.groovy.cps.NonCPS
import com.synopsys.integration.pipeline.SimplePipeline
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

class DockerImage {
    private PipelineConfiguration pipelineConfiguration
    private final String rawDockerImage
    private final String dockerImageOrg
    private final String dockerImageName
    private final String bdProjectName

    private String dockerImageVersion

    DockerImage(PipelineConfiguration pipelineConfiguration, String rawDockerImage) {
        this.pipelineConfiguration = pipelineConfiguration
        this.rawDockerImage = rawDockerImage

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

    private void setDockerImageVersion(String dockerImageVersion) {
        this.dockerImageVersion = dockerImageVersion
    }

    String getDockerDetectParams() {
        if (!dockerImageVersion?.trim()) {
            setDockerImageVersion(pipelineConfiguration.getScriptWrapper().getJenkinsProperty(SimplePipeline.PROJECT_VERSION))
            pipelineConfiguration.getLogger().info("Using environment variable ${SimplePipeline.PROJECT_VERSION} for docker image")
        }

        String fullDockerImageName = dockerImageOrg + '/' + dockerImageName + ':' + dockerImageVersion

        return "--detect.docker.image=${fullDockerImageName} --detect.project.name=${bdProjectName} --detect.project.version.name=${dockerImageVersion}"
    }
}
