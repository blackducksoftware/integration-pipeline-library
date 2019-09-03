package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.model.PipelineWrapper
import com.synopsys.integration.pipeline.model.Stage

class Pipeline {
    private final Object script

    private final List<PipelineWrapper> wrappers = new LinkedList<>()
    private final List<Stage> stages = new LinkedList<>()

    public Pipeline(Object script) {
        this.script = script
    }

    public void addStage(Stage stage) {
        stages.add(stage)
    }

    public void addPipelineWrapper(PipelineWrapper wrapper) {
        wrappers.add(wrapper)
    }

    public void run() {
        wrappers.each { wrapper -> wrapper.start() }
        try {
            stages.each { stage ->
                script.stage(stage.getName()) {
                    stage.run()
                }
            }
        } catch (Exception e) {
            wrappers.each { wrapper -> wrapper.handleException(e) }
        } finally {
            wrappers.each { wrapper -> wrapper.end() }
        }
    }


}
