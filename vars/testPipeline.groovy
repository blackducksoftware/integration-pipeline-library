#!/usr/bin/groovy
import com.synopsys.integration.pipeline.SimplePipeline

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def additionalParameters = config.get('additionalParameters', null)

    String nodeNameVar = config.nodeName
    String emailListVar = config.emailList
    String gitUrlVar = config.gitUrl
    String gitRelativeTargetDirVar = config.gitRelativeTargetDir

    String publishGitUrlVar = config.gitUrl.trim().replace("https://", "https://${GIT_USERNAME}:${GIT_PASSWORD}@")

    String gradleCommandVar = config.buildCommand

    def params = new ArrayList()
    if (additionalParameters) {
        params = new ArrayList(additionalParameters)
    }
    params.add(booleanParam(defaultValue: false, description: 'If you want to release the project, set this to true', name: SimplePipeline.RUN_RELEASE))
    params.add(string(defaultValue: 'Auto Release', description: 'The release note that you want the Auto Release tool to display.', name: 'COMMIT_MESSAGE', trim: true))
    params.add(string(defaultValue: 'master', description: 'The branch you want to build', name: 'BRANCH', trim: true))

    properties([parameters(params),
                disableConcurrentBuilds(),
                buildDiscarder(logRotator(artifactDaysToKeepStr: '90', artifactNumToKeepStr: '2', daysToKeepStr: '14', numToKeepStr: '10')),
                pipelineTriggers([cron('@daily')])])

    integrationNode(nodeNameVar) {
        emailWrapper(emailListVar) {
            setupStage {
                setJdk {}
            }
            def directoryToRunIn = gitStage {
                url = gitUrlVar
                gitRelativeTargetDir = gitRelativeTargetDirVar
            }
            dir(directoryToRunIn) {
                gradleStage {
                    buildCommand = gradleCommandVar
                }
            }
        }
    }


}
