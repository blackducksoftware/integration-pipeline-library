#!/usr/bin/groovy
import com.blackduck.integration.Constants

def call(String recipientList = Constants.INTEGRATIONS_TEAM_EMAIL) {
    echo 'Sending out Build Failure email'
    emailext(
            body: '$DEFAULT_CONTENT',
            subject: '$DEFAULT_SUBJECT',
            to: recipientList
    )
}
