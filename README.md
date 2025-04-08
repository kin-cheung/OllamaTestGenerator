# Ollama Test Generator Plugin for IntelliJ IDEA

This plugin leverages locally installed [Ollama](https://ollama.ai/) to automatically generate JUnit 5 and Mockito unit tests for Java classes in IntelliJ IDEA.

![Plugin Icon](src/main/resources/icons/ollama_icon.svg)

## Overview

The Ollama Test Generator plugin integrates with your IntelliJ IDEA environment to provide AI-powered test generation capabilities. Using a locally running Ollama instance, it can analyze your Java classes and generate comprehensive unit tests with JUnit 5 and optional Mockito support.

This streamlines the test creation process by providing a solid starting point for your unit tests, allowing you to focus on refining them rather than writing them from scratch.

## Key Features

- **One-Click Test Generation**: Right-click on any Java class to generate unit tests
- **AI-Powered Test Creation**: Leverages Ollama's LLM capabilities to create contextually appropriate tests
- **JUnit 5 Integration**: Generates tests compatible with the latest JUnit framework
- **Mockito Support**: Optional automated mock creation for class dependencies
- **Customizable Settings**: Configure Ollama endpoint, model selection, and generation options
- **Code Inspection**: Automatically identifies classes without corresponding test classes
- **Test Navigation**: Easily navigate between classes and their generated tests

## Project Structure

The plugin is organized into several key components:

1. **Core Action Components**:
   - Test generation action handlers
   - Code inspection tools
   - Background processing tasks

2. **Service Layer**:
   - Ollama API communication service
   - Request/response model classes

3. **Settings Management**:
   - Persistent plugin settings
   - UI components for configuration

4. **Utility Classes**:
   - Java code analysis tools
   - Test file creation utilities

5. **UI Components**:
   - Configuration dialogs
   - Settings panels

## Technical Implementation

- Built for IntelliJ IDEA using the IntelliJ Platform SDK
- Java 11 compatibility
- OkHttp for API communication with Ollama
- GSON for JSON parsing
- IntelliJ PSI for Java code analysis
- Gradle-based build system

## Prerequisites

- IntelliJ IDEA (Community or Ultimate) version 2022.2 or later
- Java 11 or later
- [Ollama](https://ollama.ai/) installed locally with a language model that can generate code (recommended: llama3 or codellama)

## Installation

### Option 1: Install from JetBrains Marketplace

1. Open IntelliJ IDEA
2. Go to **Settings/Preferences > Plugins**
3. Select the **Marketplace** tab
4. Search for "Ollama Test Generator"
5. Click **Install** and restart IntelliJ IDEA when prompted

### Option 2: Manual Installation

1. Download the latest plugin release from the [Releases](https://github.com/kincheung/ollama-test-generator/releases) page
2. Open IntelliJ IDEA
3. Go to **Settings/Preferences > Plugins**
4. Click the gear icon and select **Install Plugin from Disk...**
5. Navigate to the downloaded ZIP file and select it
6. Restart IntelliJ IDEA when prompted

## Configuration

1. After installation, go to **Settings/Preferences > Tools > Ollama Test Generator**
2. Configure the following settings:
   - **Ollama URL**: The URL where your Ollama instance is running (default: http://localhost:11434)
   - **Model name**: The name of the model to use (default: llama3)
   - **Include Mockito**: Whether to include Mockito for mocking dependencies
   - **Include comments**: Whether to include detailed comments in generated tests
   - **Timeout**: Maximum time in seconds to wait for test generation

## Usage

### Generating Tests from Context Menu

1. Right-click on a Java class in the editor or project view
2. Select **Generate Unit Tests with Ollama** from the context menu
3. In the dialog that appears, configure test generation options
4. Click **OK** to generate the tests
5. The generated test file will open automatically when complete

### Using Code Inspection

1. Open a Java class
2. If the class doesn't have a corresponding test, you'll see a warning
3. Click on the warning highlight and select **Generate test with Ollama** from the quick fix menu

## Troubleshooting

- **Ollama Not Available**: Ensure Ollama is running locally at the configured URL
- **Model Not Found**: Verify that the selected model is installed in Ollama using `ollama list`
- **Generation Takes Too Long**: Increase the timeout in settings or try a smaller, faster model
- **Poor Test Quality**: Try a different model or adjust the prompt settings

## Building from Source

```bash
# Clone the repository
git clone https://github.com/kincheung/ollama-test-generator.git
cd ollama-test-generator

# Build the plugin
./gradlew build

# The plugin ZIP file will be in build/distributions/
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.