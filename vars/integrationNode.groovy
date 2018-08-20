#!/usr/bin/groovy

def call(String nodeLabel = 'integrations', Closure body) {
    node(nodeLabel) {
        body()
    }
}
