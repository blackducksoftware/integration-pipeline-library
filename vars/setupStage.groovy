#!/usr/bin/groovy

def call(String stageName = 'Setup', Closure body) {
    stage(stageName) {
        if (isUnix()) {
            sh 'rm -rf *'
        } else {
            def files = bat 'dir /a'
            bat "echo ${files}"
            bat "rmdir /s /q ${files}"
        }
        body()
    }
}
