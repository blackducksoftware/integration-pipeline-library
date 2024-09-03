package com.blackduck.integration.pipeline.setup


import com.cloudbees.groovy.cps.NonCPS
import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

class ApiTokenStage extends com.blackduck.integration.pipeline.model.Stage {
    ApiTokenStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        String apiTokensUrl = retrieveDefaultStringFromEnv('BLACKDUCK_API_TOKENS_URL')
        String blackDuckUrl = retrieveDefaultStringFromEnv('BLACKDUCK_URL')
        String blackDuckApiTokenName = retrieveDefaultStringFromEnv('BLACKDUCK_API_TOKEN_NAME')
        String blackDuckApiTokenUsername = retrieveDefaultStringFromEnv('BLACKDUCK_API_TOKEN_USERNAME')

        if (blackDuckUrl) {
            String url = "${apiTokensUrl}?vm=${blackDuckUrl}&name=${blackDuckApiTokenName}&username=${blackDuckApiTokenUsername}"
            String blackDuckApiToken = retrieveApiTokenFromServer(url)

            if (blackDuckApiToken?.trim()) {
                pipelineConfiguration.scriptWrapper.setJenkinsProperty('BLACKDUCK_API_TOKEN', blackDuckApiToken)
                pipelineConfiguration.getLogger().info("BLACKDUCK_API_TOKEN for server ${blackDuckUrl} set as ${blackDuckApiToken}")
            } else {
                throw new RuntimeException("${blackDuckUrl} is not defined within ${apiTokensUrl}")
            }

        } else {
            pipelineConfiguration.getLogger().info("BLACKDUCK_API_TOKEN not set as required BLACKDUCK_URL not set.")
        }
    }

    @NonCPS
    private static String retrieveApiTokenFromServer(String url) {
        URL apiTokenURL = new URL(url)
        HttpURLConnection httpURLConnection = (HttpURLConnection) apiTokenURL.openConnection()
        httpURLConnection.connect()
        new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())).withCloseable {
            String blackDuckApiToken = it.readLine()
            return blackDuckApiToken
        }
    }

}
