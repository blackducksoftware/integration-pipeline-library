#!/usr/bin/groovy

import com.synopsys.integration.ConfigUtils
import com.synopsys.integration.pipeline.SimplePipeline

def call(String buildToolVar, String exeVar, Closure buildBody, Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()
    call(buildToolVar, exeVar, buildBody, config)
}

def call(String buildToolVar, String exeVar, Closure buildBody, Map config) {
    ConfigUtils configUtils = new ConfigUtils(config)

    String nodeName = config.nodeName
    String emailListVar = config.emailList
    String gitUrlVar = config.gitUrl
    String gitBranchVar = config.gitBranch ?: "${BRANCH}"

    String publishGitUrlVar = config.gitUrl.trim().replace("https://", "https://${GIT_USERNAME}:${GIT_PASSWORD}@")

    println "publishGitUrlVar is: ${publishGitUrlVar}"

    if (null == gitBranchVar || gitBranchVar.trim().length() == 0) {
        gitBranchVar = 'master'
    } else if (gitBranchVar.contains('/')) {
        gitBranchVar = gitBranchVar.substring(gitBranchVar.lastIndexOf('/') + 1).trim()
    }
    println "Using branch ${gitBranchVar}"

    String gitRelativeTargetDirVar = config.gitRelativeTargetDir

    String jdkVar = config.jdk

    Closure initialStageBody = config.initialStage
    Closure preBuildBody = config.preBuild
    Closure postBuildBody = config.postBuild

    String detectCommandVar = config.detectCommand

    boolean runGitHubReleaseVar
    try {
        String runGitHubReleaseString = configUtils.get('runGitHubRelease', "true")
        runGitHubReleaseVar = Boolean.valueOf(runGitHubReleaseString)
    } catch (MissingPropertyException e) {
        runGitHubReleaseVar = true
    }

    String releaseVersionVar = config.releaseVersion
    String ownerVar = config.owner
    String artifactFileVar = config.artifactFile
    String artifactPatternVar = config.artifactPattern
    String artifactDirectoryVar = config.artifactDirectory
    String projectVar = config.project
    String releaseDescriptionVar = config.releaseDescription

    boolean runArchiveVar
    try {
        String runArchiveString = configUtils.get('runArchive', "true")
        runArchiveVar = Boolean.valueOf(runArchiveString)
    } catch (MissingPropertyException e) {
        runArchiveVar = true
    }
    String archivePatternVar = config.archivePattern

    boolean runJunitVar
    try {
        String runJunitString = configUtils.get('runJunit', "true")
        runJunitVar = Boolean.valueOf(runJunitString)
    } catch (MissingPropertyException e) {
        runJunitVar = true
    }
    String junitXmlPatternVar = config.junitXmlPattern

    boolean runJacocoVar
    try {
        String runJacocoString = configUtils.get('runJacoco', "true")
        runJacocoVar = Boolean.valueOf(runJacocoString)
    } catch (MissingPropertyException e) {
        runJacocoVar = true
    }

    boolean runReleaseVar
    try {
        String runReleaseString = configUtils.get('runRelease', "${RUN_RELEASE}")
        runReleaseVar = Boolean.valueOf(runReleaseString)
    } catch (MissingPropertyException e) {
        runReleaseVar = false
    }

    boolean checkAllDependenciesVar
    try {
        String checkAllDependenciesString = configUtils.get('checkAllDependencies', "false")
        checkAllDependenciesVar = Boolean.valueOf(checkAllDependenciesString)
    } catch (MissingPropertyException e) {
        checkAllDependenciesVar = false
    }
    println "Going to run the Release ${runReleaseVar}"

    boolean runDetectVar
    try {
        String runDetectString = configUtils.get('runDetect', "true")
        runDetectVar = Boolean.valueOf(runDetectString)
    } catch (MissingPropertyException e) {
        runDetectVar = true
    }

    def additionalParameters = config.get('additionalParameters', null)
    def params = new ArrayList()
    if (additionalParameters) {
        params = new ArrayList(additionalParameters)
    }
    params.add(booleanParam(defaultValue: false, description: 'If you want to release the project, set this to true', name: SimplePipeline.RUN_RELEASE))
    params.add(string(defaultValue: 'Auto Release', description: 'The release note that you want the Auto Release tool to display.', name: 'COMMIT_MESSAGE', trim: true))

    params.add(gitParameter(branch: '',
            branchFilter: '',
            defaultValue: 'origin/master',
            description: 'The branch you want to build. If none are selected, origin/master will be chosen',
            listSize: '5',
            name: 'BRANCH',
            quickFilterEnabled: true,
            selectedValue: 'NONE',
            sortMode: 'NONE',
            tagFilter: '',
            type: 'PT_BRANCH_TAG',
            useRepository: gitUrlVar))

    properties([parameters(params),
                disableConcurrentBuilds(),
                buildDiscarder(logRotator(artifactDaysToKeepStr: '90', artifactNumToKeepStr: '2', daysToKeepStr: '14', numToKeepStr: '10')),
                pipelineTriggers([cron('@daily')])])

    def directoryToRunIn = ""

    integrationNode(nodeName) {
        emailWrapper(emailListVar) {
            setupStage {
                setJdk {
                    jdk = jdkVar
                }
            }
            directoryToRunIn = gitStage {
                url = gitUrlVar
                branch = gitBranchVar
                relativeTargetDir = gitRelativeTargetDirVar
                publishGitUrl = publishGitUrlVar
            }
            dir(directoryToRunIn) {
                if (null != initialStageBody) {
                    stage('Initial') {
                        initialStageBody()
                    }
                }
                if (runReleaseVar) {
                    preReleaseStage {
                        buildTool = buildToolVar
                        exe = exeVar
                        branch = gitBranchVar
                        checkAllDependencies = checkAllDependenciesVar
                        publishGitUrl = publishGitUrlVar
                    }
                }
                if (null != preBuildBody) {
                    stage('Pre Build') {
                        preBuildBody()
                    }
                }
                try {
                    buildBody()
                } catch (e) {
                    // If there was an exception thrown, the build failed
                    currentBuild.result = "FAILURE"
                    throw e
                } finally {
                    if (runJunitVar) {
                        junitStage {
                            xmlPattern = junitXmlPatternVar
                        }
                    }
                }
                if (null != postBuildBody) {
                    stage('Post Build') {
                        postBuildBody()
                    }
                }
                if (runGitHubReleaseVar) {
                    newGarStage {
                        buildTool = buildToolVar
                        exe = exeVar
                        releaseVersion = releaseVersionVar
                        owner = ownerVar
                        artifactFile = artifactFileVar
                        artifactPattern = artifactPatternVar
                        artifactDirectory = artifactDirectoryVar
                        project = projectVar
                        releaseDescription = releaseDescriptionVar
                        branch = gitBranchVar
                    }
                }
                if (runReleaseVar) {
                    postReleaseStage {
                        buildTool = buildToolVar
                        exe = exeVar
                        branch = gitBranchVar
                        publishGitUrl = publishGitUrlVar
                    }
                }
                if (runArchiveVar) {
                    archiveStage {
                        patterns = archivePatternVar
                    }
                }
                if (runJacocoVar) {
                    jacocoStage {}
                }
                if (runDetectVar) {
                    detectStage {
                        detectCommand = detectCommandVar
                    }
                }
            }
        }
    }
    return directoryToRunIn
}
