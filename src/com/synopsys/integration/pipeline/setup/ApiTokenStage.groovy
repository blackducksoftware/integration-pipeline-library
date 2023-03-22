package com.synopsys.integration.pipeline.setup

import com.cloudbees.groovy.cps.NonCPS
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.Stage

class ApiTokenStage extends Stage {
    ApiTokenStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        PipelineLogger logger = pipelineConfiguration.getLogger()
        String apiTokensUrl = retrieveDefaultStringFromEnv('BLACKDUCK_API_TOKENS_URL')
        String blackDuckUrl = retrieveDefaultStringFromEnv('BLACKDUCK_URL')
        String blackDuckApiTokenName = retrieveDefaultStringFromEnv('BLACKDUCK_API_TOKEN_NAME')
        String blackDuckApiTokenUsername = retrieveDefaultStringFromEnv('BLACKDUCK_API_TOKEN_USERNAME')

        if (blackDuckUrl) {
            logger.info("apiTokensUrl-->${apiTokensUrl}")
            logger.info("blackDuckUrl-->${blackDuckUrl}")
            logger.info("blackDuckApiTokenName-->${blackDuckApiTokenName}")
            logger.info("blackDuckApiTokenUsername-->${blackDuckApiTokenUsername}")
            
            if (apiTokensUrl?.trim()) {
                throw new RuntimeException("BLACKDUCK_API_TOKENS_URL is not defined within environment")
            }

            if (blackDuckApiTokenName?.trim() || blackDuckApiTokenName?.trim()) {
                throw new RuntimeException("BLACKDUCK_API_TOKEN_NAME or BLACKDUCK_API_TOKEN_USERNAME must be defined within environment")
            }

            String url = "${apiTokensUrl}?vm=${blackDuckUrl}&name=${blackDuckApiTokenName}&username=${blackDuckApiTokenUsername}"
            logger.info("Trying to get the token from url --> ${url}")
            String blackDuckApiToken = retrieveApiTokenFromServer(url)

            if (blackDuckApiToken?.trim()) {
                pipelineConfiguration.scriptWrapper.setJenkinsProperty('BLACKDUCK_API_TOKEN', blackDuckApiToken)
                logger.info("BLACKDUCK_API_TOKEN for server ${blackDuckUrl} set as ${blackDuckApiToken}")
            } else {
                throw new RuntimeException("${blackDuckUrl} is not defined within ${apiTokensUrl}")
            }

        } else {
            logger.info("BLACKDUCK_API_TOKEN not set as required BLACKDUCK_URL not set.")
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
