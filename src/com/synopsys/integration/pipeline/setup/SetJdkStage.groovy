package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class SetJdkStage extends Stage {
    public static final String DEFAULT_JDK_TOOL_NAME = 'jdk8'


    private final JenkinsScriptWrapper scriptWrapper
    private String jdkToolName = DEFAULT_JDK_TOOL_NAME

    SetJdkStage(JenkinsScriptWrapper scriptWrapper, String name) {
        super(name)
        this.scriptWrapper = scriptWrapper;
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        PipelineLogger logger = new DefaultPipelineLoger(scriptWrapper)

        logger.info("Setting jdk = ${jdkToolName}")

        String toolHome = scriptWrapper.tool(jdkToolName)
        scriptWrapper.env().JAVA_HOME = "${toolHome}"
        String currentPath = scriptWrapper.env().PATH
        scriptWrapper.env().PATH = "${toolHome}/bin:${currentPath}"

        logger.info("JAVA_HOME = ${scriptWrapper.env().JAVA_HOME}")
        logger.info("PATH = ${scriptWrapper.env().PATH}")

    }

    String getJdkToolName() {
        return jdkToolName
    }

    void setJdkToolName(final String jdkToolName) {
        this.jdkToolName = jdkToolName
    }
}
