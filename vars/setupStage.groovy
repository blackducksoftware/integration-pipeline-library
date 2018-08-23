#!/usr/bin/groovy

def call(String stageName = 'Setup', Closure body) {
    stage(stageName) {
        if (isUnix()) {
            sh 'rm -rf *'
        } else {
            bat 'rmdir /s *'
        }
        body()
    }
}
