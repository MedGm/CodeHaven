export interface CodeReviewRequest {
  code: string;
  language: string;
  context?: string;
  reviewType?: 'SECURITY' | 'PERFORMANCE' | 'BEST_PRACTICES' | 'GENERAL';
}

export interface BugFixRequest {
  code: string;
  language: string;
  errorDescription: string;
  expectedBehavior?: string;
  context?: string;
}

export interface CodeOptimizationRequest {
  code: string;
  language: string;
  optimizationType?: 'PERFORMANCE' | 'MEMORY' | 'READABILITY' | 'GENERAL';
  context?: string;
}

export interface CodeExplanationRequest {
  code: string;
  language: string;
  explanationLevel?: 'BEGINNER' | 'INTERMEDIATE' | 'ADVANCED';
  context?: string;
}

export interface CodeGenerationRequest {
  description: string;
  language: string;
  codeType?: 'FUNCTION' | 'CLASS' | 'API' | 'COMPONENT';
  requirements?: string;
  framework?: string;
  complexity?: 'SIMPLE' | 'MEDIUM' | 'COMPLEX';
}

export interface AiResponse {
  id: number;
  requestType: string;
  response: string;
  originalCode?: string;
  suggestedCode?: string;
  suggestions?: string[];
  issues?: string[];
  confidenceScore?: number;
  language: string;
  username: string;
  createdAt: string | number[]; // Handle both string and array formats
  processingTimeMs?: number;
}

export interface AiRequest {
  id: number;
  user: any;
  requestType: 'CODE_REVIEW' | 'BUG_FIX' | 'CODE_OPTIMIZATION' | 'CODE_EXPLANATION' | 'CODE_GENERATION';
  inputData: string;
  response: string;
  createdAt: Date;
}
