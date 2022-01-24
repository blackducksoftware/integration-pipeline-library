#!/usr/bin/groovy

def call() {
    booleanParam(
            defaultValue: false,
            description: 'If you want to release the project, set this to true',
            name: 'RUN_RELEASE'
    )
}

