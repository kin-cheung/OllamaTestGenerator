package com.kincheung.ollama.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for analyzing Java classes.
 */
public class JavaClassAnalyzer {
    
    /**
     * Checks if the given class is a test class.
     * 
     * @param psiClass The class to check
     * @return true if the class is a test class
     */
    public static boolean isTestClass(@NotNull PsiClass psiClass) {
        String className = psiClass.getName();
        if (className == null) {
            return false;
        }
        
        // Check class name
        if (className.endsWith("Test") || className.startsWith("Test")) {
            return true;
        }
        
        // Check annotations
        for (PsiAnnotation annotation : psiClass.getAnnotations()) {
            String qualifiedName = annotation.getQualifiedName();
            if (qualifiedName != null && (
                    qualifiedName.equals("org.junit.jupiter.api.Test") ||
                    qualifiedName.equals("org.junit.Test") ||
                    qualifiedName.startsWith("org.junit.jupiter.api."))) {
                return true;
            }
        }
        
        // Check methods with test annotations
        for (PsiMethod method : psiClass.getMethods()) {
            for (PsiAnnotation annotation : method.getAnnotations()) {
                String qualifiedName = annotation.getQualifiedName();
                if (qualifiedName != null && (
                        qualifiedName.equals("org.junit.jupiter.api.Test") ||
                        qualifiedName.equals("org.junit.Test"))) {
                    return true;
                }
            }
        }
        
        // Check if class is in a test source root
        VirtualFile virtualFile = psiClass.getContainingFile().getVirtualFile();
        if (virtualFile != null) {
            String path = virtualFile.getPath();
            if (path.contains("/test/") || path.contains("\\test\\")) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Checks if a test class exists for the given class.
     * 
     * @param psiClass The class to check
     * @return true if a test class exists
     */
    public static boolean hasTestClass(@NotNull PsiClass psiClass) {
        Project project = psiClass.getProject();
        String className = psiClass.getName();
        if (className == null) {
            return false;
        }
        
        // Look for class with the same name + "Test"
        String testClassName = className + "Test";
        
        // Try to find the test class in the same package but in test directory
        String packageName = ((PsiJavaFile) psiClass.getContainingFile()).getPackageName();
        Module module = ModuleUtilCore.findModuleForPsiElement(psiClass);
        GlobalSearchScope scope = module != null ? 
                GlobalSearchScope.moduleWithDependenciesScope(module) : 
                GlobalSearchScope.projectScope(project);
        
        PsiClass[] testClasses = PsiShortNamesCache.getInstance(project)
                .getClassesByName(testClassName, scope);
        
        for (PsiClass testClass : testClasses) {
            PsiFile containingFile = testClass.getContainingFile();
            if (containingFile instanceof PsiJavaFile) {
                String testPackageName = ((PsiJavaFile) containingFile).getPackageName();
                if (testPackageName.equals(packageName)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Determines the appropriate test directory for a class.
     * 
     * @param psiClass The class to analyze
     * @return The virtual file representing the test directory, or null if not found
     */
    @Nullable
    public static VirtualFile findTestDirectory(@NotNull PsiClass psiClass) {
        PsiFile containingFile = psiClass.getContainingFile();
        if (!(containingFile instanceof PsiJavaFile)) {
            return null;
        }
        
        VirtualFile vFile = containingFile.getVirtualFile();
        if (vFile == null) {
            return null;
        }
        
        // Try to find test source roots
        Module module = ModuleUtilCore.findModuleForPsiElement(psiClass);
        if (module == null) {
            return null;
        }
        
        VirtualFile sourceRoot = findSourceRoot(vFile);
        if (sourceRoot == null) {
            return null;
        }
        
        // If the source root is already a test root, use it
        String path = sourceRoot.getPath();
        if (path.contains("/test/") || path.contains("\\test\\")) {
            return sourceRoot;
        }
        
        // Try to find the corresponding test root
        // Common patterns: src/main/java -> src/test/java, src -> test
        String sourcePath = sourceRoot.getPath();
        String testPath = sourcePath.replace("/main/", "/test/");
        if (testPath.equals(sourcePath)) {
            testPath = sourcePath.replaceFirst("/src/", "/test/");
        }
        if (testPath.equals(sourcePath)) {
            // If we couldn't find a specific test directory, use the source directory
            return sourceRoot;
        }
        
        VirtualFile testRoot = vFile.getFileSystem().findFileByPath(testPath);
        return testRoot != null ? testRoot : sourceRoot;
    }
    
    /**
     * Finds the source root for the given file.
     * 
     * @param file The file to analyze
     * @return The source root virtual file
     */
    @Nullable
    private static VirtualFile findSourceRoot(@NotNull VirtualFile file) {
        VirtualFile parent = file;
        while (parent != null) {
            // Look for common source root markers
            VirtualFile srcDir = parent.findChild("src");
            if (srcDir != null && srcDir.isDirectory()) {
                return srcDir;
            }
            
            // Look for Maven/Gradle style source roots
            VirtualFile mainJava = parent.findFileByRelativePath("src/main/java");
            if (mainJava != null && mainJava.isDirectory()) {
                return mainJava;
            }
            
            parent = parent.getParent();
        }
        
        return null;
    }
}
