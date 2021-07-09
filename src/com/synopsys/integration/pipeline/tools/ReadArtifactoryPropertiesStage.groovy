package com.synopsys.integration.pipeline.tools

import com.cloudbees.groovy.cps.NonCPS
import com.google.gson.Gson
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.rest.HttpUrl
import org.apache.commons.lang3.StringUtils

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

    @Override
    void stageExecution() throws Exception {
        List<ArtifactoryProduct> artifactoryProducts = new LinkedList<>()
        artifactoryProducts.add(new ArtifactoryProduct(NUGET_REPO, 'BlackduckNugetInspector', 'NUGET_INSPECTOR'))
        artifactoryProducts.add(new ArtifactoryProduct(NUGET_REPO, 'IntegrationNugetInspector', 'NUGET_INSPECTOR'))
        artifactoryProducts.add(new ArtifactoryProduct(NUGET_REPO, 'NugetDotnet3Inspector', 'NUGET_DOTNET3_INSPECTOR'))
        artifactoryProducts.add(new ArtifactoryProduct(NUGET_REPO, 'NugetDotnet5Inspector', 'NUGET_DOTNET5_INSPECTOR'))

        artifactoryProducts.add(new ArtifactoryProduct(MAVEN_REPO, DETECT_REPOPATH, 'DETECT'))
        artifactoryProducts.add(new ArtifactoryProduct(MAVEN_REPO, DETECT_REPOPATH, 'DETECT_FONT_BUNDLE'))

        artifactoryProducts.add(new ArtifactoryProduct(MAVEN_REPO, DOCKER_INSPECTOR_REPOPATH, 'DOCKER_INSPECTOR_AIR_GAP'))
        artifactoryProducts.add(new ArtifactoryProduct(MAVEN_REPO, DOCKER_INSPECTOR_REPOPATH, 'DOCKER_INSPECTOR'))

        def propertiesReportBuilder = new StringBuilder()
        for (ArtifactoryProduct artifactoryProduct : artifactoryProducts) {
            String repoKey = artifactoryProduct.getRepoKey()
            String itemPath = artifactoryProduct.getItemPathToCheck()
            propertiesReportBuilder.append(String.format("\nProperties for: %s/%s\n", repoKey, itemPath))

            HttpUrl propertiesUrl = new HttpUrl(String.format("%s/api/storage/%s/%s?properties", PUBLIC_ARTIFACTORY, repoKey, itemPath))
            URLConnection urlConnection = propertiesUrl.url().openConnection()
            urlConnection.connect()
            String content = urlConnection.getInputStream().text
            ArtifactoryPropertiesResponse propertiesResponse = new Gson().fromJson(content, ArtifactoryPropertiesResponse.class)
            Map<String, List<String>> properties = propertiesResponse.getProperties()
            for (String name : properties.keySet()) {
                if (name.startsWith(artifactoryProduct.getPropertyPrefix())) {
                    String values = StringUtils.join(properties.get(name))
                    String propertyOutput = String.format("\t%s: %s", name, values)
                    propertiesReportBuilder.append(propertyOutput)
                    propertiesReportBuilder.append('\n')
                }
            }
        }
        pipelineConfiguration.getLogger().info(propertiesReportBuilder)
    }

}
