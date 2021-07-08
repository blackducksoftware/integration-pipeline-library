package com.synopsys.integration.pipeline.tools;

import java.util.List;
import java.util.Map;

public class ArtifactoryPropertiesResponse {
    private final String uri;
    private final Map<String, List<String>> properties;

    public ArtifactoryPropertiesResponse(String uri, Map<String, List<String>> properties) {
        this.uri = uri;
        this.properties = properties;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, List<String>> getProperties() {
        return properties;
    }

}
