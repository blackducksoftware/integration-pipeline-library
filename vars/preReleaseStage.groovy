#!/usr/bin/groovy

import com.synopsys.integration.ProjectUtils

def call(String stageName = 'Pre-Release Stage', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildTool = config.buildTool
    String exe = config.exe
    boolean checkAllDependencies = config.checkAllDependencies ?: false

    String branch = config.branch ?: "${BRANCH}"



    stage(stageName) {
        ProjectUtils projectUtils = new ProjectUtils()
        projectUtils.initialize(this, buildTool, exe)
        def hasSnapshotDependencies = projectUtils.checkForSnapshotDependencies(checkAllDependencies)
        if (hasSnapshotDependencies) {
            def errorMessage = "Failing release build because of ${buildTool} SNAPSHOT dependencies"
            throw new Exception(errorMessage)
        }
        def version = projectUtils.getProjectVersion()
        if (version.contains('-SNAPSHOT')) {
            println "Removing SNAPSHOT from the Project Version"
            def newVersion = projectUtils.removeSnapshotFromProjectVersion()
            println "Commiting the release ${newVersion}"
            sh "git commit -am \"Release ${newVersion}\""
            println "Pushing release to branch ${branch}"
            sh "git push origin ${branch}"
        }
    }
}
