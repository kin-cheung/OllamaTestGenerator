package com.kincheung.ollama;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.kincheung.ollama.model.OllamaRequest;
import com.kincheung.ollama.model.OllamaResponse;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Service for interacting with the Ollama API.
 */
public class OllamaService {
    private static final Logger LOG = Logger.getInstance(OllamaService.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Gson gson = new Gson();
    private final OkHttpClient client;

    public OllamaService() {
        OllamaSettingsState settings = OllamaSettingsState.getInstance();
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(settings.timeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(settings.timeoutSeconds, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Generates a unit test for a Java class using Ollama.
     *
     * @param className The name of the class
     * @param classCode The source code of the class
     * @param usesMockito Whether to include Mockito in the generated test
     * @param includeComments Whether to include comments in the generated test
     * @param indicator Progress indicator for UI feedback
     * @return A CompletableFuture with the generated test code
     */
    public CompletableFuture<String> generateTest(
            String className,
            String classCode,
            boolean usesMockito,
            boolean includeComments,
            ProgressIndicator indicator) {
        
        OllamaSettingsState settings = OllamaSettingsState.getInstance();
        
        // Build the prompt for the model
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Generate a JUnit 5 unit test for the following Java class.\n\n");
        
        if (usesMockito) {
            promptBuilder.append("Use Mockito for mocking dependencies.\n");
        }
        
        if (includeComments) {
            promptBuilder.append("Include clear comments explaining the tests.\n");
        }
        
        promptBuilder.append("\nHere is the class to test:\n\n```java\n");
        promptBuilder.append(classCode);
        promptBuilder.append("\n```\n\n");
        promptBuilder.append("Generate a complete test class named ").append(className).append("Test with comprehensive test methods for each public method.");
        
        String prompt = promptBuilder.toString();
        
        // Create the request object
        OllamaRequest request = new OllamaRequest();
        request.setModel(settings.modelName);
        request.setPrompt(prompt);
        request.setStream(false);
        
        String requestJson = gson.toJson(request);
        
        // Create the HTTP request
        RequestBody body = RequestBody.create(requestJson, JSON);
        Request httpRequest = new Request.Builder()
                .url(settings.ollamaUrl + "/api/generate")
                .post(body)
                .build();
        
        // Create a CompletableFuture to return the result asynchronously
        CompletableFuture<String> future = new CompletableFuture<>();
        
        // Execute the request asynchronously
        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LOG.error("Error generating test with Ollama", e);
                ApplicationManager.getApplication().invokeLater(() -> 
                    future.completeExceptionally(e)
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful() || responseBody == null) {
                        String errorMsg = "Error from Ollama: " + response.code() + " - " + response.message();
                        LOG.error(errorMsg);
                        ApplicationManager.getApplication().invokeLater(() -> 
                            future.completeExceptionally(new IOException(errorMsg))
                        );
                        return;
                    }

                    String responseJson = responseBody.string();
                    OllamaResponse ollamaResponse = gson.fromJson(responseJson, OllamaResponse.class);
                    
                    // Extract the code from the response
                    String testCode = ollamaResponse.getResponse();
                    
                    // Clean up the response to extract just the Java code if it's wrapped in markdown
                    testCode = extractJavaCode(testCode);
                    
                    final String finalCode = testCode;
                    ApplicationManager.getApplication().invokeLater(() -> 
                        future.complete(finalCode)
                    );
                } catch (Exception e) {
                    LOG.error("Error processing Ollama response", e);
                    ApplicationManager.getApplication().invokeLater(() -> 
                        future.completeExceptionally(e)
                    );
                }
            }
        });
        
        return future;
    }
    
    /**
     * Extract Java code from the response, removing any markdown formatting.
     * 
     * @param text The response text that might contain markdown
     * @return The cleaned Java code
     */
    private String extractJavaCode(String text) {
        // If the response is wrapped in markdown code blocks, extract the Java code
        if (text.contains("```java")) {
            int start = text.indexOf("```java") + 7;
            int end = text.lastIndexOf("```");
            if (end > start) {
                return text.substring(start, end).trim();
            }
        }
        
        // If there's just a generic code block
        if (text.contains("```")) {
            int start = text.indexOf("```") + 3;
            int end = text.lastIndexOf("```");
            if (end > start) {
                return text.substring(start, end).trim();
            }
        }
        
        // Otherwise return the whole text
        return text;
    }
    
    /**
     * Check if Ollama is reachable at the configured URL.
     * 
     * @return A CompletableFuture that completes with true if Ollama is reachable
     */
    public CompletableFuture<Boolean> isOllamaAvailable() {
        OllamaSettingsState settings = OllamaSettingsState.getInstance();
        
        Request request = new Request.Builder()
                .url(settings.ollamaUrl + "/api/tags")
                .build();
        
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ApplicationManager.getApplication().invokeLater(() -> 
                    future.complete(false)
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                boolean isAvailable = response.isSuccessful();
                response.close();
                ApplicationManager.getApplication().invokeLater(() -> 
                    future.complete(isAvailable)
                );
            }
        });
        
        return future;
    }
}
