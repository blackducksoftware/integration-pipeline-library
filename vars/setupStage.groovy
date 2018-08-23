#!/usr/bin/groovy

def call(String stageName = 'Setup', Closure body) {
    stage(stageName) {
        if (isUnix()) {
            sh 'rm -rf *'
        } else {
            def currentDirectoryPath = pwd()
            File currentDirectory = new File(currentDirectoryPath)
            def files = currentDirectory.listFiles()
            if (null != files && !files.isEmpty()) {
                def delete = ''
                files.each {
                    delete = it.getAbsolutePath() + " " + delete
                }
                bat "echo ${delete}"
                bat "rmdir /s /q ${delete}"
            }
        }
        body()
    }
}
