#!/usr/bin/groovy

import com.synopsys.integration.ProjectUtils

def call(String stageName = 'Post-Release Stage', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def runRelease = config.runRelease
    def buildTool = config.buildTool
    def exe = config.exe
    def branch = config.get('branch', "${BRANCH}")

    ProjectUtils projectUtils = new ProjectUtils(this)
    if (runRelease) {
        println "Using the next snapshot post release"
        def newMavenVersion = projectUtils.increaseSemver('maven', exe)
        def newGradleVersion = projectUtils.increaseSemver('gradle', exe)
        sh "git commit -a -m \"Using the next snapshot post release ${newMavenVersion}  ${newGradleVersion}\""
        sh "git push origin ${branch}"
    }

}
