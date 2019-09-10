package com.synopsys.integration.pipeline.results


import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.StageWrapper

class JunitStageWrapper extends StageWrapper {
    public static final String DEFAULT_JUNIT_XML_PATTERN = 'build/**/*.xml'

    private JenkinsScriptWrapper scriptWrapper
    private String xmlFilePattern = DEFAULT_JUNIT_XML_PATTERN
    private boolean allowEmptyResults = false

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
        scriptWrapper.junit(allowEmptyResults, xmlFilePattern)
    }

    String getXmlFilePattern() {
        return xmlFilePattern
    }

    void setXmlFilePattern(final String xmlFilePattern) {
        this.xmlFilePattern = xmlFilePattern
    }

    boolean getAllowEmptyResults() {
        return allowEmptyResults
    }

    void setAllowEmptyResults(final boolean allowEmptyResults) {
        this.allowEmptyResults = allowEmptyResults
    }
}
