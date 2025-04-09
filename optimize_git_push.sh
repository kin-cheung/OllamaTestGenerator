#!/bin/bash
# This script optimizes Git for pushing a large repository with many deleted files

# Increase Git timeouts and buffer sizes for better performance with large repos
echo "Configuring Git for optimized pushing..."
git config http.postBuffer 524288000
git config http.lowSpeedLimit 1000
git config http.lowSpeedTime 300

# Try a more targeted push approach
echo "Preparing to push changes..."

# First make sure we're authenticated
echo "Remember to use your GitHub token for authentication in the actual push command"

# The commands below should be executed manually:
echo "Run these commands in your terminal:"
echo "----------------------------------------"
echo "1. Push with increased timeout:"
echo "   timeout 600 git push https://YOUR_GITHUB_TOKEN@github.com/kin-cheung/ollama-test-generator.git main"
echo ""
echo "2. If still timing out, consider pushing with smaller history:"
echo "   git push https://YOUR_GITHUB_TOKEN@github.com/kin-cheung/ollama-test-generator.git --force main"
echo ""
echo "3. Or consider a shallow clone solution if all else fails:"
echo "   # In a new directory:"
echo "   git clone --depth 1 https://github.com/kin-cheung/ollama-test-generator.git"
echo "   cd ollama-test-generator"
echo "   # Copy your changes here"
echo "   git add ."
echo "   git commit -m \"Clean repository state with proper .gitignore\""
echo "   git push"
echo "----------------------------------------"