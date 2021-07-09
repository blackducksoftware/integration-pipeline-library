package com.synopsys.integration.pipeline.tools;

class ArtifactoryProduct {
    final String repoKey;
    final String itemPathToCheck;
    final String propertyPrefix;

    ArtifactoryProduct(String repoKey, String itemPathToCheck, String propertyPrefix) {
        this.repoKey = repoKey
        this.itemPathToCheck = itemPathToCheck
        this.propertyPrefix = propertyPrefix
    }

}
