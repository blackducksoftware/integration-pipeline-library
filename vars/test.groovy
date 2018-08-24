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
    def version = projectUtils.getProjectVersion(tool, exe)
    println version
}
