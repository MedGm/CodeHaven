package com.codehaven.backend.application.dto.mapper;

import com.codehaven.backend.application.dto.ai.AiResponseDto;
import com.codehaven.backend.domain.model.AiRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiMapper {
    
    private final ObjectMapper objectMapper;
    
    public AiResponseDto toDto(AiRequest aiRequest) {
        Map<String, Object> inputData = parseInputData(aiRequest.getInputData());
        Map<String, Object> responseData = parseResponseData(aiRequest.getResponseData());
        
        return AiResponseDto.builder()
                .id(aiRequest.getId())
                .requestType(aiRequest.getType().name())
                .response(aiRequest.getResponseData())
                .originalCode(extractFromMap(inputData, "code", String.class))
                .suggestedCode(extractFromMap(responseData, "suggestedCode", String.class))
                .suggestions(extractListFromMap(responseData, "suggestions"))
                .issues(extractListFromMap(responseData, "issues"))
                .confidenceScore(extractFromMap(responseData, "confidenceScore", Integer.class))
                .language(extractFromMap(inputData, "language", String.class))
                .username(aiRequest.getUser().getUsername())
                .createdAt(aiRequest.getCreatedAt())
                .processingTimeMs(aiRequest.getProcessingTimeMs())
                .build();
    }
    
    public String createInputData(Object request, String requestType) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("requestType", requestType);
            data.put("request", request);
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error serializing input data", e);
            return "{}";
        }
    }
    
    public String createResponseData(String response, String suggestedCode, List<String> suggestions, 
                                   List<String> issues, Integer confidenceScore) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("response", response);
            if (suggestedCode != null) {
                data.put("suggestedCode", suggestedCode);
            }
            if (suggestions != null) {
                data.put("suggestions", suggestions);
            }
            if (issues != null) {
                data.put("issues", issues);
            }
            if (confidenceScore != null) {
                data.put("confidenceScore", confidenceScore);
            }
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.error("Error serializing response data", e);
            return response;
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseInputData(String inputData) {
        try {
            if (inputData == null) return new HashMap<>();
            return objectMapper.readValue(inputData, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing input data", e);
            return new HashMap<>();
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseResponseData(String responseData) {
        try {
            if (responseData == null) return new HashMap<>();
            return objectMapper.readValue(responseData, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error parsing response data", e);
            return new HashMap<>();
        }
    }
    
    private <T> T extractFromMap(Map<String, Object> map, String key, Class<T> type) {
        Object value = map.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private List<String> extractListFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        return new ArrayList<>();
    }
}
