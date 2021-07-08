package com.synopsys.integration.pipeline.tools;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration;
import com.synopsys.integration.pipeline.model.Stage;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

public class TestStage extends Stage {
    public TestStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name);
    }

    @Override
    public void stageExecution() throws Exception {
        pipelineConfiguration.getLogger().info("happy happy dance");
    }

}
