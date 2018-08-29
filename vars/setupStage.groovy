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
                    println "Ignore this error if the only problem was something like 'refusing to remove ...'\nThere was a problem cleaning the workspace ${e.getMessage()}"
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
