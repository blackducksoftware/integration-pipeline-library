package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class SetJdkStage extends Stage {
    public static final String DEFAULT_JDK_TOOL_NAME = 'jdk8'

    private final PipelineLogger pipelineLogger
    private String jdkToolName = DEFAULT_JDK_TOOL_NAME

    SetJdkStage(String name, PipelineLogger pipelineLogger) {
        super(name)
        this.pipelineLogger = pipelineLogger
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        pipelineLogger.info("Setting jdk = ${jdkToolName}")

        String toolHome = getScriptWrapper().tool(jdkToolName)
        getScriptWrapper().env().JAVA_HOME = "${toolHome}"
        String currentPath = getScriptWrapper().env().PATH
        getScriptWrapper().env().PATH = "${toolHome}/bin:${currentPath}"

        pipelineLogger.info("JAVA_HOME = ${getScriptWrapper().env().JAVA_HOME}")
        pipelineLogger.info("PATH = ${getScriptWrapper().env().PATH}")

    }

    String getJdkToolName() {
        return jdkToolName
    }

    void setJdkToolName(final String jdkToolName) {
        this.jdkToolName = jdkToolName
    }
}
