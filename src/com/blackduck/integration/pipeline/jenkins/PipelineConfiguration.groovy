package com.blackduck.integration.pipeline.jenkins

class PipelineConfiguration implements Serializable {
    public com.blackduck.integration.pipeline.logging.PipelineLogger logger
    public JenkinsScriptWrapper scriptWrapper
    private Map<String, String> buildDataMap = new HashMap<>()

    public PipelineConfiguration(com.blackduck.integration.pipeline.logging.PipelineLogger logger, JenkinsScriptWrapper scriptWrapper) {
        this.logger = logger
        this.scriptWrapper = scriptWrapper
    }

    public JenkinsScriptWrapper getScriptWrapper() {
        return scriptWrapper
    }

    public void setScriptWrapper(final JenkinsScriptWrapper scriptWrapper) {
        this.scriptWrapper = scriptWrapper
    }

    public com.blackduck.integration.pipeline.logging.PipelineLogger getLogger() {
        return logger
    }

    void setLogger(final com.blackduck.integration.pipeline.logging.PipelineLogger logger) {
        this.logger = logger
    }

    void addToBuildDataMap(String key, String value) {
        buildDataMap.put(key, value)
    }

    Map<String, String> getBuildDataMap() {
        return buildDataMap
    }

}
