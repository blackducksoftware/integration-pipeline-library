#!/usr/bin/groovy

def call(int numberOfBuildsToKeep = 20) {
    buildDiscarder(
        logRotator(
            artifactDaysToKeepStr: '',
            artifactNumToKeepStr: '',
            daysToKeepStr: '',
            numToKeepStr: "$numberOfBuildsToKeep"
        )
    )
}