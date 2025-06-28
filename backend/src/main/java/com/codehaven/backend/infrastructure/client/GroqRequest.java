package com.codehaven.backend.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GroqRequest {
    
    private String model = "llama3-8b-8192"; // Default Groq model
    private List<Message> messages;
    private Double temperature = 0.7;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens = 1000;
    
    private String user;
    
    @Data
    public static class Message {
        private String role;
        private String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
        
        public static Message system(String content) {
            return new Message("system", content);
        }
        
        public static Message user(String content) {
            return new Message("user", content);
        }
        
        public static Message assistant(String content) {
            return new Message("assistant", content);
        }
    }

    @Override
    public String toString() {
        return "GroqRequest{" +
                "model='" + model + '\'' +
                ", messages=" + messages +
                ", temperature=" + temperature +
                ", maxTokens=" + maxTokens +
                ", user='" + user + '\'' +
                '}';
    }
}
