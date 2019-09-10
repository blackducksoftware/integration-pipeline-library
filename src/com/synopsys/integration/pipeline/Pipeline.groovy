package com.synopsys.integration.pipeline


import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.PipelineWrapper
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.model.Step
import org.jenkinsci.plugins.workflow.cps.CpsScript

class Pipeline implements Serializable {
    final JenkinsScriptWrapper scriptWrapper
    final PipelineLogger pipelineLogger

    final List<PipelineWrapper> wrappers = new LinkedList<>()
    final List<Step> steps = new LinkedList<>()

    Pipeline(CpsScript script) {
        this.scriptWrapper = new JenkinsScriptWrapper(script)
        pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
    }

    void addStep(Step step) {
        pipelineLogger.info("Adding step")
        steps.add(step)
    }

    void addStage(Stage stage) {
        pipelineLogger.info("Adding stage ${stage.getName()}")
        steps.add(stage)
    }

    void addPipelineWrapper(PipelineWrapper wrapper) {
        pipelineLogger.info("Adding wrapper ${wrapper.getName()}")
        wrappers.add(wrapper)
    }

    void run() {
        pipelineLogger.info("Starting run")
        wrappers.each { wrapper -> wrapper.start() }
        try {
            steps.each { currentStep ->
                if (currentStep instanceof Stage) {
                    Stage currentStage = (Stage) currentStep
                    scriptWrapper.dir(currentStage.getRelativeDirectory()) {
                        scriptWrapper.stage(currentStage.getName()) {
                            pipelineLogger.info("running stage ${currentStage.getName()}")
                            currentStage.run()
                        }
                    }
                } else {
                    currentStep.run()
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

    JenkinsScriptWrapper getScriptWrapper() {
        return scriptWrapper
    }

    PipelineLogger getPipelineLogger() {
        return pipelineLogger
    }
}
