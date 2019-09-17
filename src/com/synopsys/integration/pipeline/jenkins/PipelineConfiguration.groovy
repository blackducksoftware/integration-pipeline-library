package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.logging.PipelineLogger

class PipelineConfiguration implements Serializable {
    public final PipelineLogger logger
    public JenkinsScriptWrapper scriptWrapper

    public PipelineConfiguration(PipelineLogger logger) {
        this.logger = logger

    }

    public JenkinsScriptWrapper getScriptWrapper() {
        return scriptWrapper
    }

    public void setScriptWrapper(final JenkinsScriptWrapper scriptWrapper) {
        this.scriptWrapper = scriptWrapper
    }

    public PipelineLogger getLogger() {
        return logger
    }

}
