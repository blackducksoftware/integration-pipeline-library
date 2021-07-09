package com.synopsys.integration.pipeline.tools

import com.cloudbees.groovy.cps.NonCPS
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class ReadArtifactoryPropertiesStage extends Stage {
    public static final String PUBLIC_ARTIFACTORY = 'https://sig-repo.synopsys.com/artifactory'
    public static final String INTERNAL_ARTIFACTORY = 'https://artifactory.internal.synopsys.com/artifactory'

    public static final String NUGET_REPO = 'bds-integrations-nuget-release'
    public static final String MAVEN_REPO = 'bds-integrations-release'

    public static final String DETECT_REPOPATH = 'com/synopsys/integration/synopsys-detect'
    public static final String DOCKER_INSPECTOR_REPOPATH = 'com/synopsys/integration/blackduck-docker-inspector'

    ReadArtifactoryPropertiesStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    private void doTheStuff() {
        List<ArtifactoryProduct> artifactoryProducts = new LinkedList<>()
        artifactoryProducts.add(create(NUGET_REPO, 'BlackduckNugetInspector', 'NUGET_INSPECTOR'))
        artifactoryProducts.add(create(NUGET_REPO, 'IntegrationNugetInspector', 'NUGET_INSPECTOR'))
        artifactoryProducts.add(create(NUGET_REPO, 'NugetDotnet3Inspector', 'NUGET_DOTNET3_INSPECTOR'))
        artifactoryProducts.add(create(NUGET_REPO, 'NugetDotnet5Inspector', 'NUGET_DOTNET5_INSPECTOR'))

        artifactoryProducts.add(create(MAVEN_REPO, DETECT_REPOPATH, 'DETECT'))
        artifactoryProducts.add(create(MAVEN_REPO, DETECT_REPOPATH, 'DETECT_FONT_BUNDLE'))

        artifactoryProducts.add(create(MAVEN_REPO, DOCKER_INSPECTOR_REPOPATH, 'DOCKER_INSPECTOR_AIR_GAP'))
        artifactoryProducts.add(create(MAVEN_REPO, DOCKER_INSPECTOR_REPOPATH, 'DOCKER_INSPECTOR'))
        for (ArtifactoryProduct artifactoryProduct : artifactoryProducts) {
            String repoKey = artifactoryProduct.getRepoKey()
            String itemPath = artifactoryProduct.getItemPathToCheck()
            pipelineConfiguration.getLogger().info(String.format("Properties for: %s/%s", repoKey, itemPath))
        }
    }

    private ArtifactoryProduct create(String repo, String repoPathToCheck, String propertyPrefix) {
        return new ArtifactoryProduct(repo, repoPathToCheck, propertyPrefix)
    }

    @Override
    void stageExecution() throws Exception {
        doTheStuff()
        //            HttpUrl propertiesUrl = new HttpUrl(String.format("%s/api/storage/%s/%s", PUBLIC_ARTIFACTORY, repoKey, itemPath))
        //            URLConnection urlConnection = propertiesUrl.url().openConnection()
        //            urlConnection.connect()
        //            urlConnection.getInputStream().withCloseable { inputStream ->
        //                String content = inputStream.text
        //                ArtifactoryPropertiesResponse propertiesResponse = gson.fromJson(content, ArtifactoryPropertiesResponse.class)
        //                Map<String, List<String>> properties = propertiesResponse.getProperties()
        //                pipelineConfiguration.getLogger().info(String.format("Properties for: %s/%s", repoKey, itemPath))
        //                for (String name : properties.keySet()) {
        //                    if (name.startsWith(artifactoryProduct.getPropertyPrefix())) {
        //                        String values = StringUtils.join(properties.get(name))
        //                        String propertyOutput = String.format("%s: %s", name, values)
        //                        pipelineConfiguration.getLogger().info(propertyOutput)
        //                    }
        //                }
        //            }
    }

}
