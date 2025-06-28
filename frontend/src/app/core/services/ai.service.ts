import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { 
  CodeReviewRequest, 
  BugFixRequest, 
  CodeOptimizationRequest, 
  CodeExplanationRequest, 
  CodeGenerationRequest,
  AiResponse,
  AiRequest
} from '../models/ai.models';
import { ApiResponse, PaginatedResponse } from '../models/common.models';

@Injectable({
  providedIn: 'root'
})
export class AiService {

  constructor(private http: HttpClient) { }

  codeReview(request: CodeReviewRequest): Observable<AiResponse> {
    return this.http.post<AiResponse>(`${environment.apiUrl}/ai/review`, request);
  }

  bugFix(request: BugFixRequest): Observable<AiResponse> {
    return this.http.post<AiResponse>(`${environment.apiUrl}/ai/bug-fix`, request);
  }

  optimizeCode(request: CodeOptimizationRequest): Observable<AiResponse> {
    return this.http.post<AiResponse>(`${environment.apiUrl}/ai/optimize`, request);
  }

  explainCode(request: CodeExplanationRequest): Observable<AiResponse> {
    return this.http.post<AiResponse>(`${environment.apiUrl}/ai/explain`, request);
  }

  generateCode(request: CodeGenerationRequest): Observable<AiResponse> {
    return this.http.post<AiResponse>(`${environment.apiUrl}/ai/generate`, request);
  }

  getAiHistory(page = 0, size = 10): Observable<ApiResponse<PaginatedResponse<AiRequest>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PaginatedResponse<AiRequest>>>(`${environment.apiUrl}/ai/history`, { params });
  }

  getAiRequest(id: number): Observable<ApiResponse<AiRequest>> {
    return this.http.get<ApiResponse<AiRequest>>(`${environment.apiUrl}/ai/history/${id}`);
  }
}
