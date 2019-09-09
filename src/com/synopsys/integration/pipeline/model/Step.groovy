package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.exception.PipelineException

abstract class Step implements Serializable {
    abstract void run() throws PipelineException, Exception

}
