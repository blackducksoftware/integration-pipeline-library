package com.synopsys.integration.pipeline.tools

import com.cloudbees.groovy.cps.NonCPS
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

class ReadArtifactoryPropertiesStage extends Stage {
    public static final String PUBLIC_ARTIFACTORY = 'https://sig-repo.synopsys.com/artifactory'
    public static final String INTERNAL_ARTIFACTORY = 'https://artifactory.internal.synopsys.com/artifactory'

    ReadArtifactoryPropertiesStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @NonCPS
    private void doTheStuff() {
        for (ArtifactoryProduct artifactoryProduct : ArtifactoryProducts.artifactoryProducts) {
            String repoKey = artifactoryProduct.getRepoKey()
            String itemPath = artifactoryProduct.getItemPathToCheck()
            pipelineConfiguration.getLogger().info(String.format("Properties for: %s/%s", repoKey, itemPath))
        }
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
