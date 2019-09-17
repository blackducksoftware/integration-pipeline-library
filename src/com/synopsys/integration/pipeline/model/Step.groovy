package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

abstract class Step implements Serializable {
    public final PipelineConfiguration pipelineConfiguration
    public String relativeDirectory = '.'

    Step(PipelineConfiguration pipelineConfiguration) {
        this.pipelineConfiguration = pipelineConfiguration
    }

    abstract void run() throws PipelineException, Exception

    public String getRelativeDirectory() {
        return relativeDirectory
    }

    public void setRelativeDirectory(final String relativeDirectory) {
        this.relativeDirectory = relativeDirectory
    }

    PipelineConfiguration getPipelineConfiguration() {
        return pipelineConfiguration
    }
}
