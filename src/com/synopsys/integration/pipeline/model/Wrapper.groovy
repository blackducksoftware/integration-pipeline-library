package com.synopsys.integration.pipeline.model


import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

abstract class Wrapper implements Serializable {
    // Fields here must be public or they can't be accessed (in Jenkins at runtime) in sub classes
    public final PipelineConfiguration pipelineConfiguration
    public final String name
    public String relativeDirectory = '.'

    Wrapper(PipelineConfiguration pipelineConfiguration, String name) {
        this.pipelineConfiguration = pipelineConfiguration;
        this.name = name
    }

    abstract void start()

    String startMessage() {
        return ""
    }

    abstract void handleException(Exception e)

    String exceptionMessage() {
        return ""
    }

    abstract void end()

    String endMessage() {
        return ""
    }

    PipelineConfiguration getPipelineConfiguration() {
        return pipelineConfiguration
    }

    String getName() {
        return name
    }

    String getRelativeDirectory() {
        return relativeDirectory
    }

    void setRelativeDirectory(final String relativeDirectory) {
        this.relativeDirectory = relativeDirectory
    }

}
