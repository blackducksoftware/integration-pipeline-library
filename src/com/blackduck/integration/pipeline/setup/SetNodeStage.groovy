package com.blackduck.integration.pipeline.setup

import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Stage

class SetNodeStage extends Stage {
    public static final String DEFAULT_NODE_DIR_PATH = '<NOT SET>'

    private String nodeDirPath = DEFAULT_NODE_DIR_PATH

    SetNodeStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        if (!nodeDirPath?.trim() || nodeDirPath == DEFAULT_NODE_DIR_PATH) {
            getPipelineConfiguration().getLogger().info("Not changing PATH for node as value is invalid.")
        } else {
            getPipelineConfiguration().getLogger().info("Setting node path = ${nodeDirPath}")

            String currentPath = getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('PATH')

            getPipelineConfiguration().getScriptWrapper().setJenkinsProperty('PATH', "${nodeDirPath}:${currentPath}")

            getPipelineConfiguration().getLogger().info("PATH = ${getPipelineConfiguration().getScriptWrapper().getJenkinsProperty('PATH')}")

            getPipelineConfiguration().getScriptWrapper().executeCommandWithException("realpath ${nodeDirPath}")
            getPipelineConfiguration().getScriptWrapper().executeCommandWithException("${nodeDirPath}/node --version 2>&1")

            pipelineConfiguration.addToBuildDataMap("NODE_HOME", nodeDirPath)
        }
    }

    String getNodeDirPath() {
        return nodeDirPath
    }

    void setNodeDirPath(final String nodeDirPath) {
        this.nodeDirPath = nodeDirPath
    }

}
