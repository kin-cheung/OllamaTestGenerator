package com.kincheung.ollama;

/**
 * Interface defining Ollama settings methods.
 */
public interface OllamaSettings {
    String getOllamaUrl();
    void setOllamaUrl(String ollamaUrl);
    
    String getModelName();
    void setModelName(String modelName);
    
    boolean getIncludeMockito();
    void setIncludeMockito(boolean includeMockito);
    
    boolean getIncludeComments();
    void setIncludeComments(boolean includeComments);
    
    int getTimeout();
    void setTimeout(int timeoutSeconds);
}
