package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class SetJdkStage extends Stage {
    public static final String DEFAULT_JDK_TOOL_NAME = 'OpenJDK 11'

    private String jdkToolName = DEFAULT_JDK_TOOL_NAME

    SetJdkStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        jdkToolName = setJdkToolNameEnvironment()
        getPipelineConfiguration().getLogger().info("Setting jdk = ${jdkToolName}")

        String toolHome = getPipelineConfiguration().getScriptWrapper().tool(jdkToolName)
        getPipelineConfiguration().getScriptWrapper().setJenkinsProperty('JAVA_HOME', toolHome)
        String currentPath = getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('PATH')
        getPipelineConfiguration().getScriptWrapper().setJenkinsProperty('PATH', "${toolHome}/bin:${currentPath}")

        getPipelineConfiguration().getLogger().info("JAVA_HOME = ${getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('JAVA_HOME')}")
        getPipelineConfiguration().getLogger().info("PATH = ${getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('PATH')}")
    }

    String getJdkToolName() {
        return jdkToolName
    }

    void setJdkToolName(final String jdkToolName) {
        this.jdkToolName = jdkToolName
    }

    private String setJdkToolNameEnvironment() {
        def envJdkToolName = pipelineConfiguration.scriptWrapper.getJenkinsProperty('INT_JDK_TOOL_NAME')
        return envJdkToolName?.trim() ? envJdkToolName : jdkToolName
    }
}
