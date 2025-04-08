package com.kincheung.ollama.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.lang.java.JavaLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for creating test files.
 */
public class TestFileCreator {

    /**
     * Creates a test file for the given class with the generated test code.
     * 
     * @param project The project
     * @param sourceClass The source class to create a test for
     * @param testClassName The name of the test class
     * @param testCode The generated test code
     * @return The created virtual file, or null if creation failed
     */
    @Nullable
    public static VirtualFile createTestFile(
            @NotNull Project project,
            @NotNull PsiClass sourceClass,
            @NotNull String testClassName,
            @NotNull String testCode) {
        
        PsiFile sourceFile = sourceClass.getContainingFile();
        if (!(sourceFile instanceof PsiJavaFile)) {
            return null;
        }
        
        final PsiJavaFile sourceJavaFile = (PsiJavaFile) sourceFile;
        final String packageName = sourceJavaFile.getPackageName();
        
        // Find or create the test directory
        VirtualFile testDirectory = JavaClassAnalyzer.findTestDirectory(sourceClass);
        if (testDirectory == null) {
            return null;
        }
        
        // Create package directories if needed
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiDirectory directory = psiManager.findDirectory(testDirectory);
        if (directory == null) {
            return null;
        }
        
        String[] packageParts = packageName.split("\\.");
        PsiDirectory currentDirectory = directory;
        for (String part : packageParts) {
            if (part.isEmpty()) {
                continue;
            }
            
            PsiDirectory subDir = currentDirectory.findSubdirectory(part);
            if (subDir == null) {
                final PsiDirectory dirToModify = currentDirectory;
                final String dirPart = part;
                final PsiDirectory[] resultDir = new PsiDirectory[1];
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    resultDir[0] = dirToModify.createSubdirectory(dirPart);
                });
                currentDirectory = resultDir[0];
            } else {
                currentDirectory = subDir;
            }
        }
        directory = currentDirectory;
        
        // Check if test file already exists
        PsiFile existingFile = directory.findFile(testClassName + ".java");
        if (existingFile != null) {
            return existingFile.getVirtualFile();
        }
        
        // Create the test file
        final PsiFile[] createdFile = new PsiFile[1];
        final PsiDirectory targetDirectory = directory;
        
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // Prepare the content with package declaration
            StringBuilder content = new StringBuilder();
            if (!packageName.isEmpty()) {
                content.append("package ").append(packageName).append(";\n\n");
            }
            
            // Add the test code
            content.append(testCode);
            
            // Create the file
            PsiFileFactory factory = PsiFileFactory.getInstance(project);
            PsiFile file = factory.createFileFromText(testClassName + ".java", JavaLanguage.INSTANCE, content);
            createdFile[0] = (PsiFile) targetDirectory.add(file);
        });
        
        if (createdFile[0] != null) {
            return createdFile[0].getVirtualFile();
        }
        
        return null;
    }
    
    /**
     * Opens the specified file in the editor.
     * 
     * @param project The project
     * @param file The file to open
     */
    public static void openFileInEditor(@NotNull Project project, @NotNull VirtualFile file) {
        ApplicationManager.getApplication().invokeLater(() -> {
            FileEditorManager.getInstance(project).openFile(file, true);
        });
    }
}
