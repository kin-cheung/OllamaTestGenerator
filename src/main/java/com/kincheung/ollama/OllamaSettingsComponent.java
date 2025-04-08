package com.kincheung.ollama;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * UI component for the plugin settings.
 */
public class OllamaSettingsComponent implements OllamaSettings {
    private final JPanel myMainPanel;
    private final JBTextField ollamaUrlField = new JBTextField();
    private final JBTextField modelNameField = new JBTextField();
    private final JBCheckBox includeMockitoCheckbox = new JBCheckBox("Include Mockito for mocking dependencies");
    private final JBCheckBox includeCommentsCheckbox = new JBCheckBox("Include comments in generated tests");
    private final JSpinner timeoutSpinner = new JSpinner(new SpinnerNumberModel(60, 10, 300, 10));

    public OllamaSettingsComponent() {
        JPanel timeoutPanel = new JPanel();
        timeoutPanel.add(new JBLabel("Timeout (seconds):"));
        timeoutPanel.add(timeoutSpinner);

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Ollama URL:"), ollamaUrlField, 1, false)
                .addLabeledComponent(new JBLabel("Model name:"), modelNameField, 1, false)
                .addComponent(includeMockitoCheckbox, 1)
                .addComponent(includeCommentsCheckbox, 1)
                .addComponent(timeoutPanel, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    @Override
    public String getOllamaUrl() {
        return ollamaUrlField.getText();
    }

    @Override
    public void setOllamaUrl(String ollamaUrl) {
        ollamaUrlField.setText(ollamaUrl);
    }

    @Override
    public String getModelName() {
        return modelNameField.getText();
    }

    @Override
    public void setModelName(String modelName) {
        modelNameField.setText(modelName);
    }

    @Override
    public boolean getIncludeMockito() {
        return includeMockitoCheckbox.isSelected();
    }

    @Override
    public void setIncludeMockito(boolean includeMockito) {
        includeMockitoCheckbox.setSelected(includeMockito);
    }

    @Override
    public boolean getIncludeComments() {
        return includeCommentsCheckbox.isSelected();
    }

    @Override
    public void setIncludeComments(boolean includeComments) {
        includeCommentsCheckbox.setSelected(includeComments);
    }

    @Override
    public int getTimeout() {
        return (Integer) timeoutSpinner.getValue();
    }

    @Override
    public void setTimeout(int timeoutSeconds) {
        timeoutSpinner.setValue(timeoutSeconds);
    }
}
