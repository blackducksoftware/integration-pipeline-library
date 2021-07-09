package com.synopsys.integration.pipeline.tools

class ArtifactoryPropertiesResponse {
    final String uri
    final Map<String, List<String>> properties

    ArtifactoryPropertiesResponse(String uri, Map<String, List<String>> properties) {
        this.uri = uri
        this.properties = properties
    }

}
