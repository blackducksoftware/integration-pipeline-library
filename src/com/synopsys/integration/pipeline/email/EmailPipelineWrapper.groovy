package com.synopsys.integration.pipeline.email

import com.synopsys.integration.pipeline.jenkins.BuildWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.PipelineWrapper

class EmailPipelineWrapper extends PipelineWrapper {
    final PipelineLogger pipelineLogger
    final String recipientList
    final String jobName
    final String buildNumber
    final String buildURL

    EmailPipelineWrapper(PipelineLogger pipelineLogger, String recipientList, String jobName, String buildNumber, String buildURL) {
        this(pipelineLogger, "Email Pipeline Wrapper", recipientList, jobName, buildNumber, buildURL)
    }

    EmailPipelineWrapper(PipelineLogger pipelineLogger, String wrapperName, String recipientList, String jobName, String buildNumber, String buildURL) {
        super(wrapperName)
        this.pipelineLogger = pipelineLogger
        this.recipientList = recipientList
        this.jobName = jobName
        this.buildNumber = buildNumber
        this.buildURL = buildURL
    }

    @Override
    void start() {

    }

    @Override
    void handleException(final Exception e) {

    }

    @Override
    void end() {
        String TO = "${recipientList}"
        String SUBJECT = '$DEFAULT_SUBJECT'
        String CONTENT = '$DEFAULT_CONTENT'

        BuildWrapper currentBuild = getScriptWrapper().currentBuild()
        if (currentBuild.result == "FAILURE") {
            pipelineLogger.error("Sending out Build Failure email: ${SUBJECT}")
            getScriptWrapper().emailext(CONTENT, SUBJECT, TO)
        } else if (null != currentBuild.getPreviousBuild() && currentBuild.getPreviousBuild().result != "SUCCESS" && currentBuild.resultIsBetterOrEqualTo(currentBuild.getPreviousBuild().result)) {
            SUBJECT = "${jobName} - Build #${buildNumber} - Fixed!"
            CONTENT = "${jobName} - Build #${buildNumber} - Fixed!\nCheck console output at ${buildURL} to view the results."
            pipelineLogger.info("Sending out Build Fixed email: ${SUBJECT}")
            getScriptWrapper().emailext(CONTENT, SUBJECT, TO)
        }
    }
}
