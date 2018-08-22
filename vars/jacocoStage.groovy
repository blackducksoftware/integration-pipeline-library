#!/usr/bin/groovy

def call(String stageName = 'Record JaCoCo coverage', Closure body) {

    stage(stageName) {
        step([$class: 'JacocoPublisher'])
    }
}
