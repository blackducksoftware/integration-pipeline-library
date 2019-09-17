package com.synopsys.integration.pipeline.results

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration
import com.synopsys.integration.pipeline.model.Stage

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
