#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    def emailList = config.emailList
    def gitUrl = config.gitUrl
    def gradleCommand = config.buildCommand

    def detectCommand = config.detectCommand

    def runGitHubRelease = config.get('runGitHubRelease', true)
    def gradleExe = config.gradleExe
    def releaseVersion = config.releaseVersion
    def owner = config.owner
    def artifactFile = config.artifactFile
    def artifactPattern = config.artifactPattern
    def artifactDirectory = config.artifactDirectory
    def project = config.project
    def releaseDescription = config.releaseDescription

    def runArchive = config.get('runArchive', true)
    def archivePattern = config.archivePattern

    def runJunit = config.get('runJunit', true)
    def junitXmlPattern = config.junitXmlPattern

    def runJacoco = config.get('runJacoco', true)

    def userRunRelease = config.get('runRelease', Boolean.valueOf("${RUN_RELEASE}"))
    def userCheckAllDependencies = config.get('checkAllDependencies', false)
    println "Going to run the Release ${userRunRelease}"

    integrationNode {
        emailWrapper(emailList) {
            setupStage {
                setJdk {}
            }
            gitStage {
                url = gitUrl
            }
            if (userRunRelease) {
                preReleaseStage {
                    buildTool = 'gradle'
                    exe = gradleExe
                    checkAllDependencies = userCheckAllDependencies
                }
            }
            gradleStage {
                buildCommand = gradleCommand
            }
            detectStage {
                detectCommand = detectCommand
            }
            if (runGitHubRelease) {
                newGarStage {
                    buildTool = 'gradle'
                    exe = gradleExe
                    releaseVersion = releaseVersion
                    owner = owner
                    artifactFile = artifactFile
                    artifactPattern = artifactPattern
                    artifactDirectory = artifactDirectory
                    project = project
                    releaseDescription = releaseDescription
                }
            }
            if (userRunRelease) {
                postReleaseStage {
                    buildTool = 'gradle'
                    exe = gradleExe
                }
            }
            if (runArchive) {
                archiveStage {
                    patterns = archivePattern
                }
            }
            if (runJunit) {
                junitStage {
                    xmlPattern = junitXmlPattern
                }
            }
            if (runJacoco) {
                jacocoStage {}
            }
        }
    }
}
