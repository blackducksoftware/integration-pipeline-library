#!/usr/bin/groovy

def call(String stageName = 'Record JaCoCo coverage') {

    stage(stageName) {
        jacoco()
    }
}
