#!/usr/bin/groovy

def call(String stageName = 'Setup', Boolean cleanupWorkspace = true, Closure body) {
    stage(stageName) {
        if (cleanupWorkspace && new File("${WORKSPACE}").exists()) {
            sh "rm -rf ${WORKSPACE}"
            sh "mkdir ${WORKSPACE}"
        }
        body()
    }
}
