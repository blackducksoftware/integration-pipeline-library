package com.synopsys.integration.pipeline


import com.synopsys.integration.pipeline.jenkins.*
import com.synopsys.integration.pipeline.logging.DefaultPipelineLogger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.logging.SilentPipelineLogger
import com.synopsys.integration.pipeline.model.PipelineWrapper
import com.synopsys.integration.pipeline.model.Stage
import com.synopsys.integration.pipeline.model.Step
import org.jenkinsci.plugins.workflow.cps.CpsScript

class Pipeline implements Serializable {
    public final CpsScript script
    public final PipelineConfiguration pipelineConfiguration

    public final List<PipelineWrapper> wrappers = new LinkedList<>()
    public final List<Step> steps = new LinkedList<>()

    Pipeline(CpsScript script) {
        this.script = script
        JenkinsScriptWrapper scriptWrapper = new JenkinsScriptWrapperImpl(script)
        this.pipelineConfiguration = new PipelineConfiguration(new DefaultPipelineLogger(scriptWrapper), scriptWrapper)
    }

    void addStage(Stage stage) {
        getPipelineConfiguration().getLogger().debug("Adding stage ${stage.getName()}")
        steps.add(stage)
    }

    void addStep(Step step) {
        getPipelineConfiguration().getLogger().debug("Adding step")
        steps.add(step)
    }

    void addPipelineWrapper(PipelineWrapper wrapper) {
        getPipelineConfiguration().getLogger().debug("Adding pipeline wrapper ${wrapper.getName()}")
        wrappers.add(wrapper)
    }

    void addProperties(List pipelineProperties) {
        getPipelineConfiguration().getScriptWrapper().pipelineProperties(pipelineProperties)
    }

    void run() {
        JenkinsScriptWrapper originalScriptWrapper = getPipelineConfiguration().getScriptWrapper()
        PipelineLogger originalLogger = getPipelineConfiguration().getLogger()

        DryRunPipelineBuilder dryRunPipelineBuilder = new DryRunPipelineBuilder(getPipelineConfiguration())
        dryRunPipelineBuilder.initialize()
        JenkinsScriptWrapper dryRunWrapper = new JenkinsScriptWrapperDryRun(this.script, dryRunPipelineBuilder)
        getPipelineConfiguration().setScriptWrapper(dryRunWrapper)
        SilentPipelineLogger silentLogger = new SilentPipelineLogger()
        getPipelineConfiguration().setLogger(silentLogger)

        originalLogger.info("Starting dry run")
        runWithJenkinsWrapper()
        originalLogger.info("End dry run")
        originalLogger.alwaysLog(dryRunWrapper.getDryRunPipelineBuilder().getPipelineString())

        getPipelineConfiguration().setScriptWrapper(originalScriptWrapper)
        getPipelineConfiguration().setLogger(originalLogger)
        getPipelineConfiguration().getLogger().info("Starting run")
        runWithJenkinsWrapper()
    }

    void runWithJenkinsWrapper() {
        JenkinsScriptWrapper scriptWrapper = getPipelineConfiguration().getScriptWrapper()
        PipelineLogger logger = getPipelineConfiguration().getLogger()

        for (int i = 0; i < getWrappers().size(); i++) {
            PipelineWrapper wrapper = getWrappers().get(i)
            scriptWrapper.dir(wrapper.getRelativeDirectory()) {
                wrapper.startMessage()
                        .ifPresent { message -> logger.info(message)
                        }
                wrapper.start()
            }
        }
        try {
            for (int i = 0; i < getSteps().size(); i++) {
                Step currentStep = getSteps().get(i)
                scriptWrapper.dir(currentStep.getRelativeDirectory()) {
                    if (currentStep instanceof Stage) {
                        Stage currentStage = (Stage) currentStep
                        scriptWrapper.stage(currentStage.getName()) {
                            logger.info("running stage ${currentStage.getName()}")
                            currentStage.run()
                        }
                    } else {
                        try {
                            currentStep.run()
                        } catch (Exception e) {
                            logger.error(e)
                        }
                    }
                }
            }
        } catch (Exception e) {
            scriptWrapper.currentBuild().result = "FAILURE"
            logger.error("Build failed because ${e.getMessage()}", e)
            for (int i = 0; i < getWrappers().size(); i++) {
                PipelineWrapper wrapper = getWrappers().get(i)
                scriptWrapper.dir(wrapper.getRelativeDirectory()) {
                    wrapper.exceptionMessage()
                            .ifPresent { message -> logger.error(message)
                            }
                    wrapper.handleException(e)
                }
            }
        } finally {
            for (int i = 0; i < getWrappers().size(); i++) {
                PipelineWrapper wrapper = getWrappers().get(i)
                scriptWrapper.dir(wrapper.getRelativeDirectory()) {
                    wrapper.endMessage()
                            .ifPresent { message -> logger.info(message)
                            }
                    wrapper.end()
                }
            }
        }
    }

    public PipelineConfiguration getPipelineConfiguration() {
        return pipelineConfiguration
    }

    public List<PipelineWrapper> getWrappers() {
        return wrappers
    }

    public List<Step> getSteps() {
        return steps
    }
}
