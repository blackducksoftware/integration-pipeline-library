package com.blackduck.integration


public class ConfigUtils {
    private final Map config

    public ConfigUtils(Map config) {
        this.config = config
    }

    public <T> T get(String key, T defaultValue) {
        T result = defaultValue
        if (config.containsKey(key)) {
            result = config.get(key)
            if (null == result) {
                result = defaultValue
            }
        }
        return result
    }
}
