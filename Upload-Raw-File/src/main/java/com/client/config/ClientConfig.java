package com.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "client")
public class ClientConfig {

    private String rawDataDir;
    private String processedDataDir;
    private String rawDataFileName;
    private String processedDataFileName;
    private String dateFormat;
    private String restURL;

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
    }

    public String getRawDataDir() {
        return rawDataDir;
    }

    public void setRawDataDir(String rawDataDir) {
        this.rawDataDir = rawDataDir;
    }

    public String getProcessedDataDir() {
        return processedDataDir;
    }

    public void setProcessedDataDir(String processedDataDir) {
        this.processedDataDir = processedDataDir;
    }

    public String getRawDataFileName() {
        return rawDataFileName;
    }

    public void setRawDataFileName(String rawDataFileName) {
        this.rawDataFileName = rawDataFileName;
    }

    public String getProcessedDataFileName() {
        return processedDataFileName;
    }

    public void setProcessedDataFileName(String processedDataFileName) {
        this.processedDataFileName = processedDataFileName;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

}
