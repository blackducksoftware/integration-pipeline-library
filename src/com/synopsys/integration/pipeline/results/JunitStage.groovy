package com.synopsys.integration.pipeline.results

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.Stage

class JunitStage extends Stage {
    public static final String DEFAULT_JUNIT_XML_PATTERN = 'build/**/*.xml'

    private String xmlFilePattern = DEFAULT_JUNIT_XML_PATTERN
    private JenkinsScriptWrapper scriptWrapper


    JunitStage(JenkinsScriptWrapper scriptWrapper, String name) {
        super(name)
        this.scriptWrapper = scriptWrapper
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        scriptWrapper.step([$class: 'JUnitResultArchiver', testResults: "${xmlFilePattern}"])
    }

    String getXmlFilePattern() {
        return xmlFilePattern
    }

    void setXmlFilePattern(final String xmlFilePattern) {
        this.xmlFilePattern = xmlFilePattern
    }
}
