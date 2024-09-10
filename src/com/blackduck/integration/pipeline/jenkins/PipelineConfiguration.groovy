package com.blackduck.integration.pipeline.jenkins

import com.blackduck.integration.pipeline.logging.PipelineLogger

class PipelineConfiguration implements Serializable {
    public PipelineLogger logger
    public JenkinsScriptWrapper scriptWrapper
    private Map<String, String> buildDataMap = new HashMap<>()

    public PipelineConfiguration(PipelineLogger logger, JenkinsScriptWrapper scriptWrapper) {
        this.logger = logger
        this.scriptWrapper = scriptWrapper
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

    void setLogger(final PipelineLogger logger) {
        this.logger = logger
    }

    void addToBuildDataMap(String key, String value) {
        buildDataMap.put(key, value)
    }

    Map<String, String> getBuildDataMap() {
        return buildDataMap
    }

}
