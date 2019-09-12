package com.synopsys.integration.pipeline.results

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.model.Stage

class ArchiveStage extends Stage {
    public static final String DEFAULT_ARCHIVE_FILE_PATTERN = '**/*.jar'

    private String archiveFilePattern = DEFAULT_ARCHIVE_FILE_PATTERN
    private JenkinsScriptWrapper scriptWrapper


    ArchiveStage(JenkinsScriptWrapper scriptWrapper, String name) {
        super(name)
        this.scriptWrapper = scriptWrapper
    }

    @Override
    void stageExecution() throws PipelineException, Exception {
        scriptWrapper.archiveArtifacts(archiveFilePattern)
    }

    String getArchiveFilePattern() {
        return archiveFilePattern
    }

    void setArchiveFilePattern(final String archiveFilePattern) {
        this.archiveFilePattern = archiveFilePattern
    }
}
