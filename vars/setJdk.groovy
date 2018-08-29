#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    String jdk = config.jdk ?: 'jdk8'

    String jdkTool = tool "${jdk}"
    env.JAVA_HOME = "${jdkTool}"
    env.PATH = "${JAVA_HOME}/bin:${PATH}"

}
