package com.synopsys.integration.pipeline.results


import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.StageWrapper

class JunitStageWrapper extends StageWrapper {
    public static final String DEFAULT_JUNIT_XML_PATTERN = 'build/**/*.xml'

    private JenkinsScriptWrapper scriptWrapper
    private LinkedHashMap junitOptions = [allowEmptyResults: false, testResults: DEFAULT_JUNIT_XML_PATTERN]

    JunitStageWrapper(JenkinsScriptWrapper scriptWrapper, String name) {
        super(name)
        this.scriptWrapper = scriptWrapper
    }

    @Override
    void start() {

    }

    @Override
    void handleException(final Exception e) {

    }

    @Override
    void end() {
        scriptWrapper.junit(junitOptions)
    }

    LinkedHashMap getJunitOptions() {
        return junitOptions
    }

    void setJunitOptions(final LinkedHashMap junitOptions) {
        this.junitOptions = junitOptions
    }
}
