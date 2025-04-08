package com.kincheung.ollama;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.kincheung.ollama.util.TestFileCreator;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Background task for generating test classes.
 */
public class TestGenerationTask extends Task.Backgroundable {
    private final PsiClass targetClass;
    private final String testClassName;
    private final boolean useMockito;
    private final boolean includeComments;

    public TestGenerationTask(
            @NotNull Project project,
            @NotNull PsiClass targetClass,
            @NotNull String testClassName,
            boolean useMockito,
            boolean includeComments) {
        super(project, "Generating Unit Tests", false);
        this.targetClass = targetClass;
        this.testClassName = testClassName;
        this.useMockito = useMockito;
        this.includeComments = includeComments;
    }

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
                        myProject, 
                        targetClass, 
                        testClassName, 
                        testCode
                );
                
                if (testFile != null) {
                    TestFileCreator.openFileInEditor(myProject, testFile);
                } else {
                    Messages.showErrorDialog(
                            myProject,
                            "Could not create test file",
                            "Test Generation Failed"
                    );
                }
            });
        } catch (Exception e) {
            ApplicationManager.getApplication().invokeLater(() -> {
                Messages.showErrorDialog(
                        myProject,
                        "Error generating tests: " + e.getMessage(),
                        "Test Generation Failed"
                );
            });
        }
    }
}
