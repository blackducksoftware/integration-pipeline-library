#!/bin/bash
#
# Run this bash script in an empty directory to pull down the groovy script files for releasing libraries (the "Releaser script").
#
curl -O https://raw.githubusercontent.com/blackducksoftware/integration-pipeline-library/master/scripts/releaseLibs/ToolRunner.groovy
curl -O https://raw.githubusercontent.com/blackducksoftware/integration-pipeline-library/master/scripts/releaseLibs/Releaser.groovy

echo "To see usage information: groovy Releaser"
