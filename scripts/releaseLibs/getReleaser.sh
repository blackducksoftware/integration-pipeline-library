#!/bin/bash
#
# Run this bash script in an empty directory to pull down the groovy script files for releasing libraries (the "Releaser script"). Then:
#
#	- To change the library list (you shouldn't need to), edit Libraries.groovy
#	- For instructions on how to run the script, run: groovy Releaser.groovy
#
# The Releaser script requires that groovy be on your path.
#
curl -O https://raw.githubusercontent.com/blackducksoftware/integration-pipeline-library/master/scripts/releaseLibs/ToolRunner.groovy
curl -O https://raw.githubusercontent.com/blackducksoftware/integration-pipeline-library/master/scripts/releaseLibs/Releaser.groovy

echo "To see usage information: groovy Releaser"
