package com.blackduck.integration.pipeline.results


import com.blackduck.integration.pipeline.jenkins.PipelineConfiguration

class ArchiveStage extends com.blackduck.integration.pipeline.model.Stage {
    public static final String DEFAULT_ARCHIVE_FILE_PATTERN = '**/*.jar'

    private String archiveFilePattern = DEFAULT_ARCHIVE_FILE_PATTERN

    ArchiveStage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }

    @Override
    void stageExecution() throws com.blackduck.integration.pipeline.exception.PipelineException, Exception {
        getPipelineConfiguration().getScriptWrapper().archiveArtifacts(archiveFilePattern)
    }

    String getArchiveFilePattern() {
        return archiveFilePattern
    }

    void setArchiveFilePattern(final String archiveFilePattern) {
        this.archiveFilePattern = archiveFilePattern
    }
}
