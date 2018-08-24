package com.synopsys.integration;

class GitHubAutoRelease {
    private final String buildTool;
    private final String owner;
    private final String artifactFile;
    private final String artifactType;
    private final String artifactDirectory;
    private final String project;
    private final String releaseVersion;
    private final String releaseDescription;

    public GitHubAutoRelease (final String buildTool, final String owner, final String artifactFile, final String artifactType, final String artifactDirectory, final String project, final String releaseVersion, final String releaseDescription) {
        this.buildTool = buildTool;
        this.owner = owner;
        this.artifactFile = artifactFile;
        this.artifactType = artifactType;
        this.artifactDirectory = artifactDirectory;
        this.project = project;
        this.releaseVersion = releaseVersion;
        this.releaseDescription = releaseDescription;
    }

    // git config --get remote.origin.url

}
