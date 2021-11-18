#!/usr/bin/groovy

def call(String nodeLabel = 'integrations-bd', Closure body) {
    String label = nodeLabel
    if (null == nodeLabel || nodeLabel.trim().length() == 0) {
        label = 'integrations-bd'
    }
    node(label) {
        body()
    }
}
