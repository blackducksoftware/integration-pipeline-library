package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.email.EmailPipelineWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger

class PipeLineFactory {
    def script
    private final PipelineLogger pipelineLogger

    public PipeLineFactory(Object script) {
        this.script = script
        this.pipelineLogger = new DefaultPipelineLoger(script.println)
    }

    public EmailPipelineWrapper createEmailPipelineWrapper(String recipientList) {
        return new EmailPipelineWrapper(recipientList, script.currentBuild, script.emailext, script.env.JOB_NAME, script.env.BUILD_NUMBER, script.env.BUILD_URL)
    }

}
