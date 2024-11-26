package com.amdox.process;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mdox")
public class Config {
    private String processedFileStatusPath;

    public String getProcessedFileStatusPath() {
        return processedFileStatusPath;
    }

    public void setProcessedFileStatusPath(String processedFileStatusPath) {
        this.processedFileStatusPath = processedFileStatusPath;
    }
}
