#!/usr/bin/groovy

def call(String stageName = 'Run Detect', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def detectURL = config.get('detectURL', "${HUB_DETECT_URL}")
    def detectCommand = config.detectCommand

    def commandLines = []
    commandLines.add("#!/bin/bash")
    commandLines.add("bash <(curl -s ${detectURL}) ${detectCommand}")


    stage(stageName) {
        sh commandLines.join(" \n")
    }
}
