package com.synopsys.integration.pipeline.tools

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.Stage

class DetectStage extends Stage {
    private final JenkinsScriptWrapper scriptWrapper
    private final String detectCommand

    private String detectURL = config.detectURL ?: "${HUB_DETECT_URL}"

    DetectStage(JenkinsScriptWrapper scriptWrapper, String stageName, String detectCommand) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
        this.detectCommand = detectCommand
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        def commandLines = []
        commandLines.add("#!/bin/bash")
        commandLines.add("bash <(curl -s ${detectURL}) ${detectCommand}")
        scriptWrapper.executeCommand(commandLines.join(" \n"))
    }

    String getDetectCommand() {
        return detectCommand
    }

    String getDetectURL() {
        return detectURL
    }

    void setDetectURL(final String detectURL) {
        this.detectURL = detectURL
    }
}
