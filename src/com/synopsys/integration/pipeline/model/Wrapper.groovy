package com.synopsys.integration.pipeline.model

abstract class Wrapper implements Serializable {
    final String name

    Wrapper(String name) {
        this.name = name
    }

    abstract void start()

    Optional<String> startMessage() {
        return Optional.empty()
    }

    abstract void handleException(Exception e)

    Optional<String> exceptionMessage() {
        return Optional.empty()
    }

    abstract void end()

    Optional<String> endMessage() {
        return Optional.empty()
    }

    String getName() {
        return name
    }
}
