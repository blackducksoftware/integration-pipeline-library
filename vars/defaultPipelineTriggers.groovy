#!/usr/bin/groovy


def call(int numberOfBuildsToKeep = 20) {
        pipelineTriggers(
                [
                        cron('H H(0-6) * * *'),
                        pollSCM('H/15 * * * *')
                ]
        )
}