#!/usr/bin/groovy

def call(String nodeLabel, Closure body) {
    String label = nodeLabel
    if (null == nodeLabel || nodeLabel.trim().length() == 0) {
        label = 'integrations'
    }
    node(label) {
        body()
    }
}
