#!/usr/bin/groovy

def call() {
    string(
            defaultValue: 'Auto Release',
            description: 'The release note that you want the Auto Release tool to display.',
            name: 'COMMIT_MESSAGE',
            trim: true
    )
}
