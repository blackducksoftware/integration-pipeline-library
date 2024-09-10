package com.blackduck.integration.pipeline.results

import com.blackduck.integration.pipeline.exception.PipelineException
import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration
import com.blackduck.integration.pipeline.model.Stage

class ArchiveStage extends Stage {
    public static final String DEFAULT_ARCHIVE_FILE_PATTERN = '**/*.jar'

    private String archiveFilePattern = DEFAULT_ARCHIVE_FILE_PATTERN

    ArchiveStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().archiveArtifacts(archiveFilePattern)
    }

    String getArchiveFilePattern() {
        return archiveFilePattern
    }

    void setArchiveFilePattern(final String archiveFilePattern) {
        this.archiveFilePattern = archiveFilePattern
    }
}
