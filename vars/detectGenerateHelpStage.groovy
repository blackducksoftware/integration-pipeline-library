#!/usr/bin/groovy

def call(String stageName = 'Generate Detect Help', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String detectURL = config.detectURL ?: "${HUB_DETECT_URL}"
    String ghPageTargetDir = config.ghPageTargetDir ?: 'gh-pages'

    def commandLines = []
    commandLines.add("#!/bin/bash")
    commandLines.add("bash <(curl -s ${detectURL}) -hdoc")

    stage(stageName) {
        dir("${WORKSPACE}") {
            sh 'mkdir html-help'
            dir('html-help') {
                sh commandLines.join(" \n")
                sh 'chmod 777 *.html'
                // Add the help .html document (generated from the previously released build) to our gh-pages
                sh "mv *.html ../${ghPageTargetDir}/"
            }
        }
    }
}
