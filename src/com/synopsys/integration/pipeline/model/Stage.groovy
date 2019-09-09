package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.exception.PipelineException

abstract class Stage extends Step {
    // Fields here must be public or they can't be accessed (in Jenkins at runtime) in sub classes
    public final String name
    public String relativeDirectory = '.'

    final List<StageWrapper> wrappers = new LinkedList<>()

    Stage(String name) { this.name = name }

    void addStageWrapper(StageWrapper wrapper) {
        wrappers.add(wrapper)
    }

    @Override
    void run() throws PipelineException, Exception {
        wrappers.each { wrapper -> wrapper.start() }
        try {
            stageExecution()
        } catch (Exception e) {
            wrappers.each { wrapper -> wrapper.handleException(e) }
            throw e
        } finally {
            wrappers.each { wrapper -> wrapper.end() }
        }
    }

    // Stage that takes in sh commands and asserts that it contained certain output
    abstract void stageExecution() throws PipelineException, Exception

    String getName() {
        return name
    }

    String getRelativeDirectory() {
        return relativeDirectory
    }

    void setRelativeDirectory(final String relativeDirectory) {
        this.relativeDirectory = relativeDirectory
    }
}
