package com.synopsys.integration.pipeline.model

abstract class Wrapper implements Serializable {
    private final String name

    public Wrapper(String name) {
        this.name = name
    }

    public abstract void start()

    public Optional<String> startMessage() {
        return Optional.empty()
    }

    public abstract void handleException(Exception e)

    public Optional<String> exceptionMessage() {
        return Optional.empty()
    }

    public abstract void end()

    public Optional<String> endMessage() {
        return Optional.empty()
    }

    public String getName() {
        return name
    }
}
