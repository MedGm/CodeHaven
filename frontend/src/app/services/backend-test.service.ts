import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface HealthResponse {
  status: string;
  message: string;
  timestamp: string;
}

export interface TestResponse {
  success: boolean;
  message: string;
  data: {
    server: string;
    version: string;
    environment: string;
  };
}

@Injectable({
  providedIn: 'root'
})
export class BackendTestService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  checkHealth(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>(`${this.apiUrl}/health`);
  }

  testConnection(): Observable<TestResponse> {
    return this.http.get<TestResponse>(`${this.apiUrl}/test`);
  }
}
