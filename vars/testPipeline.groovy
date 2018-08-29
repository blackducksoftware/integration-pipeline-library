#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String message = config.get('message')
    Closure testBody = config.get(testBody)

    integrationNode {
        stage('Test') {
            println "${message}"
            testBody()
        }
    }

}
