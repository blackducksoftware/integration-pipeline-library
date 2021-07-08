package com.synopsys.integration.pipeline.tools;

public class ArtifactoryProduct {
    private final String repoKey;
    private final String itemPathToCheck;
    private final String propertyPrefix;

    public ArtifactoryProduct(String repoKey, String itemPathToCheck, String propertyPrefix) {
        this.repoKey = repoKey;
        this.itemPathToCheck = itemPathToCheck;
        this.propertyPrefix = propertyPrefix;
    }

    public String getRepoKey() {
        return repoKey;
    }

    public String getItemPathToCheck() {
        return itemPathToCheck;
    }

    public String getPropertyPrefix() {
        return propertyPrefix;
    }

}
