package com.synopsys.integration.pipeline


import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapperDryRun
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapperImpl
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.PipelineWrapper
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.model.Step
import org.jenkinsci.plugins.workflow.cps.CpsScript

class Pipeline implements Serializable {
    public final CpsScript script
    public final PipelineLogger pipelineLogger

    public final List<PipelineWrapper> wrappers = new LinkedList<>()
    public final List<Step> steps = new LinkedList<>()

    Pipeline(CpsScript script) {
        this.script = script
        pipelineLogger = new DefaultPipelineLoger(new JenkinsScriptWrapperImpl(script))
    }

    void addStage(Stage stage) {
        getPipelineLogger().info("Adding stage ${stage.getName()}")
        steps.add(stage)
    }

    void addStep(Step step) {
        getPipelineLogger().info("Adding step")
        steps.add(step)
    }

    void addPipelineWrapper(PipelineWrapper wrapper) {
        getPipelineLogger().info("Adding wrapper ${wrapper.getName()}")
        wrappers.add(wrapper)
    }

    void addProperties(ArrayList pipelineProperties) {
        scriptWrapper.properties(pipelineProperties)
    }

    void run() {
        JenkinsScriptWrapper dryRunWrapper = new JenkinsScriptWrapperDryRun(this.script, pipelineLogger)
        runWithJenkinsWrapper(dryRunWrapper)

        JenkinsScriptWrapper scriptWrapper = new JenkinsScriptWrapperImpl(this.script)
        runWithJenkinsWrapper(scriptWrapper)
    }

    void runWithJenkinsWrapper(JenkinsScriptWrapper currentJenkinsScriptWrapper) {
        getPipelineLogger().info("Starting run")
        getWrappers().each { wrapper ->
            currentJenkinsScriptWrapper.dir(wrapper.getRelativeDirectory()) {
                wrapper.start()
            }
        }
        try {
            getSteps().each { currentStep ->
                currentJenkinsScriptWrapper.dir(currentStep.getRelativeDirectory()) {
                    if (currentStep instanceof Stage) {
                        Stage currentStage = (Stage) currentStep
                        currentJenkinsScriptWrapper.stage(currentStage.getName()) {
                            getPipelineLogger().info("running stage ${currentStage.getName()}")
                            currentStage.run()
                        }
                    } else {
                        currentStep.run()
                    }
                }
            }
        } catch (Exception e) {
            currentJenkinsScriptWrapper.currentBuild().result = "FAILURE"
            getWrappers().each { wrapper ->
                currentJenkinsScriptWrapper.dir(wrapper.getRelativeDirectory()) {
                    wrapper.handleException(e)
                }
            }
            getPipelineLogger().error("Build failed because ${e.getMessage()}", e)
        } finally {
            getWrappers().each { wrapper ->
                currentJenkinsScriptWrapper.dir(wrapper.getRelativeDirectory()) {
                    wrapper.end()
                }
            }
        }
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
