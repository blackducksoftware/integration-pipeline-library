package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.exception.PipelineException

abstract class Step implements Serializable {
    public String relativeDirectory = '.'

    Step() {}

    abstract void run() throws PipelineException, Exception

    public String getRelativeDirectory() {
        return relativeDirectory
    }

    public void setRelativeDirectory(final String relativeDirectory) {
        this.relativeDirectory = relativeDirectory
    }
}
