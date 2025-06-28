package com.codehaven.backend.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroqClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${groq.api.key}")
    private String apiKey;
    
    @Value("${groq.api.url}")
    private String apiUrl;
    
    public CompletableFuture<GroqResponse> chatCompletion(GroqRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Making Groq API call with model: {}, messages: {}", 
                    request.getModel(), request.getMessages().size());
                
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + apiKey);
                headers.set("Content-Type", "application/json");
                
                HttpEntity<GroqRequest> entity = new HttpEntity<>(request, headers);
                
                ResponseEntity<GroqResponse> response = restTemplate.exchange(
                    apiUrl + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    GroqResponse.class
                );
                
                log.info("Groq API call successful for model: {}", request.getModel());
                return response.getBody();
                
            } catch (Exception e) {
                log.error("Error calling Groq API with request: {}", request, e);
                log.error("API URL: {}", apiUrl + "/chat/completions");
                log.error("API Key present: {}", apiKey != null && !apiKey.isEmpty());
                throw new RuntimeException("Failed to call Groq API: " + e.getMessage(), e);
            }
        });
    }
    
    public CompletableFuture<GroqResponse> reviewCode(String code, String language) {
        String systemPrompt = """
            You are an expert code reviewer. Analyze the provided code and give constructive feedback.
            Focus on:
            1. Code quality and best practices
            2. Performance optimizations
            3. Security concerns
            4. Maintainability and readability
            5. Potential bugs or edge cases
            
            Provide specific, actionable recommendations with examples when possible.
            """;
        
        String userPrompt = String.format("""
            Please review this %s code:
            
            ```%s
            %s
            ```
            """, language, language, code);
        
        GroqRequest request = new GroqRequest();
        request.setModel("llama3-8b-8192"); // Use valid model from the available list
        request.setMessages(List.of(
            GroqRequest.Message.system(systemPrompt),
            GroqRequest.Message.user(userPrompt)
        ));
        request.setMaxTokens(1500);
        request.setTemperature(0.3);
        
        return chatCompletion(request);
    }
    
    public CompletableFuture<GroqResponse> suggestBugFix(String code, String language, String description) {
        String systemPrompt = """
            You are an expert software engineer specializing in debugging and bug fixes.
            Analyze the provided code and the described issue to suggest a solution.
            
            Provide:
            1. Root cause analysis
            2. Specific fix with code examples
            3. Explanation of the solution
            4. Prevention strategies for similar issues
            """;
        
        String userPrompt = String.format("""
            Bug Description: %s
            
            Code (%s):
            ```%s
            %s
            ```
            
            Please help me fix this bug.
            """, description, language, language, code);
        
        GroqRequest request = new GroqRequest();
        request.setModel("llama3-8b-8192"); // Use valid model from the available list
        request.setMessages(List.of(
            GroqRequest.Message.system(systemPrompt),
            GroqRequest.Message.user(userPrompt)
        ));
        request.setMaxTokens(1200);
        request.setTemperature(0.2);
        
        return chatCompletion(request);
    }
    
    public CompletableFuture<GroqResponse> optimizeCode(String code, String language) {
        String systemPrompt = """
            You are a performance optimization expert. Analyze the provided code and suggest optimizations.
            
            Focus on:
            1. Time complexity improvements
            2. Memory usage optimization
            3. Algorithm efficiency
            4. Language-specific optimizations
            5. Best practices for performance
            
            Provide optimized code with explanations.
            """;
        
        String userPrompt = String.format("""
            Please optimize this %s code for better performance:
            
            ```%s
            %s
            ```
            """, language, language, code);
        
        GroqRequest request = new GroqRequest();
        request.setModel("llama3-8b-8192"); // Use valid model from the available list
        request.setMessages(List.of(
            GroqRequest.Message.system(systemPrompt),
            GroqRequest.Message.user(userPrompt)
        ));
        request.setMaxTokens(1200);
        request.setTemperature(0.3);
        
        return chatCompletion(request);
    }
    
    public CompletableFuture<GroqResponse> explainCode(String code, String language) {
        String systemPrompt = """
            You are a programming mentor. Explain the provided code in a clear, educational manner.
            
            Include:
            1. Overview of what the code does
            2. Step-by-step breakdown
            3. Key concepts and patterns used
            4. Potential use cases
            5. Learning points for developers
            """;
        
        String userPrompt = String.format("""
            Please explain this %s code:
            
            ```%s
            %s
            ```
            """, language, language, code);
        
        GroqRequest request = new GroqRequest();
        request.setModel("llama3-8b-8192"); // Default model is sufficient for explanations
        request.setMessages(List.of(
            GroqRequest.Message.system(systemPrompt),
            GroqRequest.Message.user(userPrompt)
        ));
        request.setMaxTokens(1000);
        request.setTemperature(0.4);
        
        return chatCompletion(request);
    }
    
    public CompletableFuture<GroqResponse> generateDocumentation(String code, String language) {
        String systemPrompt = """
            You are a technical documentation expert. Generate comprehensive documentation for the provided code.
            
            Include:
            1. Function/class descriptions
            2. Parameter documentation
            3. Return value descriptions
            4. Usage examples
            5. Important notes or warnings
            
            Format the documentation appropriately for the language (JSDoc, JavaDoc, etc.).
            """;
        
        String userPrompt = String.format("""
            Please generate documentation for this %s code:
            
            ```%s
            %s
            ```
            """, language, language, code);
        
        GroqRequest request = new GroqRequest();
        request.setModel("llama3-8b-8192"); // Default model for documentation
        request.setMessages(List.of(
            GroqRequest.Message.system(systemPrompt),
            GroqRequest.Message.user(userPrompt)
        ));
        request.setMaxTokens(1000);
        request.setTemperature(0.3);
        
        return chatCompletion(request);
    }
    
    public CompletableFuture<GroqResponse> suggestTags(String title, String description) {
        String systemPrompt = """
            You are a content categorization expert. Based on the title and description provided,
            suggest 5-8 relevant tags that would help categorize and make the content discoverable.
            
            Consider:
            1. Programming languages mentioned
            2. Frameworks and technologies
            3. Concepts and patterns
            4. Application domains
            5. Difficulty level
            
            Return only the tags as a comma-separated list.
            """;
        
        String userPrompt = String.format("""
            Title: %s
            Description: %s
            
            Please suggest relevant tags for this content.
            """, title, description);
        
        GroqRequest request = new GroqRequest();
        request.setModel("llama3-8b-8192"); // Default model for tag suggestions
        request.setMessages(List.of(
            GroqRequest.Message.system(systemPrompt),
            GroqRequest.Message.user(userPrompt)
        ));
        request.setMaxTokens(200);
        request.setTemperature(0.5);
        
        return chatCompletion(request);
    }
    
    public CompletableFuture<GroqResponse> generateUnitTests(String code, String language) {
        String systemPrompt = """
            You are a test automation expert. Generate comprehensive unit tests for the provided code.
            
            Include:
            1. Test cases for normal scenarios
            2. Edge cases and boundary conditions
            3. Error conditions and exception handling
            4. Mock objects where necessary
            5. Proper test structure and naming
            
            Use appropriate testing frameworks for the language.
            """;
        
        String userPrompt = String.format("""
            Please generate unit tests for this %s code:
            
            ```%s
            %s
            ```
            """, language, language, code);
        
        GroqRequest request = new GroqRequest();
        request.setModel("llama3-8b-8192"); // Use valid model from the available list
        request.setMessages(List.of(
            GroqRequest.Message.system(systemPrompt),
            GroqRequest.Message.user(userPrompt)
        ));
        request.setMaxTokens(1500);
        request.setTemperature(0.3);
        
        return chatCompletion(request);
    }
}
