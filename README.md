# Ollama Test Generator Plugin for IntelliJ IDEA

This plugin leverages locally installed [Ollama](https://ollama.ai/) to automatically generate JUnit 5 and Mockito unit tests for Java classes in IntelliJ IDEA.

![Plugin Icon](src/main/resources/icons/ollama_icon.svg)

## Overview

The Ollama Test Generator plugin seamlessly integrates with your IntelliJ IDEA environment to provide AI-powered test generation capabilities. Using a locally running Ollama instance, it analyzes your Java classes and generates comprehensive unit tests with JUnit 5 and optional Mockito support.

This plugin significantly improves developer productivity by:
- Reducing time spent writing boilerplate test code
- Ensuring consistent test coverage across your codebase
- Providing intelligent test cases based on code analysis
- Streamlining the test creation process with one-click generation

By leveraging the power of local LLMs, the plugin operates entirely within your development environment without sending code to external services, ensuring security and privacy.

## Key Features

- **One-Click Test Generation**: Right-click on any Java class to generate complete unit tests with a single action
- **AI-Powered Test Creation**: Leverages Ollama's LLM capabilities to analyze code structure, methods, parameters, and return types to create contextually appropriate tests
- **JUnit 5 Integration**: Generates tests using the latest JUnit Jupiter API with proper annotations and lifecycle methods
- **Mockito Support**: Intelligent detection and mocking of class dependencies using Mockito's advanced features
- **Smart Test Case Generation**: Creates meaningful assertions based on method semantics and return types
- **Customizable Settings**: Flexible configuration options for Ollama endpoint, model selection, temperature, and generation options
- **Code Inspection**: Proactive detection of untested classes with quick-fix actions for test generation
- **Test Navigation**: Seamless navigation between source classes and their corresponding test classes
- **Security & Privacy**: All processing happens locally using your Ollama instance - no code is sent to external servers

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
- Java 17 compatibility
- OkHttp for API communication with Ollama
- GSON for JSON parsing
- IntelliJ PSI for Java code analysis
- Gradle-based build system

## Prerequisites

- IntelliJ IDEA (Community or Ultimate) version 2022.2 through 2024.3
- Java 17 or later
- [Ollama](https://ollama.ai/) installed locally with a language model that can generate code (recommended: qwen2.5-coder:7b)

## Installation

### Option 1: Install from JetBrains Marketplace

1. Open IntelliJ IDEA
2. Go to **Settings/Preferences > Plugins**
3. Select the **Marketplace** tab
4. Search for "Ollama Test Generator"
5. Click **Install** and restart IntelliJ IDEA when prompted

### Option 2: Manual Installation

1. Download the latest plugin release (ollama-test-generator-1.0.2.zip) from the [Releases](https://github.com/kin-cheung/ollama-test-generator/releases) page
2. Open IntelliJ IDEA
3. Go to **Settings/Preferences > Plugins**
4. Click the gear icon and select **Install Plugin from Disk...**
5. Navigate to the downloaded ZIP file and select it
6. Restart IntelliJ IDEA when prompted

### Verifying Installation

After installation and restart, you should see:
- A new menu item in the context menu when right-clicking on Java classes
- A new settings section under **Tools > Ollama Test Generator**
- Ollama-related inspections in your code

## Configuration

### Basic Setup

1. After installation, go to **Settings/Preferences > Tools > Ollama Test Generator**
2. Configure the following essential settings:
   - **Ollama URL**: The URL where your Ollama instance is running (default: http://localhost:11434)
   - **Model name**: The name of the model to use (default: qwen2.5-coder:7b)
   
### Advanced Options

Additional configuration options to customize test generation:
   - **Include Mockito**: Toggle to include Mockito for mocking dependencies (recommended for classes with external dependencies)
   - **Include comments**: Add detailed explanatory comments in generated tests
   - **Temperature**: Adjust creativity level (0.0-1.0) - lower values for more deterministic outputs
   - **Timeout**: Maximum time in seconds to wait for test generation (increase for larger classes)
   - **Custom Prompt Template**: Customize the instructions sent to the LLM (advanced users)

### Setting Up Ollama

If you haven't installed Ollama yet:
1. Download and install from [ollama.ai](https://ollama.ai)
2. Start the Ollama service
3. Pull a suitable model: `ollama pull qwen2.5-coder:7b`
4. Verify the service is running at http://localhost:11434 before using the plugin

## Usage

### Generating Tests from Context Menu

The fastest way to generate tests for a Java class:

1. Right-click on a Java class in the editor or project view
2. Select **Generate Unit Tests with Ollama** from the context menu
3. In the dialog that appears, you can customize:
   - Test class name (default follows the `ClassNameTest` pattern)
   - Output directory (default is the corresponding test directory)
   - Model parameters (temperature, max tokens)
   - Mockito inclusion and assertion style
4. Click **OK** to generate the tests
5. The generated test file will open automatically when complete
6. Review and modify the generated tests as needed

### Using Code Inspection

Discover untested classes with the built-in code inspection:

1. Open a Java class in the editor
2. If the class doesn't have a corresponding test class, you'll see a warning highlight
3. Hover over the highlighted class name
4. Click on the warning highlight and select **Generate test with Ollama** from the quick fix menu
5. Configure options in the dialog that appears
6. The plugin will create appropriate test class

### Examples

#### Sample Java Class
```java
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public User createUser(String username, String email, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        User user = new User(username, email, password);
        User savedUser = userRepository.save(user);
        emailService.sendWelcomeEmail(email, username);
        return savedUser;
    }
}
```

#### Generated Test (with Mockito)
```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private EmailService emailService;
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, emailService);
    }
    
    @Test
    void createUser_ShouldCreateAndReturnUser_WhenUsernameDoesNotExist() {
        // Arrange
        String username = "testUser";
        String email = "test@example.com";
        String password = "password123";
        User expectedUser = new User(username, email, password);
        User savedUser = new User(username, email, password);
        savedUser.setId(1L);
        
        when(userRepository.findByUsername(username)).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        doNothing().when(emailService).sendWelcomeEmail(email, username);
        
        // Act
        User result = userService.createUser(username, email, password);
        
        // Assert
        assertNotNull(result);
        assertEquals(savedUser, result);
        verify(userRepository).findByUsername(username);
        verify(userRepository).save(any(User.class));
        verify(emailService).sendWelcomeEmail(email, username);
    }
    
    @Test
    void createUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        // Arrange
        String username = "existingUser";
        String email = "existing@example.com";
        String password = "password123";
        User existingUser = new User(username, email, password);
        
        when(userRepository.findByUsername(username)).thenReturn(existingUser);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            userService.createUser(username, email, password)
        );
        
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).findByUsername(username);
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
    }
}
```

### Keyboard Shortcuts

For faster workflow, you can assign a keyboard shortcut:

1. Go to **Settings/Preferences > Keymap**
2. Search for "Ollama Test Generator"
3. Right-click on the action and select **Add Keyboard Shortcut**
4. Assign your preferred shortcut combination
5. Use this shortcut when a Java class is open to quickly generate tests

## Troubleshooting

### Common Issues

- **Ollama Not Available**: 
  - Ensure Ollama is running locally at the configured URL
  - Verify with `curl http://localhost:11434/api/tags` in terminal
  - Check firewall settings if running on a different machine

- **Model Not Found**: 
  - Verify that the selected model is installed using `ollama list`
  - If missing, install with `ollama pull llama3` or your preferred model
  - Check model name spelling in the plugin settings

- **Generation Takes Too Long**: 
  - Increase the timeout in settings for complex classes
  - Try a smaller, faster model (llama3 is faster than codellama)
  - Reduce max tokens in generation settings

- **Poor Test Quality**: 
  - Try different models (codellama often produces better code than general models)
  - Adjust temperature settings (lower for more predictable outputs)
  - Simplify complex classes or break them down
  - Use custom prompt templates to guide the LLM

### Log Files

Check the IDE log files for detailed error information:

1. In IntelliJ IDEA, go to **Help > Show Log in Explorer/Finder**
2. Look for entries related to "Ollama" or "Test Generator"
3. Submit these logs when reporting issues

## Building from Source

### Prerequisites for Development

- IntelliJ IDEA (Community or Ultimate) 2022.2 through 2024.1
- Java Development Kit (JDK) 17+
- Gradle 8.0+ (wrapper included)
- Git

### Build Steps

```bash
# Clone the repository
git clone https://github.com/kin-cheung/ollama-test-generator.git
cd ollama-test-generator

# Build the plugin
./gradlew build

# The plugin ZIP file will be in build/distributions/
```

### Running and Debugging

To run or debug the plugin directly from source:

```bash
# Start IntelliJ IDEA with the plugin installed
./gradlew runIde

# For debugging
./gradlew runIdeForUiTests
```

### Project Structure

- `src/main/java/com/kincheung/ollama/` - Core plugin code
- `src/main/resources/` - Plugin resources, icons, and metadata
- `build.gradle` - Gradle build configuration
- `src/main/resources/META-INF/plugin.xml` - Plugin descriptor file

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Areas for Contribution

- Improved LLM prompts for more accurate test generation
- Support for additional testing frameworks
- Enhanced code analysis for complex Java classes
- UI improvements and usability enhancements
- Documentation and examples
- Performance optimizations

### Code Style

Please follow the existing code style in the project. The plugin uses standard Java conventions with IntelliJ IDEA default formatting.
