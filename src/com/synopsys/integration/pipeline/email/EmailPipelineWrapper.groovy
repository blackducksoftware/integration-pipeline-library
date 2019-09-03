package com.synopsys.integration.pipeline.email


import com.synopsys.integration.pipeline.model.PipelineWrapper

class EmailPipelineWrapper extends PipelineWrapper {
    private final String recipientList
    def final currentBuild
    def final emailext
    def final jobName
    def final buildNumber
    def final buildURL

    EmailPipelineWrapper(String recipientList, def currentBuild, def emailext, String jobName, String buildNumber, String buildURL) {
        super("Email Pipeline Wrapper")
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
