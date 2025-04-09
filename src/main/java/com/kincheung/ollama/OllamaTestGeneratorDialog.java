package com.kincheung.ollama;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.kincheung.ollama.util.JavaClassAnalyzer;
import com.kincheung.ollama.util.TestFileCreator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

/**
 * Dialog for configuring test generation options.
 */
public class OllamaTestGeneratorDialog extends DialogWrapper {
    private final Project project;
    private final PsiClass targetClass;
    private final JBTextField testClassNameField = new JBTextField();
    private final JBCheckBox mockitoDependenciesCheckBox = new JBCheckBox("Use Mockito for mocking dependencies");
    private final JBCheckBox includeCommentsCheckBox = new JBCheckBox("Include detailed comments");
    private final JBLabel statusLabel = new JBLabel("");

    public OllamaTestGeneratorDialog(@Nullable Project project, PsiClass targetClass) {
        super(project);
        this.project = project;
        this.targetClass = targetClass;
        
        setTitle("Generate Unit Tests with Ollama");
        init();
        
        // Initialize fields with default values
        testClassNameField.setText(targetClass.getName() + "Test");
        
        OllamaSettingsState settings = OllamaSettingsState.getInstance();
        mockitoDependenciesCheckBox.setSelected(settings.includeMockito);
        includeCommentsCheckBox.setSelected(settings.includeComments);
        
        // Check if Ollama is available
        OllamaService ollamaService = new OllamaService();
        ollamaService.isOllamaAvailable().thenAccept(available -> {
            if (!available) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    statusLabel.setText("⚠️ Ollama is not available at " + settings.ollamaUrl);
                    statusLabel.setForeground(Color.RED);
                });
            } else {
                ApplicationManager.getApplication().invokeLater(() -> {
                    statusLabel.setText("✅ Ollama is available");
                    statusLabel.setForeground(new Color(0, 128, 0)); // Dark green
                });
            }
        });
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Test class name:"), testClassNameField, 1, false)
                .addComponent(mockitoDependenciesCheckBox, 1)
                .addComponent(includeCommentsCheckBox, 1)
                .addComponent(statusLabel, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    /**
     * Starts the test generation process with a progress indicator.
     */
    public void generateTests() {
        String testClassName = testClassNameField.getText();
        boolean useMockito = mockitoDependenciesCheckBox.isSelected();
        boolean includeComments = includeCommentsCheckBox.isSelected();
        
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "Generating Unit Tests", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("Analyzing class...");
                
                String className = targetClass.getName();
                String classCode = targetClass.getText();
                
                indicator.setText("Generating tests with Ollama...");
                
                OllamaService ollamaService = new OllamaService();
                CompletableFuture<String> future = ollamaService.generateTest(
                        className,
                        classCode,
                        useMockito,
                        includeComments,
                        indicator
                );
                
                try {
                    String testCode = future.get();
                    indicator.setText("Creating test file...");
                    
                    ApplicationManager.getApplication().invokeLater(() -> {
                        VirtualFile testFile = TestFileCreator.createTestFile(
                                project, 
                                targetClass, 
                                testClassName, 
                                testCode
                        );
                        
                        if (testFile != null) {
                            TestFileCreator.openFileInEditor(project, testFile);
                        } else {
                            Messages.showErrorDialog(
                                    project,
                                    "Could not create test file",
                                    "Test Generation Failed"
                            );
                        }
                    });
                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showErrorDialog(
                                project,
                                "Error generating tests: " + e.getMessage(),
                                "Test Generation Failed"
                        );
                    });
                }
            }
        });
    }
}
