#!/usr/bin/groovy
import com.synopsys.integration.ConfigUtils
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapperImpl
import com.synopsys.integration.pipeline.logging.DefaultPipelineLogger
import com.synopsys.integration.pipeline.logging.LogLevel
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.utilities.ProjectUtils


def call(String stageName = 'Pre-Release Stage', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String buildTool = config.buildTool
    String exe = config.exe
    boolean checkAllDependencies = config.checkAllDependencies ?: false

    String branch = config.branch

    stage(stageName) {
        JenkinsScriptWrapper jenkinsScriptWrapper = new JenkinsScriptWrapperImpl(this)
        PipelineLogger pipelineLogger = new DefaultPipelineLogger(jenkinsScriptWrapper)
        pipelineLogger.setLogLevel(LogLevel.DEBUG)

        ConfigUtils configUtils = new ConfigUtils(config)
        boolean runReleaseVar = false
        //        try {
        //            runReleaseVar = configUtils.get('runRelease', Boolean.valueOf("${RUN_RELEASE}"))
        //        } catch (MissingPropertyException e) {
        //            runReleaseVar = false
        //        }

        boolean runQABuildVar = false
        //        try {
        //            runQABuildVar = configUtils.get('runQABuild', Boolean.valueOf("${RELEASE_QA_BUILD}"))
        //        } catch (MissingPropertyException e) {
        //            runQABuildVar = false
        //        }

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
            sh "git push origin ${branch}"
            println "Pushing release to branch ${branch}"
        }
    }
}
