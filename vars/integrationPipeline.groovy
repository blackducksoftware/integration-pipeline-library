#!/usr/bin/groovy

import com.synopsys.integration.ConfigUtils

def call(String buildToolVar, String exeVar, Closure buildBody, Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    ConfigUtils configUtils = new ConfigUtils(config)

    String nodeName = config.nodeName
    String emailListVar = config.emailList
    String gitUrlVar = config.gitUrl
    String gitBranchVar = config.gitBranch ?: "${BRANCH}"
    if (null == gitBranchVar || gitBranchVar.trim().length() == 0) {
        gitBranchVar = 'master'
    } else if (gitBranchVar.contains('/')) {
        gitBranchVar = gitBranchVar.substring(gitBranchVar.lastIndexOf('/') + 1).trim()
    }
    println "Using branch ${gitBranchVar}"

    String gitRelativeTargetDirVar = config.gitRelativeTargetDir

    Closure preBuildBody = config.preBuild
    Closure postBuildBody = config.postBuild

    String detectCommandVar = config.detectCommand

    boolean runGitHubReleaseVar = configUtils.get('runGitHubRelease', true)

    String releaseVersionVar = config.releaseVersion
    String ownerVar = config.owner
    String artifactFileVar = config.artifactFile
    String artifactPatternVar = config.artifactPattern
    String artifactDirectoryVar = config.artifactDirectory
    String projectVar = config.project
    String releaseDescriptionVar = config.releaseDescription

    boolean runArchiveVar = configUtils.get('runArchive', true)
    String archivePatternVar = config.archivePattern

    boolean runJunitVar = configUtils.get('runJunit', true)
    String junitXmlPatternVar = config.junitXmlPattern

    boolean runJacocoVar = configUtils.get('runJacoco', true)

    boolean runReleaseVar
    try {
        runReleaseVar = configUtils.get('runRelease', Boolean.valueOf("${RUN_RELEASE}"))
    } catch (MissingPropertyException e) {
        runReleaseVar = false
    }
    boolean checkAllDependenciesVar = configUtils.get('checkAllDependencies', false)
    println "Going to run the Release ${runReleaseVar}"

    boolean runDetectVar = configUtils.get('runDetect', true)

    def additionalParameters = config.get('additionalParameters', null)
    def params = new ArrayList()
    if (additionalParameters) {
        params = new ArrayList(additionalParameters)
    }
    params.add(booleanParam(defaultValue: false, description: 'If you want to release the project, set this to true', name: 'RUN_RELEASE'))
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
                setJdk {}
            }
            directoryToRunIn = gitStage {
                url = gitUrlVar
                branch = gitBranchVar
                relativeTargetDir = gitRelativeTargetDirVar
            }
            dir(directoryToRunIn) {
                if (runReleaseVar) {
                    preReleaseStage {
                        buildTool = buildToolVar
                        exe = exeVar
                        branch = gitBranchVar
                        checkAllDependencies = checkAllDependenciesVar
                    }
                }
                if (null != preBuildBody) {
                    stage('Pre Build') {
                        preBuildBody()
                    }
                }
                buildBody()
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
                    }
                }
                if (runArchiveVar) {
                    archiveStage {
                        patterns = archivePatternVar
                    }
                }
                if (runJunitVar) {
                    junitStage {
                        xmlPattern = junitXmlPatternVar
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
