#!/usr/bin/groovy

def call(String stageName = 'Maven Build', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def mavenToolName = config.get('toolName', 'maven-3')
    def mavenBuildCommand = config.get('buildCommand', '-U clean package deploy')

    def mvnHome = tool "${mavenToolName}"
    stage(stageName) {
        if (isUnix()) {
            sh "${mvnHome}/bin/mvn ${mavenBuildCommand}"
        } else {
            File directory = new File("${mvnHome}")
            directory = new File(directory, 'bin')
            def files = directory.listFiles()
            if (null != files && !files.isEmpty()) {
                files.each {
                    println it.getAbsolutePath()
                }
            }
            //            "${mvnHome}\\bin\\mvn.bat ${mavenBuildCommand}".execute().waitFor()
        }
    }
}
