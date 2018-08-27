#!/usr/bin/groovy

def call(String stageName = 'Post-Release Stage', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def runRelease = config.runRelease
    def buildTool = config.buildTool
    def exe = config.exe

    ProjectUtils projectUtils = new ProjectUtils()
    if (runRelease) {
        def newMavenVersion = projectUtils.increaseSemver('maven', exe)
        def newGradleVersion = projectUtils.increaseSemver('gradle', exe)
        sh "git commit -a -m \"Using the next snapshot post release ${newMavenVersion}  ${newGradleVersion}\""
        sh "git push origin ${branch}"
    }

}
