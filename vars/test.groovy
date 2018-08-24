#!/usr/bin/groovy

import com.synopsys.integration.ProjectUtils

def call(String stageName = 'Setup', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def tool = config.tool
    def exe = config.exe

    ProjectUtils projectUtils = new ProjectUtils()
    if(tool.equals('maven')){
        def me = sh "whoami"
        sh "echo ${me}"
        sh "echo ${projectUtils.getMavenProjectVersionProcess(exe)}"
        sh "echo ${projectUtils.getMavenProjectVersionParse()}"
    } else {
        sh "echo ${projectUtils.getGradleProjectVersionProcess(exe)}"
        sh "echo ${projectUtils.getGradleProjectVersionParse()}"
    }
}
