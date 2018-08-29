#!/usr/bin/groovy

def call(String stageName = 'Setup', Boolean cleanupWorkspace = true, Closure body) {
    stage(stageName) {
        if (cleanupWorkspace) {
            def workspaceExists = fileExists ''
            if (workspaceExists) {
                println "Cleaning the workspace"
                sh "rm -rf ${WORKSPACE}"
                sh "mkdir ${WORKSPACE}"
            } else {
                println "Skipping the workspace cleanup since the ${WORKSPACE} does not exist"
            }
        } else {
            println "Skipping the workspace cleanup at the Users request"
        }
        body()
    }
}
