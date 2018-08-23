#!/usr/bin/groovy

def call(String stageName = 'Setup', Closure body) {
    stage(stageName) {
        if (isUnix()) {
            sh 'rm -rf *'
        } else {
            File currentDirectory = new File(".")
            def files = currentDirectory.listFiles()
            def delete = ''
            files.each {
                delete = it.getAbsolutePath() + " " + delete
            }
            bat "echo ${delete}"
            bat "rmdir /s /q ${delete}"
        }
        body()
    }
}
