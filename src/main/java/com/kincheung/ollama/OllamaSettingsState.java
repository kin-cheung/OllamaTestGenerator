package com.kincheung.ollama;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Persistent state component that stores the plugin settings.
 */
@State(
    name = "com.kincheung.ollama.OllamaSettingsState",
    storages = @Storage("OllamaTestGeneratorSettings.xml")
)
public class OllamaSettingsState implements PersistentStateComponent<OllamaSettingsState> {
    public String ollamaUrl = "http://localhost:11434";
    public String modelName = "qwen2.5-coder:7b";
    public boolean includeMockito = true;
    public boolean includeComments = true;
    public int timeoutSeconds = 60;

    public static OllamaSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(OllamaSettingsState.class);
    }

    @Override
    public @Nullable OllamaSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull OllamaSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
