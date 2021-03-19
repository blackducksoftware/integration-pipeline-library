package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class ApiTokenStage extends Stage {
    ApiTokenStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        String apiTokensUrl = getPropertyValue('BLACKDUCK_API_TOKENS_URL')
        String blackDuckUrl = getPropertyValue('BLACKDUCK_URL')
        String blackDuckApiTokenName = getPropertyValue('BLACKDUCK_API_TOKEN_NAME')
        String blackDuckApiTokenUsername = getPropertyValue('BLACKDUCK_API_TOKEN_USERNAME')

        if (blackDuckUrl) {
            String url = "${apiTokensUrl}/puretoken?vm=${blackDuckUrl}&name=${blackDuckApiTokenName}&username=${blackDuckApiTokenUsername}"
            URL apiTokenURL = new URL(url)
            HttpURLConnection httpURLConnection = (HttpURLConnection) apiTokenURL.openConnection()
            httpURLConnection.connect()
            new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream())).withCloseable {
                String blackDuckApiToken = it.readLine()
                pipelineConfiguration.scriptWrapper.setJenkinsProperty('BLACKDUCK_API_TOKEN', blackDuckApiToken)
            }
        }
    }

    private String getPropertyValue(String propertyName) {
        String value = pipelineConfiguration.scriptWrapper.getJenkinsProperty(propertyName)
        return value ? value : ''
    }

}
