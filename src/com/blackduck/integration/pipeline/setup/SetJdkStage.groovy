package com.blackduck.integration.pipeline.setup


import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

class SetJdkStage extends com.blackduck.integration.pipeline.model.Stage {
    public static final String DEFAULT_JDK_TOOL_NAME = 'jdk11'

    private String jdkToolName = DEFAULT_JDK_TOOL_NAME

    SetJdkStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getLogger().info("Setting jdk = ${jdkToolName}")

        String toolHome = getPipelineConfiguration().getScriptWrapper().tool(jdkToolName)
        String currentPath = getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('PATH')

        getPipelineConfiguration().getScriptWrapper().setJenkinsProperty('JAVA_HOME', toolHome)
        getPipelineConfiguration().getScriptWrapper().setJenkinsProperty('PATH', "${toolHome}/bin:${currentPath}")

        getPipelineConfiguration().getLogger().info("JAVA_HOME = ${getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('JAVA_HOME')}")
        getPipelineConfiguration().getLogger().info("PATH = ${getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('PATH')}")

        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("realpath ${toolHome}")
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${toolHome}/bin/java -version 2>&1")

        pipelineConfiguration.addToBuildDataMap("JAVA_HOME", toolHome)
    }

    String getJdkToolName() {
        return jdkToolName
    }

    void setJdkToolName(final String jdkToolName) {
        this.jdkToolName = jdkToolName
    }

}
