package com.blackduck.integration.pipeline.results


import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.StageWrapper

class JunitStageWrapper extends StageWrapper {
    public static final String DEFAULT_JUNIT_XML_PATTERN = 'build/**/*.xml'

    private LinkedHashMap junitOptions = [allowEmptyResults: false, testResults: DEFAULT_JUNIT_XML_PATTERN]

    JunitStageWrapper(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void start() {

    }

    @Override
    void handleException(final Exception e) {

    }

    @Override
    void end() {
        getPipelineConfiguration().getScriptWrapper().junit(junitOptions)
    }

    LinkedHashMap getJunitOptions() {
        return junitOptions
    }

    void setJunitOptions(final LinkedHashMap junitOptions) {
        this.junitOptions = junitOptions
    }
}
