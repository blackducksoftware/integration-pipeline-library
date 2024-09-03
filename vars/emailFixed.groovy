#!/usr/bin/groovy
import com.blackduck.integration.Constants

def call(String recipientList = Constants.INTEGRATIONS_TEAM_EMAIL) {
    echo 'Sending out Build Fixed email'
    emailext(
            body: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - Fixed!\nCheck console output at ${env.BUILD_URL} to view the results.",
            subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - Fixed!",
            to: recipientList
    )
}
