#!/usr/bin/groovy

import com.synopsys.integration.ProjectUtils

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def gitUrl = config.gitUrl

    def runRelease = config.get('runRelease', true)

    def releaseVersion = config.get('releaseVersion', "${RELEASE_VERSION}")

    ProjectUtils projectUtils = new ProjectUtils()
    if (null == releaseVersion || releaseVersion.trim().length() == 0) {
        def exe = config.exe
        releaseVersion = projectUtils.getProjectVersion(buildTool, exe)
    }
    if (!releaseVersion.contains('-SNAPSHOT')) {
        runRelease = true
    }

    integrationNode {
        setupStage {
            setJdk {}
        }
        gitStage {
            url = gitUrl
        }
        preReleaseStage {
            runRelease = runRelease
            releaseVersion = releaseVersion
        }
        gradleStage {
            buildCommand = 'clean'
        }
        mavenStage {
            buildCommand = 'clean'
        }

        newGarStage('Gradle GAR') {
            buildTool = 'gradle'

        }
        newGarStage('Maven GAR') {
            buildTool = 'maven'

        }
        postReleaseStage {
            runRelease = runRelease
            releaseVersion = releaseVersion
        }

    }
}
