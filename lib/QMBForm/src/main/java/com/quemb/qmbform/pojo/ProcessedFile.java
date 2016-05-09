package com.quemb.qmbform.pojo;

import java.io.File;
import java.io.Serializable;

public class ProcessedFile implements Serializable {
    private Integer id;
    private String path;
    private String name;

    private Object logicFile;

    private double currentPercent;
    private ProcessedFile.ProcessedStatus processedStatus = ProcessedStatus.READY;

    public ProcessedFile(String path) {
        this.name = new File(path).getName();
        this.path = path;
    }

    public ProcessedFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setLogicFile(Object logicFile) {
        this.logicFile = logicFile;
    }

    public Object getLogicFile() {
        return logicFile;
    }

    public ProcessedStatus getProcessedStatus() {
        return processedStatus;
    }

    public void setProcessedStatus(ProcessedStatus processedStatus) {
        this.processedStatus = processedStatus;
    }

    public double getCurrentPercent() {
        return currentPercent;
    }

    public void setCurrentPercent(double currentPercent) {
        this.currentPercent = currentPercent;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static enum ProcessedStatus {
        READY,
        UPLOADING,
        SUCCESS,
        FAIL;

        private ProcessedStatus() {
        }
    }
}
