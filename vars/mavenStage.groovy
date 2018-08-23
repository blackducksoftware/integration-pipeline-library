#!/usr/bin/groovy

def call(String stageName = 'Maven Build', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def mavenExe = config.exe
    def mavenToolName = config.get('toolName', 'maven-3')
    def mavenBuildCommand = config.get('buildCommand', '-U clean package deploy')

    def mvnHome = tool "${mavenToolName}"

    if (null == mavenExe || mavenExe.trim().length() == 0) {
        File mavenHome = new File("${mvnHome}")
        File mavenBin = new File(mavenHome, 'bin')
        File mavenFile = mavenBin.listFiles().find {
            it.getName().contains('mvn')
        }
        mavenExe = mavenFile.getAbsolutePath()
    }

    stage(stageName) {
        if (isUnix()) {
            sh "${mavenExe} ${mavenBuildCommand}"
        } else {
            "${mavenExe} ${mavenBuildCommand}".execute().waitFor()
        }
    }
}
