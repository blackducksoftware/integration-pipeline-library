package com.synopsys.integration.pipeline.email


import com.synopsys.integration.pipeline.model.PipelineWrapper

class EmailPipelineWrapper extends PipelineWrapper {
    private final String recipientList
    private final Object currentBuild
    private final Object emailext
    private final String jobName
    private final String buildNumber
    private final String buildURL

    EmailPipelineWrapper(String recipientList, Object currentBuild, Object emailext, String jobName, String buildNumber, String buildURL) {
        this("Email Pipeline Wrapper", recipientList, currentBuild, emailext, jobName, buildNumber, buildURL)
    }

    EmailPipelineWrapper(String wrapperName, String recipientList, Object currentBuild, Object emailext, String jobName, String buildNumber, String buildURL) {
        super(wrapperName)
        this.recipientList = recipientList
        this.currentBuild = currentBuild
        this.emailext = emailext
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

        if (currentBuild.result == "FAILURE") {
            println "Sending out Build Failure email: ${SUBJECT}"
            emailext body: CONTENT, subject: SUBJECT, to: TO
        } else if (null != currentBuild.getPreviousBuild() && currentBuild.getPreviousBuild().result != "SUCCESS" && currentBuild.resultIsBetterOrEqualTo(currentBuild.getPreviousBuild().result)) {
            SUBJECT = "${jobName} - Build #${buildNumber} - Fixed!"
            CONTENT = "${jobName} - Build #${buildNumber} - Fixed!\nCheck console output at ${buildURL} to view the results."
            println "Sending out Build Fixed email: ${SUBJECT}"
            emailext body: CONTENT, subject: SUBJECT, to: TO
        }
    }
}
