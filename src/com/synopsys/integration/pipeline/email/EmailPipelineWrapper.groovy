package com.synopsys.integration.pipeline.email

import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.PipelineWrapper
import org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper

class EmailPipelineWrapper extends PipelineWrapper {
    final PipelineLogger pipelineLogger
    final JenkinsScriptWrapper scriptWrapper
    final String recipientList
    final String jobName
    final String buildNumber
    final String buildURL

    EmailPipelineWrapper(JenkinsScriptWrapper scriptWrapper, PipelineLogger pipelineLogger, String recipientList, String jobName, String buildNumber, String buildURL) {
        this(scriptWrapper, pipelineLogger, "Email Pipeline Wrapper", recipientList, jobName, buildNumber, buildURL)
    }

    EmailPipelineWrapper(JenkinsScriptWrapper scriptWrapper, PipelineLogger pipelineLogger, String wrapperName, String recipientList, String jobName, String buildNumber, String buildURL) {
        super(wrapperName)
        this.pipelineLogger = pipelineLogger;
        this.scriptWrapper = scriptWrapper
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

        RunWrapper currentBuild = scriptWrapper.currentBuild()
        if (currentBuild.result == "FAILURE") {
            pipelineLogger.error("Sending out Build Failure email: ${SUBJECT}")
            scriptWrapper.emailext(CONTENT, SUBJECT, TO)
        } else if (null != currentBuild.getPreviousBuild() && currentBuild.getPreviousBuild().result != "SUCCESS" && currentBuild.resultIsBetterOrEqualTo(currentBuild.getPreviousBuild().result)) {
            SUBJECT = "${jobName} - Build #${buildNumber} - Fixed!"
            CONTENT = "${jobName} - Build #${buildNumber} - Fixed!\nCheck console output at ${buildURL} to view the results."
            pipelineLogger.info("Sending out Build Fixed email: ${SUBJECT}")
            scriptWrapper.emailext(CONTENT, SUBJECT, TO)
        }
    }
}
