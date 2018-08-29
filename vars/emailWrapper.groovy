#!/usr/bin/groovy

def call(String recipientList = 'justiceleague@synopsys.com', Closure body) {
    try {
        body()
    } catch (e) {
        // If there was an exception thrown, the build failed
        currentBuild.result = "FAILURE"
        throw e
    } finally {
        //        String TO = "${recipientList}"
        String TO = "jrichard@synopsys.com"
        String SUBJECT = '$DEFAULT_SUBJECT'
        String CONTENT = '$DEFAULT_CONTENT'
        if (currentBuild.result == "FAILURE") {
            println "Sending out Build Failure email: ${SUBJECT}"
            emailext body: CONTENT, subject: SUBJECT, to: TO
        } else if (null != currentBuild.getPreviousBuild() && currentBuild.getPreviousBuild().result != "SUCCESS" && currentBuild.resultIsBetterOrEqualTo(currentBuild.getPreviousBuild().result)) {
            SUBJECT = "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - Fixed!"
            CONTENT = "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - Fixed!\nCheck console output at ${env.BUILD_URL} to view the results."
            println "Sending out Build Fixed email: ${SUBJECT}"
            emailext body: CONTENT, subject: SUBJECT, to: TO
        }
    }
}
