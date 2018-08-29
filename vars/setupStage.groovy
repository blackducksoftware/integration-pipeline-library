#!/usr/bin/groovy

def call(String stageName = 'Setup', Boolean cleanupWorkspace = true, Closure body) {
    stage(stageName) {
        if (cleanupWorkspace) {
            File workspace = new File("${WORKSPACE}")
            if (new File("${WORKSPACE}").exists()) {
                println "Cleaning the workspace"
                sh "rm -rf ${WORKSPACE}"
                sh "mkdir ${WORKSPACE}"
            } else {
                println "Skipping the workspace cleanup since the ${WORKSPACE} does not exist"
            }
        }
        body()
    }
}
