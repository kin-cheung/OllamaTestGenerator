package com.kincheung.ollama;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Configurable component for plugin settings accessible from IDE Settings/Preferences.
 */
public class OllamaTestGeneratorConfigurable implements Configurable {
    private OllamaSettingsComponent mySettingsComponent;

    public OllamaTestGeneratorConfigurable() {
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Ollama Test Generator";
    }

    @Override
    public @Nullable JComponent createComponent() {
        mySettingsComponent = new OllamaSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        OllamaSettingsState settings = OllamaSettingsState.getInstance();
        return !mySettingsComponent.getOllamaUrl().equals(settings.ollamaUrl) ||
               !mySettingsComponent.getModelName().equals(settings.modelName) ||
               mySettingsComponent.getIncludeMockito() != settings.includeMockito ||
               mySettingsComponent.getIncludeComments() != settings.includeComments ||
               mySettingsComponent.getTimeout() != settings.timeoutSeconds;
    }

    @Override
    public void apply() {
        OllamaSettingsState settings = OllamaSettingsState.getInstance();
        settings.ollamaUrl = mySettingsComponent.getOllamaUrl();
        settings.modelName = mySettingsComponent.getModelName();
        settings.includeMockito = mySettingsComponent.getIncludeMockito();
        settings.includeComments = mySettingsComponent.getIncludeComments();
        settings.timeoutSeconds = mySettingsComponent.getTimeout();
    }

    @Override
    public void reset() {
        OllamaSettingsState settings = OllamaSettingsState.getInstance();
        mySettingsComponent.setOllamaUrl(settings.ollamaUrl);
        mySettingsComponent.setModelName(settings.modelName);
        mySettingsComponent.setIncludeMockito(settings.includeMockito);
        mySettingsComponent.setIncludeComments(settings.includeComments);
        mySettingsComponent.setTimeout(settings.timeoutSeconds);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }
}
