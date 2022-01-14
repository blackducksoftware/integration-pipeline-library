#!/usr/bin/groovy

def call(String defaultValue = 'origin/master') {
    gitParameter(
            branch: '',
            branchFilter: '.*',
            defaultValue: defaultValue,
            description: "The branch you want to build. If none are selected, $defaultValue will be chosen",
            name: 'BRANCH',
            quickFilterEnabled: false,
            selectedValue: 'NONE',
            sortMode: 'NONE',
            tagFilter: '*',
            type: 'PT_BRANCH_TAG'
    )
}