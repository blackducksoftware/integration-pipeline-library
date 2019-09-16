package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class SetJdkStage extends Stage {
    public static final String DEFAULT_JDK_TOOL_NAME = 'jdk8'

    private String jdkToolName = DEFAULT_JDK_TOOL_NAME

    SetJdkStage(String name) {
        super(name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        PipelineLogger logger = new DefaultPipelineLoger(getScriptWrapper())

        logger.info("Setting jdk = ${jdkToolName}")

        String toolHome = getScriptWrapper().tool(jdkToolName)
        getScriptWrapper().env().JAVA_HOME = "${toolHome}"
        String currentPath = getScriptWrapper().env().PATH
        getScriptWrapper().env().PATH = "${toolHome}/bin:${currentPath}"

        logger.info("JAVA_HOME = ${getScriptWrapper().env().JAVA_HOME}")
        logger.info("PATH = ${getScriptWrapper().env().PATH}")

    }

    String getJdkToolName() {
        return jdkToolName
    }

    void setJdkToolName(final String jdkToolName) {
        this.jdkToolName = jdkToolName
    }
}
