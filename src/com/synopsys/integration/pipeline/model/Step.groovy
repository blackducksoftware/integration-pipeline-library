package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper

abstract class Step implements Serializable {
    public String relativeDirectory = '.'
    public JenkinsScriptWrapper scriptWrapper

    Step() {}

    abstract void run() throws PipelineException, Exception

    public String getRelativeDirectory() {
        return relativeDirectory
    }

    public void setRelativeDirectory(final String relativeDirectory) {
        this.relativeDirectory = relativeDirectory
    }

    public JenkinsScriptWrapper getScriptWrapper() {
        return scriptWrapper
    }

    public void setScriptWrapper(final JenkinsScriptWrapper scriptWrapper) {
        this.scriptWrapper = scriptWrapper
    }
}
