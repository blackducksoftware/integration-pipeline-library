#!/usr/bin/groovy

import com.synopsys.integration.ProjectUtils

def call(String stageName = 'Setup', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    ProjectUtils projectUtils = new ProjectUtils()
    if(config.tool.equals('maven')){
        sh "echo ${projectUtils.getMavenProjectVersionProcess()}"
        sh "echo ${projectUtils.getMavenProjectVersionParse()}"
    } else {
        sh "echo ${projectUtils.getGradleProjectVersionProcess()}"
        sh "echo ${projectUtils.getGradleProjectVersionParse()}"
    }
}
