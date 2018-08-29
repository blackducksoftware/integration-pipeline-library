#!/usr/bin/groovy

def call(String stageName = 'Setup', Boolean cleanupWorkspace = true, Closure body) {
    stage(stageName) {
        if (cleanupWorkspace) {
            def workspaceExists = fileExists ''
            if (workspaceExists) {
                println "Cleaning the workspace"
                try {
                    sh "rm -rf *"
                    sh "rm -rf .*"
                } catch (Exception e) {
                    //ignore exceptions
                }
            } else {
                println "Skipping the workspace cleanup since the ${WORKSPACE} does not exist"
            }
        } else {
            println "Skipping the workspace cleanup at the Users request"
        }
        body()
    }
}
