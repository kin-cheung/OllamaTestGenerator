# Git Push Guide for Large Repository Cleanup

This guide provides several options for successfully pushing this repository to GitHub after removing the build artifacts and properly configuring `.gitignore`.

## Option 1: Clone Fresh and Push

This is the cleanest and most reliable approach:

```bash
# 1. Clone a fresh copy of the repository to a new directory
git clone https://github.com/kin-cheung/ollama-test-generator.git fresh-repo
cd fresh-repo

# 2. Copy only the essential files (not build artifacts) from your current repo
# Copy these files from your current working directory:
# - src/
# - gradle/
# - .gitignore (the updated one)
# - build.gradle
# - settings.gradle
# - README.md
# - gradlew and gradlew.bat

# 3. Stage all files
git add .

# 4. Commit changes
git commit -m "Clean repository state with proper .gitignore"

# 5. Push to GitHub
git push https://YOUR_GITHUB_TOKEN@github.com/kin-cheung/ollama-test-generator.git main
```

## Option 2: Force Push Current Branch with Extended Timeout

If you want to keep the current commit history:

```bash
# 1. Configure Git for larger repositories
git config http.postBuffer 524288000
git config http.lowSpeedLimit 1000
git config http.lowSpeedTime 600

# 2. Clean up the Git repository
git gc --aggressive --prune=now

# 3. Force push with extended timeout (adjust the timeout value as needed)
timeout 600 git push --force https://YOUR_GITHUB_TOKEN@github.com/kin-cheung/ollama-test-generator.git main
```

## Option 3: Create an Orphan Branch with Clean History

If you're willing to start with a fresh history:

```bash
# 1. Create a new branch with no history
git checkout --orphan fresh_branch

# 2. Add all the files you want to keep
git add .gitignore README.md build.gradle settings.gradle
git add src/ gradle/ gradlew gradlew.bat

# 3. Commit these files
git commit -m "Fresh start with clean repository"

# 4. Delete the old main branch and rename this one
git branch -D main
git branch -m main

# 5. Force push this branch
git push --force https://YOUR_GITHUB_TOKEN@github.com/kin-cheung/ollama-test-generator.git main
```

## Important Note

These operations can change Git history, particularly options 2 and 3. Be sure that's acceptable for your workflow before proceeding.

Replace `YOUR_GITHUB_TOKEN` with your actual GitHub token when executing these commands.