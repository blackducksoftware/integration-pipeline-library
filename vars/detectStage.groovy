#!/usr/bin/groovy

def call(String stageName = 'Run Detect', Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String detectURL = config.detectURL ?: "${HUB_DETECT_URL}"
    String detectCommand = config.detectCommand

    def commandLines = []
    commandLines.add("#!/bin/bash")
    commandLines.add("bash <(curl -s ${detectURL}) ${detectCommand}")


    stage(stageName) {
        sh commandLines.join(" \n")
    }
}
