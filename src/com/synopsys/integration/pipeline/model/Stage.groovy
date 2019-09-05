package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.jenkins.ScriptWrapper
import com.synopsys.integration.pipeline.logging.PipelineLogger

abstract class Stage implements Serializable {
    private final String name

    public final List<StageWrapper> wrappers = new LinkedList<>()

    public Stage(String name, ScriptWrapper scriptWrapper) {
        this.name = name
        scriptWrapper.println("name is ${name}")
    }

    public void addStageWrapper(StageWrapper wrapper) {
        wrappers.add(wrapper)
    }

    public void run() {
        wrappers.each { wrapper -> wrapper.start() }
        try {
            stageExecution()
        } catch (Exception e) {
            wrappers.each { wrapper -> wrapper.handleException(e) }
        } finally {
            wrappers.each { wrapper -> wrapper.end() }
        }
    }

    abstract void stageExecution()

    public String getName(PipelineLogger pipelineLogger) {
        pipelineLogger.info("class ${this.getClass()}")
        pipelineLogger.info("name ${this.name}")
        pipelineLogger.info("name ${name}")
        return name
    }

}
