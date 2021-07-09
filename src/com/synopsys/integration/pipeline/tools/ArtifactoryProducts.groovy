package com.synopsys.integration.pipeline.tools

class ArtifactoryProducts {
    public static final List<ArtifactoryProduct> artifactoryProducts = new LinkedList<>()

    public static final String NUGET_REPO = 'bds-integrations-nuget-release'
    public static final String MAVEN_REPO = 'bds-integrations-release'

    public static final String DETECT_REPOPATH = 'com/synopsys/integration/synopsys-detect'
    public static final String DOCKER_INSPECTOR_REPOPATH = 'com/synopsys/integration/blackduck-docker-inspector'

    static {
        artifactoryProducts.add(create(NUGET_REPO, 'BlackduckNugetInspector', 'NUGET_INSPECTOR'))
        artifactoryProducts.add(create(NUGET_REPO, 'IntegrationNugetInspector', 'NUGET_INSPECTOR'))
        artifactoryProducts.add(create(NUGET_REPO, 'NugetDotnet3Inspector', 'NUGET_DOTNET3_INSPECTOR'))
        artifactoryProducts.add(create(NUGET_REPO, 'NugetDotnet5Inspector', 'NUGET_DOTNET5_INSPECTOR'))

        artifactoryProducts.add(create(MAVEN_REPO, DETECT_REPOPATH, 'DETECT'))
        artifactoryProducts.add(create(MAVEN_REPO, DETECT_REPOPATH, 'DETECT_FONT_BUNDLE'))

        artifactoryProducts.add(create(MAVEN_REPO, DOCKER_INSPECTOR_REPOPATH, 'DOCKER_INSPECTOR_AIR_GAP'))
        artifactoryProducts.add(create(MAVEN_REPO, DOCKER_INSPECTOR_REPOPATH, 'DOCKER_INSPECTOR'))
    }

    private static ArtifactoryProduct create(String repo, String repoPathToCheck, String propertyPrefix) {
        return new ArtifactoryProduct(repo, repoPathToCheck, propertyPrefix)
    }

}
