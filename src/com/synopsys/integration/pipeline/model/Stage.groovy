package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

import java.util.function.Function

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

    protected <T extends Object> T retrieveFromEnv(String key, Function<String, T> converter, T defaultValue) {
        def value = pipelineConfiguration.scriptWrapper.getJenkinsProperty(key)

        if (value?.trim()) {
            getPipelineConfiguration().getLogger().info("${key} was found in environment with a value of ${value}")
            return converter.apply(value)
        } else {
            getPipelineConfiguration().getLogger().info("${key} was NOT found in environment using default value of ${defaultValue}")
        }
    }

    protected String retrieveStringFromEnv(String key, String defaultValue) {
        return retrieveFromEnv(key, Function.&identity as Function, defaultValue)
    }

    protected String retrieveDefaultStringFromEnv(String key) {
        return retrieveFromEnv(key, Function.&identity as Function, '')
    }

}
