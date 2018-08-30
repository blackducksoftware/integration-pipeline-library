#!/usr/bin/groovy

def call(String stageName = 'Generate Detect Help', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String detectURL = config.detectURL ?: "${HUB_DETECT_URL}"
    String relativeTargetDir = config.relativeTargetDir ?: 'gh-pages'

    def directoryToRunIn = "${WORKSPACE}"

    def commandLines = []
    commandLines.add("#!/bin/bash")
    commandLines.add("bash <(curl -s ${detectURL}) -hdoc")

    stage(stageName) {
        dir(directoryToRunIn) {
            sh "mkdir ${relativeTargetDir}"
            sh 'mkdir html-help'
            sh 'cd html-help'
            sh commandLines.join(" \n")
            sh 'chmod 777 *.html'
            // Add the help .html document (generated from the previously released build) to our gh-pages
            sh "cp *.html ../${relativeTargetDir}/"
        }
    }
}

//
//                    #!/bin/bash
//                    mkdir html-help
//                    cd html-help/
//                            bash <(curl -s https://blackducksoftware.github.io/hub-detect/hub-detect.sh) -hdoc
//                    chmod 777 *.html
//                    # Add the help .html document (generated from the previously released build) to our gh-pages
//                    cp *.html ../gh-pages/
