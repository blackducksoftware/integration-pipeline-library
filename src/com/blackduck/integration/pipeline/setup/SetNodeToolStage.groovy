package com.blackduck.integration.pipeline.setup

import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Stage

class SetNodeToolStage extends Stage {
    public static final String DEFAULT_NODE_TOOL_NAME = 'NVM_16.15.1'

    private String nodeToolName = DEFAULT_NODE_TOOL_NAME

    SetNodeToolStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getLogger().info("Including Node tool '${nodeToolName}' in PATH")

        String nodeToolHome = getPipelineConfiguration().getScriptWrapper().tool(nodeToolName)
        String currentPath = getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('PATH')

        getPipelineConfiguration().getScriptWrapper().setJenkinsProperty('PATH', "${nodeToolHome}/bin:${currentPath}")

        getPipelineConfiguration().getLogger().info("PATH = ${getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('PATH')}")

        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("realpath ${nodeToolHome}")
        getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${nodeToolHome}/bin/node --version 2>&1")

        pipelineConfiguration.addToBuildDataMap("NODE_TOOL_HOME", nodeToolHome)
    }

    String getNodeToolName() {
        return nodeToolName
    }

    void setNodeToolName(final String nodeToolName) {
        String normalized = nodeToolName?.trim()

        if (!normalized) {
            getPipelineConfiguration().getLogger().info("Node tool name '${nodeToolName}' is invalid. Using existing: ${getNodeToolName()}")
        } else {
            this.nodeToolName = normalized
        }
    }

}
