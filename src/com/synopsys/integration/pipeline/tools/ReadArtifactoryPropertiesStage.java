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

public class ReadArtifactoryPropertiesStage extends Stage {
    public static final String PUBLIC_ARTIFACTORY = "https://sig-repo.synopsys.com/artifactory";
    public static final String INTERNAL_ARTIFACTORY = "https://artifactory.internal.synopsys.com/artifactory";

    private Gson gson;
    private IntHttpClient httpClient;

    public ReadArtifactoryPropertiesStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name);
        gson = new Gson();
        httpClient = new IntHttpClient(new SilentIntLogger(), gson, 120, false, ProxyInfo.NO_PROXY_INFO);
    }

    @Override
    public void stageExecution() throws Exception {
        for (ArtifactoryProduct artifactoryProduct : ArtifactoryProducts.artifactoryProducts) {
            String repoKey = artifactoryProduct.getRepoKey();
            String itemPath = artifactoryProduct.getItemPathToCheck();
            HttpUrl propertiesUrl = new HttpUrl(String.format("%s/api/storage/%s/%s", PUBLIC_ARTIFACTORY, repoKey, itemPath));

            Request request = new Request.Builder(propertiesUrl).build();
            try (Response response = httpClient.execute(request)) {
                String content = response.getContentString(StandardCharsets.UTF_8);
                ArtifactoryPropertiesResponse propertiesResponse = gson.fromJson(content, ArtifactoryPropertiesResponse.class);
                Map<String, List<String>> properties = propertiesResponse.getProperties();
                pipelineConfiguration.getLogger().info(String.format("Properties for: %s/%s", repoKey, itemPath));
                for (String name : properties.keySet()) {
                    if (name.startsWith(artifactoryProduct.getPropertyPrefix())) {
                        String values = StringUtils.join(properties.get(name));
                        String propertyOutput = String.format("%s: %s", name, values);
                        pipelineConfiguration.getLogger().info(propertyOutput);
                    }
                }
            }
        }
    }

}
