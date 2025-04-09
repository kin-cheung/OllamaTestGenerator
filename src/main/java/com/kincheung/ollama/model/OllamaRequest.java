package com.kincheung.ollama.model;

/**
 * Represents a request to the Ollama API.
 */
public class OllamaRequest {
    private String model;
    private String prompt;
    private boolean stream;
    private Options options;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public Options getOptions() {
        return options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    /**
     * Optional parameters for the Ollama request.
     */
    public static class Options {
        private float temperature;
        private int numPredict;

        public Options(float temperature, int numPredict) {
            this.temperature = temperature;
            this.numPredict = numPredict;
        }

        public float getTemperature() {
            return temperature;
        }

        public void setTemperature(float temperature) {
            this.temperature = temperature;
        }

        public int getNumPredict() {
            return numPredict;
        }

        public void setNumPredict(int numPredict) {
            this.numPredict = numPredict;
        }
    }
}
