package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.jenkins.DryRunPipelineBuilder
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapper
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapperDryRun
import com.synopsys.integration.pipeline.jenkins.JenkinsScriptWrapperImpl
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.PipelineWrapper
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.model.StageWrapper
import com.synopsys.integration.pipeline.model.Step
import org.jenkinsci.plugins.workflow.cps.CpsScript

class Pipeline implements Serializable {
    public final CpsScript script
    public final JenkinsScriptWrapper scriptWrapper
    public final PipelineLogger pipelineLogger

    public final List<PipelineWrapper> wrappers = new LinkedList<>()
    public final List<Step> steps = new LinkedList<>()
    public final List pipelineProperties = new LinkedList()

    Pipeline(CpsScript script) {
        this.script = script
        this.scriptWrapper = new JenkinsScriptWrapperImpl(script)
        pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
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

    void addProperties(List pipelineProperties) {
        pipelineProperties.addAll(pipelineProperties)
    }

    void run() {
        DryRunPipelineBuilder dryRunPipelineBuilder = new DryRunPipelineBuilder(getPipelineLogger())
        dryRunPipelineBuilder.initialize()
        JenkinsScriptWrapper dryRunWrapper = new JenkinsScriptWrapperDryRun(this.script, dryRunPipelineBuilder)
        getPipelineLogger().info("Starting dry run")
        runWithJenkinsWrapper(dryRunWrapper)
        getPipelineLogger().info("End dry run")

        getPipelineLogger().alwaysLog(dryRunWrapper.getDryRunPipelineBuilder().getPipelineString())


        getScriptWrapper().pipelineProperties(pipelineProperties)
        getPipelineLogger().info("Starting run")
        runWithJenkinsWrapper(getScriptWrapper())
    }

    void runWithJenkinsWrapper(JenkinsScriptWrapper currentJenkinsScriptWrapper) {
        for (PipelineWrapper wrapper : getWrappers()) {
            wrapper.setScriptWrapper(currentJenkinsScriptWrapper)
        }
        for (Step currentStep : getSteps()) {
            currentStep.setScriptWrapper(currentJenkinsScriptWrapper)
            if (currentStep instanceof Stage) {
                Stage currentStage = (Stage) currentStep
                for (StageWrapper stageWrapper : currentStage.getWrappers()) {
                    stageWrapper.setScriptWrapper(currentJenkinsScriptWrapper)
                }
            }
        }


        for (PipelineWrapper wrapper : getWrappers()) {
            currentJenkinsScriptWrapper.dir(wrapper.getRelativeDirectory()) {
                wrapper.start()
            }
        }
        try {
            for (Step currentStep : getSteps()) {
                currentJenkinsScriptWrapper.dir(currentStep.getRelativeDirectory()) {
                    if (currentStep instanceof Stage) {
                        Stage currentStage = (Stage) currentStep
                        currentJenkinsScriptWrapper.stage(currentStage.getName()) {
                            getPipelineLogger().info("running stage ${currentStage.getName()}")
                            currentStage.run()
                        }
                    } else {
                        try {
                            currentStep.run()
                        } catch (Exception e) {
                            getPipelineLogger().error(e)
                        }
                    }
                }
            }
        } catch (Exception e) {
            currentJenkinsScriptWrapper.currentBuild().result = "FAILURE"
            for (PipelineWrapper wrapper : getWrappers()) {
                currentJenkinsScriptWrapper.dir(wrapper.getRelativeDirectory()) {
                    wrapper.handleException(e)
                }
            }
            getPipelineLogger().error("Build failed because ${e.getMessage()}", e)
        } finally {
            for (PipelineWrapper wrapper : getWrappers()) {
                currentJenkinsScriptWrapper.dir(wrapper.getRelativeDirectory()) {
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
