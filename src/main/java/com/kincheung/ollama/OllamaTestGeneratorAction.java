package com.kincheung.ollama;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.kincheung.ollama.util.JavaClassAnalyzer;
import org.jetbrains.annotations.NotNull;

/**
 * Action for generating unit tests with Ollama.
 * This action appears in the editor context menu and project view popup menu.
 */
public class OllamaTestGeneratorAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }

        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (psiFile == null || !(psiFile instanceof PsiJavaFile)) {
            Messages.showErrorDialog(project, "Please select a Java file", "Cannot Generate Tests");
            return;
        }

        PsiJavaFile javaFile = (PsiJavaFile) psiFile;
        PsiClass targetClass = getPsiClassFromContext(javaFile, editor);

        if (targetClass == null) {
            Messages.showErrorDialog(project, "No class found in the current context", "Cannot Generate Tests");
            return;
        }

        // Check if the class is a test class already
        if (JavaClassAnalyzer.isTestClass(targetClass)) {
            Messages.showErrorDialog(project, "Cannot generate tests for test classes", "Cannot Generate Tests");
            return;
        }

        // Open the test generator dialog
        OllamaTestGeneratorDialog dialog = new OllamaTestGeneratorDialog(project, targetClass);
        if (dialog.showAndGet()) {
            // User clicked OK, generate the tests with the selected options
            dialog.generateTests();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Enable/disable the action depending on whether we're in a Java file
        Project project = e.getProject();
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        
        boolean enabled = project != null && file != null && "java".equals(file.getExtension());
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    /**
     * Tries to get a PsiClass from the current context - either from the cursor position
     * or just the first class in the file.
     *
     * @param javaFile The Java file
     * @param editor The editor, can be null if called from project view
     * @return The target PsiClass or null if none found
     */
    private PsiClass getPsiClassFromContext(PsiJavaFile javaFile, Editor editor) {
        PsiClass[] classes = javaFile.getClasses();
        if (classes.length == 0) {
            return null;
        }

        if (editor != null) {
            // Try to find class at cursor position
            int offset = editor.getCaretModel().getOffset();
            PsiClass psiClass = PsiTreeUtil.getParentOfType(javaFile.findElementAt(offset), PsiClass.class);
            if (psiClass != null) {
                return psiClass;
            }
        }

        // Fall back to the first class in the file
        return classes[0];
    }
}
