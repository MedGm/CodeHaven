package com.codehaven.backend.application.usecase;

import com.codehaven.backend.application.dto.ai.*;
import com.codehaven.backend.application.dto.mapper.AiMapper;
import com.codehaven.backend.domain.model.AiRequest;
import com.codehaven.backend.domain.model.User;
import com.codehaven.backend.domain.repository.AiRequestRepository;
import com.codehaven.backend.domain.service.UserService;
import com.codehaven.backend.infrastructure.client.GroqClient;
import com.codehaven.backend.infrastructure.client.GroqRequest;
import com.codehaven.backend.infrastructure.client.GroqResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AiPoweredFeaturesUseCase {

    private final AiRequestRepository aiRequestRepository;
    private final UserService userService;
    private final GroqClient groqClient;
    private final AiMapper aiMapper;

    public AiResponseDto reviewCode(CodeReviewRequest request, String username) {
        log.info("Processing code review for user: {}", username);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        long startTime = System.currentTimeMillis();
        
        // Create AI request record
        AiRequest aiRequest = AiRequest.builder()
                .type(AiRequest.RequestType.CODE_REVIEW)
                .inputData(aiMapper.createInputData(request, "CODE_REVIEW"))
                .status(AiRequest.Status.PROCESSING)
                .user(user)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        try {
            String prompt = buildCodeReviewPrompt(request);
            GroqRequest groqRequest = new GroqRequest();
            groqRequest.setModel("llama3-8b-8192"); // Use a valid Groq model
            groqRequest.setMessages(List.of(GroqRequest.Message.user(prompt)));
            groqRequest.setMaxTokens(2000);
            groqRequest.setTemperature(0.3);
            
            GroqResponse response = groqClient.chatCompletion(groqRequest).get();
            long processingTime = System.currentTimeMillis() - startTime;
            
            // Parse response into structured format
            String responseText = response.getChoices().get(0).getMessage().getContent();
            ParsedAiResponse parsedResponse = parseCodeReviewResponse(responseText);
            
            // Update AI request with results
            aiRequest.setResponseData(aiMapper.createResponseData(
                    responseText,
                    null,
                    parsedResponse.getSuggestions(),
                    parsedResponse.getIssues(),
                    parsedResponse.getConfidenceScore()
            ));
            aiRequest.setStatus(AiRequest.Status.COMPLETED);
            aiRequest.setProcessingTimeMs(processingTime);
            aiRequest.setPromptTokens(response.getUsage().getPromptTokens());
            aiRequest.setCompletionTokens(response.getUsage().getCompletionTokens());
            aiRequest.setTotalTokens(response.getUsage().getTotalTokens());
            
            aiRequest = aiRequestRepository.save(aiRequest);
            
            log.info("Code review completed for user: {} in {}ms", username, processingTime);
            return aiMapper.toDto(aiRequest);
            
        } catch (Exception e) {
            log.error("Error processing code review for user: {}", username, e);
            aiRequest.setStatus(AiRequest.Status.FAILED);
            aiRequest.setErrorMessage(e.getMessage());
            aiRequest.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            aiRequestRepository.save(aiRequest);
            throw new RuntimeException("Failed to process code review: " + e.getMessage(), e);
        }
    }

    public AiResponseDto fixBug(BugFixRequest request, String username) {
        log.info("Processing bug fix for user: {}", username);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        long startTime = System.currentTimeMillis();
        
        AiRequest aiRequest = AiRequest.builder()
                .type(AiRequest.RequestType.BUG_FIX)
                .inputData(aiMapper.createInputData(request, "BUG_FIX"))
                .status(AiRequest.Status.PROCESSING)
                .user(user)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        try {
            String prompt = buildBugFixPrompt(request);
            GroqRequest groqRequest = new GroqRequest();
            groqRequest.setModel("llama3-8b-8192");
            groqRequest.setMessages(List.of(GroqRequest.Message.user(prompt)));
            groqRequest.setMaxTokens(2000);
            groqRequest.setTemperature(0.2);
            
            GroqResponse response = groqClient.chatCompletion(groqRequest).get();
            long processingTime = System.currentTimeMillis() - startTime;
            
            String responseText = response.getChoices().get(0).getMessage().getContent();
            ParsedAiResponse parsedResponse = parseBugFixResponse(responseText);
            
            aiRequest.setResponseData(aiMapper.createResponseData(
                    responseText,
                    parsedResponse.getSuggestedCode(),
                    parsedResponse.getSuggestions(),
                    parsedResponse.getIssues(),
                    parsedResponse.getConfidenceScore()
            ));
            aiRequest.setStatus(AiRequest.Status.COMPLETED);
            aiRequest.setProcessingTimeMs(processingTime);
            aiRequest.setPromptTokens(response.getUsage().getPromptTokens());
            aiRequest.setCompletionTokens(response.getUsage().getCompletionTokens());
            aiRequest.setTotalTokens(response.getUsage().getTotalTokens());
            
            aiRequest = aiRequestRepository.save(aiRequest);
            
            log.info("Bug fix completed for user: {} in {}ms", username, processingTime);
            return aiMapper.toDto(aiRequest);
            
        } catch (Exception e) {
            log.error("Error processing bug fix for user: {}", username, e);
            aiRequest.setStatus(AiRequest.Status.FAILED);
            aiRequest.setErrorMessage(e.getMessage());
            aiRequest.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            aiRequestRepository.save(aiRequest);
            throw new RuntimeException("Failed to process bug fix: " + e.getMessage(), e);
        }
    }

    public AiResponseDto optimizeCode(CodeOptimizationRequest request, String username) {
        log.info("Processing code optimization for user: {}", username);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        long startTime = System.currentTimeMillis();
        
        AiRequest aiRequest = AiRequest.builder()
                .type(AiRequest.RequestType.CODE_OPTIMIZATION)
                .inputData(aiMapper.createInputData(request, "CODE_OPTIMIZATION"))
                .status(AiRequest.Status.PROCESSING)
                .user(user)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        try {
            String prompt = buildCodeOptimizationPrompt(request);
            GroqRequest groqRequest = new GroqRequest();
            groqRequest.setModel("llama3-8b-8192");
            groqRequest.setMessages(List.of(GroqRequest.Message.user(prompt)));
            groqRequest.setMaxTokens(2000);
            groqRequest.setTemperature(0.3);
            
            GroqResponse response = groqClient.chatCompletion(groqRequest).get();
            long processingTime = System.currentTimeMillis() - startTime;
            
            String responseText = response.getChoices().get(0).getMessage().getContent();
            ParsedAiResponse parsedResponse = parseOptimizationResponse(responseText);
            
            aiRequest.setResponseData(aiMapper.createResponseData(
                    responseText,
                    parsedResponse.getSuggestedCode(),
                    parsedResponse.getSuggestions(),
                    null,
                    parsedResponse.getConfidenceScore()
            ));
            aiRequest.setStatus(AiRequest.Status.COMPLETED);
            aiRequest.setProcessingTimeMs(processingTime);
            aiRequest.setPromptTokens(response.getUsage().getPromptTokens());
            aiRequest.setCompletionTokens(response.getUsage().getCompletionTokens());
            aiRequest.setTotalTokens(response.getUsage().getTotalTokens());
            
            aiRequest = aiRequestRepository.save(aiRequest);
            
            log.info("Code optimization completed for user: {} in {}ms", username, processingTime);
            return aiMapper.toDto(aiRequest);
            
        } catch (Exception e) {
            log.error("Error processing code optimization for user: {}", username, e);
            aiRequest.setStatus(AiRequest.Status.FAILED);
            aiRequest.setErrorMessage(e.getMessage());
            aiRequest.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            aiRequestRepository.save(aiRequest);
            throw new RuntimeException("Failed to process code optimization: " + e.getMessage(), e);
        }
    }

    public AiResponseDto explainCode(CodeExplanationRequest request, String username) {
        log.info("Processing code explanation for user: {}", username);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        long startTime = System.currentTimeMillis();
        
        AiRequest aiRequest = AiRequest.builder()
                .type(AiRequest.RequestType.EXPLANATION)
                .inputData(aiMapper.createInputData(request, "EXPLANATION"))
                .status(AiRequest.Status.PROCESSING)
                .user(user)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        try {
            String prompt = buildCodeExplanationPrompt(request);
            GroqRequest groqRequest = new GroqRequest();
            groqRequest.setModel("llama3-8b-8192");
            groqRequest.setMessages(List.of(GroqRequest.Message.user(prompt)));
            groqRequest.setMaxTokens(1500);
            groqRequest.setTemperature(0.4);
            
            GroqResponse response = groqClient.chatCompletion(groqRequest).get();
            long processingTime = System.currentTimeMillis() - startTime;
            
            String responseText = response.getChoices().get(0).getMessage().getContent();
            
            aiRequest.setResponseData(responseText);
            aiRequest.setStatus(AiRequest.Status.COMPLETED);
            aiRequest.setProcessingTimeMs(processingTime);
            aiRequest.setPromptTokens(response.getUsage().getPromptTokens());
            aiRequest.setCompletionTokens(response.getUsage().getCompletionTokens());
            aiRequest.setTotalTokens(response.getUsage().getTotalTokens());
            
            aiRequest = aiRequestRepository.save(aiRequest);
            
            log.info("Code explanation completed for user: {} in {}ms", username, processingTime);
            return aiMapper.toDto(aiRequest);
            
        } catch (Exception e) {
            log.error("Error processing code explanation for user: {}", username, e);
            aiRequest.setStatus(AiRequest.Status.FAILED);
            aiRequest.setErrorMessage(e.getMessage());
            aiRequest.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            aiRequestRepository.save(aiRequest);
            throw new RuntimeException("Failed to process code explanation: " + e.getMessage(), e);
        }
    }

    public AiResponseDto generateCode(CodeGenerationRequest request, String username) {
        log.info("Processing code generation for user: {}", username);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        long startTime = System.currentTimeMillis();
        
        AiRequest aiRequest = AiRequest.builder()
                .type(AiRequest.RequestType.CODE_GENERATION)
                .inputData(aiMapper.createInputData(request, "CODE_GENERATION"))
                .status(AiRequest.Status.PROCESSING)
                .user(user)
                .build();
        
        aiRequest = aiRequestRepository.save(aiRequest);
        
        try {
            String prompt = buildCodeGenerationPrompt(request);
            GroqRequest groqRequest = new GroqRequest();
            groqRequest.setModel("llama3-8b-8192"); // Use a valid Groq model
            groqRequest.setMessages(List.of(GroqRequest.Message.user(prompt)));
            groqRequest.setMaxTokens(1500);
            groqRequest.setTemperature(0.3);
            
            String responseText;
            String generatedCode;
            
            try {
                // Try to use real Groq API first
                GroqResponse response = groqClient.chatCompletion(groqRequest).get();
                responseText = response.getChoices().get(0).getMessage().getContent();
                generatedCode = extractCodeFromResponse(responseText);
                
                // Update with real API response data
                aiRequest.setPromptTokens(response.getUsage().getPromptTokens());
                aiRequest.setCompletionTokens(response.getUsage().getCompletionTokens());
                aiRequest.setTotalTokens(response.getUsage().getTotalTokens());
                
                log.info("Code generation completed using Groq API for user: {}", username);
                
            } catch (Exception apiException) {
                // Fallback to mock generation if API fails
                log.warn("Groq API failed, falling back to mock generation: {}", apiException.getMessage());
                generatedCode = generateMockCode(request);
                responseText = "Mock generated code for: " + request.getDescription() + "\n\n```" + 
                             (request.getLanguage() != null ? request.getLanguage() : "") + "\n" + 
                             generatedCode + "\n```\n\n" +
                             "Note: This is a mock response due to API connectivity issues. " +
                             "The generated code provides a basic structure for your requirements.";
                
                // Set default token usage for mock
                aiRequest.setPromptTokens(100);
                aiRequest.setCompletionTokens(200);
                aiRequest.setTotalTokens(300);
                
                log.info("Code generation completed using mock generation for user: {}", username);
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            aiRequest.setResponseData(aiMapper.createResponseData(
                    responseText,
                    generatedCode,
                    null,
                    null,
                    85 // Default confidence for code generation
            ));
            aiRequest.setStatus(AiRequest.Status.COMPLETED);
            aiRequest.setProcessingTimeMs(processingTime);
            
            aiRequest = aiRequestRepository.save(aiRequest);
            
            log.info("Code generation completed for user: {} in {}ms", username, processingTime);
            return aiMapper.toDto(aiRequest);
            
        } catch (Exception e) {
            log.error("Error processing code generation for user: {}", username, e);
            aiRequest.setStatus(AiRequest.Status.FAILED);
            aiRequest.setErrorMessage(e.getMessage());
            aiRequest.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            aiRequestRepository.save(aiRequest);
            throw new RuntimeException("Failed to process code generation: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Page<AiResponseDto> getUserAiHistory(String username, Pageable pageable) {
        log.info("Fetching AI history for user: {}", username);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        List<AiRequest> allRequests = aiRequestRepository.findByUserOrderByCreatedAtDesc(user);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRequests.size());
        List<AiRequest> pageContent = allRequests.subList(start, end);
        Page<AiRequest> requests = new PageImpl<>(pageContent, pageable, allRequests.size());
        List<AiResponseDto> responses = requests.getContent().stream()
                .map(aiMapper::toDto)
                .toList();
        
        return new PageImpl<>(responses, pageable, requests.getTotalElements());
    }

    @Transactional(readOnly = true)
    public AiResponseDto getAiRequestById(Long requestId, String username) {
        log.info("Fetching AI request {} for user: {}", requestId, username);
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        
        AiRequest aiRequest = aiRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("AI request not found: " + requestId));
        
        // Check if user owns the request
        if (!aiRequest.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You can only view your own AI requests");
        }
        
        return aiMapper.toDto(aiRequest);
    }

    // Helper methods for building prompts
    private String buildCodeReviewPrompt(CodeReviewRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please review the following ").append(request.getLanguage() != null ? request.getLanguage() : "").append(" code ");
        
        if (request.getReviewType() != null) {
            prompt.append("focusing on ").append(request.getReviewType().toLowerCase()).append(" aspects ");
        }
        
        prompt.append(":\n\n```\n").append(request.getCode()).append("\n```\n\n");
        
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            prompt.append("Context: ").append(request.getContext()).append("\n\n");
        }
        
        prompt.append("Please provide:\n");
        prompt.append("1. Overall assessment\n");
        prompt.append("2. Specific issues found (if any)\n");
        prompt.append("3. Suggestions for improvement\n");
        prompt.append("4. Best practices recommendations\n");
        prompt.append("5. Security considerations (if applicable)\n\n");
        prompt.append("Format your response clearly with sections for issues and suggestions.");
        
        return prompt.toString();
    }

    private String buildBugFixPrompt(BugFixRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("I have a bug in my ").append(request.getLanguage() != null ? request.getLanguage() : "").append(" code. ");
        prompt.append("Here's the error/problem: ").append(request.getErrorDescription()).append("\n\n");
        
        prompt.append("Code:\n```\n").append(request.getCode()).append("\n```\n\n");
        
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            prompt.append("Additional context: ").append(request.getContext()).append("\n\n");
        }
        
        prompt.append("Please:\n");
        prompt.append("1. Identify the root cause of the issue\n");
        prompt.append("2. Provide the corrected code\n");
        prompt.append("3. Explain what was wrong and why your fix works\n");
        prompt.append("4. Suggest how to prevent similar issues in the future\n\n");
        prompt.append("Please provide the fixed code in a clear, copy-pasteable format.");
        
        return prompt.toString();
    }

    private String buildCodeOptimizationPrompt(CodeOptimizationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please optimize the following ").append(request.getLanguage() != null ? request.getLanguage() : "").append(" code ");
        
        if (request.getOptimizationType() != null) {
            prompt.append("for ").append(request.getOptimizationType().toLowerCase()).append(" ");
        }
        
        prompt.append(":\n\n```\n").append(request.getCode()).append("\n```\n\n");
        
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            prompt.append("Context: ").append(request.getContext()).append("\n\n");
        }
        
        prompt.append("Please provide:\n");
        prompt.append("1. Optimized version of the code\n");
        prompt.append("2. Explanation of optimizations made\n");
        prompt.append("3. Performance improvements expected\n");
        prompt.append("4. Any trade-offs or considerations\n\n");
        prompt.append("Focus on maintainability and readability while improving efficiency.");
        
        return prompt.toString();
    }

    private String buildCodeExplanationPrompt(CodeExplanationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Please explain the following ").append(request.getLanguage() != null ? request.getLanguage() : "").append(" code ");
        
        String level = request.getExplanationLevel() != null ? request.getExplanationLevel().toLowerCase() : "intermediate";
        prompt.append("at a ").append(level).append(" level:\n\n");
        
        prompt.append("```\n").append(request.getCode()).append("\n```\n\n");
        
        prompt.append("Please provide:\n");
        prompt.append("1. High-level overview of what the code does\n");
        prompt.append("2. Step-by-step breakdown of the logic\n");
        prompt.append("3. Explanation of key concepts and techniques used\n");
        prompt.append("4. Purpose and functionality of each major component\n\n");
        
        if ("beginner".equals(level)) {
            prompt.append("Use simple language and explain basic programming concepts as needed.");
        } else if ("advanced".equals(level)) {
            prompt.append("Include advanced technical details and design pattern discussions.");
        }
        
        return prompt.toString();
    }

    private String buildCodeGenerationPrompt(CodeGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate ").append(request.getLanguage() != null ? request.getLanguage() : "").append(" code ");
        prompt.append("for the following requirement:\n\n").append(request.getDescription()).append("\n\n");
        
        if (request.getRequirements() != null && !request.getRequirements().trim().isEmpty()) {
            prompt.append("Specific requirements: ").append(request.getRequirements()).append("\n\n");
        }
        
        String complexity = request.getComplexity() != null ? request.getComplexity().toLowerCase() : "medium";
        prompt.append("Complexity level: ").append(complexity).append("\n\n");
        
        prompt.append("Please provide:\n");
        prompt.append("1. Clean, well-structured code\n");
        prompt.append("2. Appropriate comments and documentation\n");
        prompt.append("3. Error handling where applicable\n");
        prompt.append("4. Best practices for the chosen language\n");
        prompt.append("5. Brief explanation of the approach taken\n\n");
        prompt.append("Make sure the code is production-ready and follows coding standards.");
        
        return prompt.toString();
    }

    // Helper methods for parsing responses
    private ParsedAiResponse parseCodeReviewResponse(String response) {
        ParsedAiResponse parsed = new ParsedAiResponse();
        
        // Extract issues (look for numbered lists or bullet points)
        List<String> issues = extractListItems(response, "issue|problem|error|bug");
        parsed.setIssues(issues);
        
        // Extract suggestions
        List<String> suggestions = extractListItems(response, "suggest|recommend|improve|consider");
        parsed.setSuggestions(suggestions);
        
        // Simple confidence scoring based on response quality
        parsed.setConfidenceScore(calculateConfidenceScore(response));
        
        return parsed;
    }

    private ParsedAiResponse parseBugFixResponse(String response) {
        ParsedAiResponse parsed = new ParsedAiResponse();
        
        // Extract suggested code
        String suggestedCode = extractCodeFromResponse(response);
        parsed.setSuggestedCode(suggestedCode);
        
        // Extract suggestions/explanations
        List<String> suggestions = extractListItems(response, "fix|solution|change|modify");
        parsed.setSuggestions(suggestions);
        
        parsed.setConfidenceScore(calculateConfidenceScore(response));
        
        return parsed;
    }

    private ParsedAiResponse parseOptimizationResponse(String response) {
        ParsedAiResponse parsed = new ParsedAiResponse();
        
        // Extract optimized code
        String optimizedCode = extractCodeFromResponse(response);
        parsed.setSuggestedCode(optimizedCode);
        
        // Extract optimization suggestions
        List<String> suggestions = extractListItems(response, "optimi|improve|enhance|better");
        parsed.setSuggestions(suggestions);
        
        parsed.setConfidenceScore(calculateConfidenceScore(response));
        
        return parsed;
    }

    private String extractCodeFromResponse(String response) {
        // Look for code blocks marked with ```
        Pattern codePattern = Pattern.compile("```(?:[a-zA-Z]*\\n)?(.*?)```", Pattern.DOTALL);
        Matcher matcher = codePattern.matcher(response);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        return null;
    }

    private List<String> extractListItems(String text, String keywords) {
        List<String> items = new ArrayList<>();
        String[] lines = text.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            // Look for numbered lists, bullet points, or lines containing keywords
            if (line.matches("^\\d+\\..*") || line.matches("^[•\\-\\*].*") || 
                line.toLowerCase().matches(".*(" + keywords + ").*")) {
                // Clean up the line
                String cleaned = line.replaceAll("^\\d+\\.|^[•\\-\\*]", "").trim();
                if (!cleaned.isEmpty() && cleaned.length() > 10) {
                    items.add(cleaned);
                }
            }
        }
        
        return items;
    }

    private Integer calculateConfidenceScore(String response) {
        // Simple heuristic based on response quality indicators
        int score = 70; // Base score
        
        if (response.contains("```")) score += 10; // Contains code blocks
        if (response.length() > 500) score += 5; // Detailed response
        if (response.contains("suggest") || response.contains("recommend")) score += 5;
        if (response.contains("error") || response.contains("issue")) score += 5;
        if (response.contains("best practice")) score += 5;
        
        return Math.min(95, score); // Cap at 95%
    }

    // Helper class for parsed AI responses
    private static class ParsedAiResponse {
        private String suggestedCode;
        private List<String> suggestions = new ArrayList<>();
        private List<String> issues = new ArrayList<>();
        private Integer confidenceScore;

        // Getters and setters
        public String getSuggestedCode() { return suggestedCode; }
        public void setSuggestedCode(String suggestedCode) { this.suggestedCode = suggestedCode; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
        public List<String> getIssues() { return issues; }
        public void setIssues(List<String> issues) { this.issues = issues; }
        public Integer getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(Integer confidenceScore) { this.confidenceScore = confidenceScore; }
    }

    private String generateMockCode(CodeGenerationRequest request) {
        String language = request.getLanguage().toLowerCase();
        String description = request.getDescription();
        
        // Generate simple mock code based on language
        switch (language) {
            case "python":
                return "def generated_function():\n    \"\"\"\n    " + description + "\n    \"\"\"\n    # TODO: Implement the logic\n    pass\n\nif __name__ == \"__main__\":\n    generated_function()";
            case "javascript":
                return "function generatedFunction() {\n    // " + description + "\n    // TODO: Implement the logic\n    console.log('Function generated successfully!');\n}\n\n// Call the function\ngeneratedFunction();";
            case "java":
                return "public class GeneratedCode {\n    /**\n     * " + description + "\n     */\n    public static void generatedMethod() {\n        // TODO: Implement the logic\n        System.out.println(\"Method generated successfully!\");\n    }\n    \n    public static void main(String[] args) {\n        generatedMethod();\n    }\n}";
            case "typescript":
                return "function generatedFunction(): void {\n    // " + description + "\n    // TODO: Implement the logic\n    console.log('Function generated successfully!');\n}\n\n// Call the function\ngeneratedFunction();";
            default:
                return "// Generated code for: " + description + "\n// Language: " + language + "\n// TODO: Implement the logic";
        }
    }
}
