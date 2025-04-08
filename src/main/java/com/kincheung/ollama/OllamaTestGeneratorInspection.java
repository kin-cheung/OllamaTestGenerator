package com.kincheung.ollama;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.kincheung.ollama.util.JavaClassAnalyzer;
import org.jetbrains.annotations.NotNull;

/**
 * Code inspection to identify Java classes that don't have corresponding test classes.
 */
public class OllamaTestGeneratorInspection extends AbstractBaseJavaLocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitClass(PsiClass aClass) {
                if (shouldSkipClass(aClass)) {
                    return;
                }

                if (!JavaClassAnalyzer.hasTestClass(aClass)) {
                    holder.registerProblem(
                            aClass.getNameIdentifier() != null ? aClass.getNameIdentifier() : aClass,
                            "Class doesn't have a corresponding test class",
                            new CreateTestClassQuickFix()
                    );
                }
                
                super.visitClass(aClass);
            }
        };
    }

    /**
     * Determines whether the inspection should skip this class.
     * 
     * @param aClass The class to check
     * @return true if the class should be skipped
     */
    private boolean shouldSkipClass(PsiClass aClass) {
        // Skip anonymous classes
        if (aClass.getName() == null) {
            return true;
        }
        
        // Skip interfaces, enums, records, and annotations
        if (aClass.isInterface() || aClass.isEnum() || aClass.isRecord() || aClass.isAnnotationType()) {
            return true;
        }
        
        // Skip test classes
        if (JavaClassAnalyzer.isTestClass(aClass)) {
            return true;
        }
        
        // Skip inner classes
        if (aClass.getContainingClass() != null) {
            return true;
        }
        
        return false;
    }

    /**
     * Quick fix to create a test class for the inspected class.
     */
    private static class CreateTestClassQuickFix implements LocalQuickFix {
        @NotNull
        @Override
        public String getName() {
            return "Generate test with Ollama";
        }

        @NotNull
        @Override
        public String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement element = descriptor.getPsiElement();
            if (element == null) {
                return;
            }
            
            PsiClass psiClass = element instanceof PsiClass 
                    ? (PsiClass) element 
                    : (PsiClass) element.getParent();
                    
            OllamaTestGeneratorDialog dialog = new OllamaTestGeneratorDialog(project, psiClass);
            if (dialog.showAndGet()) {
                dialog.generateTests();
            }
        }
    }
}
