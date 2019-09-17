package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class SetJdkStage extends Stage {
    public static final String DEFAULT_JDK_TOOL_NAME = 'jdk8'

    private String jdkToolName = DEFAULT_JDK_TOOL_NAME

    SetJdkStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getLogger().info("Setting jdk = ${jdkToolName}")

        String toolHome = getPipelineConfiguration().getScriptWrapper().tool(jdkToolName)
        getPipelineConfiguration().getScriptWrapper().env().JAVA_HOME = "${toolHome}"
        String currentPath = getPipelineConfiguration().getScriptWrapper().env().PATH
        getPipelineConfiguration().getScriptWrapper().env().PATH = "${toolHome}/bin:${currentPath}"

        getPipelineConfiguration().getLogger().info("JAVA_HOME = ${getPipelineConfiguration().getScriptWrapper().env().JAVA_HOME}")
        getPipelineConfiguration().getLogger().info("PATH = ${getPipelineConfiguration().getScriptWrapper().env().PATH}")

    }

    String getJdkToolName() {
        return jdkToolName
    }

    void setJdkToolName(final String jdkToolName) {
        this.jdkToolName = jdkToolName
    }
}
