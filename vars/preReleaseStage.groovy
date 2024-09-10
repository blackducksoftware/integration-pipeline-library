#!/usr/bin/groovy
import com.blackduck.integration.ConfigUtils
import com.blackduck.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.blackduck.integration.pipeline.jenkins.JenkinsScriptWrapperImpl
import com.blackduck.integration.pipeline.logging.DefaultPipelineLogger
import com.blackduck.integration.pipeline.logging.LogLevel
import com.blackduck.integration.pipeline.logging.PipelineLogger
import com.blackduck.integration.pipeline.utilities.ProjectUtils


def call(String stageName = 'Pre-Release Stage', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildTool = config.buildTool
    String exe = config.exe
    boolean checkAllDependencies = config.checkAllDependencies ?: false
    String publishGitUrl = config.publishGitUrl.trim()

    String branch = config.branch

    stage(stageName) {
        JenkinsScriptWrapper jenkinsScriptWrapper = new JenkinsScriptWrapperImpl(this)
        PipelineLogger pipelineLogger = new DefaultPipelineLogger(jenkinsScriptWrapper)
        pipelineLogger.setLogLevel(LogLevel.DEBUG)

        ConfigUtils configUtils = new ConfigUtils(config)
        boolean runReleaseVar
        try {
            String runReleaseString = configUtils.get('runRelease', "${RUN_RELEASE}")
            runReleaseVar = Boolean.valueOf(runReleaseString)
        } catch (MissingPropertyException e) {
            runReleaseVar = false
        }

        boolean runQABuildVar
        try {
            String runQABuildString = configUtils.get('runQABuild', "${RUN_QA_BUILD}")
            runQABuildVar = Boolean.valueOf(runQABuildString)
        } catch (MissingPropertyException e) {
            runQABuildVar = false
        }

        ProjectUtils projectUtils = new ProjectUtils(pipelineLogger, jenkinsScriptWrapper)
        projectUtils.initialize(buildTool, exe)
        def hasSnapshotDependencies = projectUtils.checkForSnapshotDependencies(checkAllDependencies)
        if (hasSnapshotDependencies) {
            def errorMessage = "Failing release build because of ${buildTool} SNAPSHOT dependencies"
            throw new Exception(errorMessage)
        }
        def version = projectUtils.getProjectVersion()
        if (version.contains('-SNAPSHOT')) {
            println "Removing SNAPSHOT from the Project Version"
            def newVersion = projectUtils.updateVersionForRelease(runReleaseVar, runQABuildVar)
            println "Commiting the release ${newVersion}"
            sh "git commit -am \"Release ${newVersion}\""
            sh "git push ${publishGitUrl} ${branch}"
            println "Pushing release to branch ${branch}"
        }
    }
}
