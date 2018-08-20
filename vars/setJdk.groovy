#!/usr/bin/groovy

def call(Closure body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def jdk = config.get('jdk', 'jdk8')
    def jdkTool = tool "${jdk}"

    env.JAVA_HOME = "${jdkTool}"
    env.PATH = "${JAVA_HOME}/bin:${PATH}"

}
