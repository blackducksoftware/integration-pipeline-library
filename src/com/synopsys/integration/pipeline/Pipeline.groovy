package com.synopsys.integration.pipeline


import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.PipelineWrapper
import com.synopsys.integration.pipeline.model.Stage
import org.jenkinsci.plugins.workflow.cps.CpsScript

class Pipeline implements Serializable {
    final JenkinsScriptWrapper scriptWrapper
    final PipelineLogger pipelineLogger

    final List<PipelineWrapper> wrappers = new LinkedList<>()
    final List<Stage> stages = new LinkedList<>()

    Pipeline(CpsScript script) {
        this.scriptWrapper = new JenkinsScriptWrapper(script)
        pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
    }

    void addStage(Stage stage) {
        pipelineLogger.info("Adding stage")
        stages.add(stage)
    }

    void addPipelineWrapper(PipelineWrapper wrapper) {
        pipelineLogger.info("Adding wrapper")
        wrappers.add(wrapper)
    }

    void run() {
        pipelineLogger.info("Starting run")
        wrappers.each { wrapper -> wrapper.start() }
        try {
            stages.each { currentStage ->
                scriptWrapper.dir(currentStage.getRelativeDirectory()) {
                    scriptWrapper.stage(currentStage.getName()) {
                        pipelineLogger.info("running stage ${currentStage.getName()}")
                        currentStage.run()
                    }
                }
            }
        } catch (Exception e) {
            scriptWrapper.currentBuild().result = "FAILURE"
            wrappers.each { wrapper -> wrapper.handleException(e) }
            pipelineLogger.error("Build failed because ${e.getMessage()}", e)
        } finally {
            wrappers.each { wrapper -> wrapper.end() }
        }
    }

}
