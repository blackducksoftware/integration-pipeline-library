package com.synopsys.integration.pipeline


import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.PipelineWrapper
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.model.Step
import org.jenkinsci.plugins.workflow.cps.CpsScript

class Pipeline implements Serializable {
    public final JenkinsScriptWrapper scriptWrapper
    public final PipelineLogger pipelineLogger

    public final List<PipelineWrapper> wrappers = new LinkedList<>()
    public final List<Step> steps = new LinkedList<>()

    Pipeline(CpsScript script) {
        this.scriptWrapper = new JenkinsScriptWrapper(script)
        pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
    }

    void addStep(Step step) {
        getPipelineLogger().info("Adding step")
        steps.add(step)
    }

    void addStage(Stage stage) {
        getPipelineLogger().info("Adding stage ${stage.getName()}")
        steps.add(stage)
    }

    void addPipelineWrapper(PipelineWrapper wrapper) {
        getPipelineLogger().info("Adding wrapper ${wrapper.getName()}")
        wrappers.add(wrapper)
    }

    void run() {
        getPipelineLogger().info("Starting run")
        getWrappers().each { wrapper ->
            getScriptWrapper().dir(wrapper.getRelativeDirectory()) {
                wrapper.start()
            }
        }
        try {
            getSteps().each { currentStep ->
                getScriptWrapper().dir(currentStep.getRelativeDirectory()) {
                    if (currentStep instanceof Stage) {
                        Stage currentStage = (Stage) currentStep
                        getScriptWrapper().stage(currentStage.getName()) {
                            getPipelineLogger().info("running stage ${currentStage.getName()}")
                            currentStage.run()
                        }
                    } else {
                        currentStep.run()
                    }
                }
            }
        } catch (Exception e) {
            getScriptWrapper().currentBuild().result = "FAILURE"
            getWrappers().each { wrapper ->
                getScriptWrapper().dir(wrapper.getRelativeDirectory()) {
                    wrapper.handleException(e)
                }
            }
            getPipelineLogger().error("Build failed because ${e.getMessage()}", e)
        } finally {
            getWrappers().each { wrapper ->
                getScriptWrapper().dir(wrapper.getRelativeDirectory()) {
                    wrapper.end()
                }
            }
        }
    }

    public JenkinsScriptWrapper getScriptWrapper() {
        return scriptWrapper
    }

    public PipelineLogger getPipelineLogger() {
        return pipelineLogger
    }

    public List<PipelineWrapper> getWrappers() {
        return wrappers
    }

    public List<Step> getSteps() {
        return steps
    }
}
