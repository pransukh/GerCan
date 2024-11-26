package com.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "ftp")

public class FTPConfig {

    private String host;
    private int port;
    private String username;
    private String opener;

    private String rawDataDir;
    private String processedDataDir;
    private String rawDataFileName;
    private String processedDataFileName;
    private String dateFormat;

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOpener() {
        return opener;
    }

    public void setOpener(String opener) {
        this.opener = opener;
    }
}
