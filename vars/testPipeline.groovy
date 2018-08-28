#!/usr/bin/groovy

import com.synopsys.integration.ProjectUtils

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def gitUrl = config.gitUrl

    def userRunRelease = config.get('runRelease', Boolean.valueOf("${RUN_RELEASE}"))
    println "Going to run the Release ${runRelease}"
    ProjectUtils projectUtils = new ProjectUtils(this)

    integrationNode {
        setupStage {
            setJdk {}
        }
        gitStage {
            url = gitUrl
        }
        preReleaseStage {
            runRelease = userRunRelease
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
            runRelease = userRunRelease
        }

    }
}
