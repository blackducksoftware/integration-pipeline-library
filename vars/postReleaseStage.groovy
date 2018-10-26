#!/usr/bin/groovy

import com.synopsys.integration.ProjectUtils

def call(String stageName = 'Post-Release Stage', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildTool = config.buildTool
    String exe = config.exe

    String branch = config.branch ?: "${BRANCH}"
    if (branch.contains('/')) {
        branch = branch.substring(branch.lastIndexOf('/') + 1)
    }


    stage(stageName) {
        ProjectUtils projectUtils = new ProjectUtils()
        projectUtils.initialize(this, buildTool, exe)
        println "Using the next snapshot post release"
        def newVersion = projectUtils.increaseSemver()
        sh "git commit -a -m \"Using the next snapshot post release ${newVersion}\""
        sh "git push origin ${branch}"
    }
}
