package com.kincheung.ollama.model;

/**
 * Represents a response from the Ollama API.
 */
public class OllamaResponse {
    private String model;
    private String response;
    private long createdAt;
    private boolean done;
    private long totalDuration;
    private long loadDuration;
    private long promptEvalCount;
    private long promptEvalDuration;
    private long evalCount;
    private long evalDuration;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public long getLoadDuration() {
        return loadDuration;
    }

    public void setLoadDuration(long loadDuration) {
        this.loadDuration = loadDuration;
    }

    public long getPromptEvalCount() {
        return promptEvalCount;
    }

    public void setPromptEvalCount(long promptEvalCount) {
        this.promptEvalCount = promptEvalCount;
    }

    public long getPromptEvalDuration() {
        return promptEvalDuration;
    }

    public void setPromptEvalDuration(long promptEvalDuration) {
        this.promptEvalDuration = promptEvalDuration;
    }

    public long getEvalCount() {
        return evalCount;
    }

    public void setEvalCount(long evalCount) {
        this.evalCount = evalCount;
    }

    public long getEvalDuration() {
        return evalDuration;
    }

    public void setEvalDuration(long evalDuration) {
        this.evalDuration = evalDuration;
    }
}
