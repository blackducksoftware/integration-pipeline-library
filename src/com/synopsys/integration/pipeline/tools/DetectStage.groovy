package com.synopsys.integration.pipeline.tools

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.Stage

class DetectStage extends Stage {
    private final JenkinsScriptWrapper scriptWrapper
    private final String detectCommand

    private final String detectURL

    DetectStage(JenkinsScriptWrapper scriptWrapper, String stageName, String detectURL, String detectCommand) {
        super(stageName)
        this.scriptWrapper = scriptWrapper
        this.detectURL = detectURL
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
}
