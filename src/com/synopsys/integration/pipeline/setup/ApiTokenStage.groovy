package com.synopsys.integration.pipeline.setup

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class ApiTokenStage extends Stage {
    public static final String REQUEST_FORMAT = "%s/puretoken?vm=%s&name=%s&username=%s"

    ApiTokenStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        //        String apiTokensUrl = getPropertyValue("BLACKDUCK_API_TOKENS_URL")
        //        String blackDuckUrl = getPropertyValue("BLACKDUCK_URL")
        //        String blackDuckApiTokenName = getPropertyValue("BLACKDUCK_API_TOKEN_NAME")
        //        String blackDuckApiTokenUsername = getPropertyValue("BLACKDUCK_API_TOKEN_USERNAME")
        //
        //        if (null != blackDuckUrl && !"".equals(blackDuckUrl)) {
        //            String url = String.format(REQUEST_FORMAT, apiTokensUrl, blackDuckUrl, blackDuckApiTokenName, blackDuckApiTokenUsername)
        //            URL apiTokenURL = new URL(url)
        //            HttpURLConnection httpURLConnection = (HttpURLConnection) apiTokenURL.openConnection()
        //            httpURLConnection.connect()
        //            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
        //                String blackDuckApiToken = bufferedReader.readLine()
        //                pipelineConfiguration.scriptWrapper.setJenkinsProperty("BLACKDUCK_API_TOKEN", blackDuckApiToken)
        //            }
        //        }
    }

    private String getPropertyValue(String propertyName) {
        return pipelineConfiguration.scriptWrapper.getJenkinsProperty(propertyName)
    }

}
