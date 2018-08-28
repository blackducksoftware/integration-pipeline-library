#!/usr/bin/groovy

def call(String stageName = 'Setup', Closure body) {
    stage(stageName) {
        sh "rm -rf ${WORKSPACE}"
        sh "mkdir ${WORKSPACE}"
        body()
    }
}
