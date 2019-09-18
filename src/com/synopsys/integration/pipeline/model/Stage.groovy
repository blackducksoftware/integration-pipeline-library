package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

abstract class Stage extends Step {
    // Fields here must be public or they can't be accessed (in Jenkins at runtime) in sub classes
    public final String name

    final List<StageWrapper> wrappers = new LinkedList<>()

    Stage(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration)
        this.name = name
    }

    void addStageWrapper(StageWrapper wrapper) {
        getPipelineConfiguration().getLogger().debug("Adding stage wrapper ${wrapper.getName()}")
        wrappers.add(wrapper)
    }

    @Override
    void run() throws PipelineException, Exception {
        for (int i = 0; i < getWrappers().size(); i++) {
            StageWrapper wrapper = getWrappers().get(i)
            wrapper.start()
        }
        try {
            stageExecution()
        } catch (Exception e) {
            for (int i = 0; i < getWrappers().size(); i++) {
                StageWrapper wrapper = getWrappers().get(i)
                wrapper.handleException(e)
            }
            throw e
        } finally {
            for (int i = 0; i < getWrappers().size(); i++) {
                StageWrapper wrapper = getWrappers().get(i)
                wrapper.end()
            }
        }
    }

    // Stage that takes in sh commands and asserts that it contained certain output
    abstract void stageExecution() throws PipelineException, Exception

    String getName() {
        return name
    }
}
