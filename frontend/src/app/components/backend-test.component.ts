import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BackendTestService, HealthResponse, TestResponse } from '../services/backend-test.service';

@Component({
  selector: 'app-backend-test',
  imports: [CommonModule],
  template: `
    <div class="max-w-4xl mx-auto p-6 space-y-6">
      <h1 class="text-3xl font-bold text-gray-900 dark:text-white">Backend Connection Test</h1>
      
      <!-- Health Check -->
      <div class="bg-white dark:bg-gray-800 rounded-lg p-6 border border-gray-200 dark:border-gray-700">
        <h2 class="text-xl font-semibold mb-4 text-gray-900 dark:text-white">Health Check</h2>
        <button 
          (click)="checkHealth()"
          [disabled]="healthLoading"
          class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 mr-4">
          {{ healthLoading ? 'Checking...' : 'Check Health' }}
        </button>
        
        <div *ngIf="healthResponse" class="mt-4 p-4 rounded-lg"
             [class]="healthResponse.status === 'healthy' ? 'bg-green-50 border border-green-200 text-green-800' : 'bg-red-50 border border-red-200 text-red-800'">
          <h3 class="font-semibold">Health Status: {{ healthResponse.status }}</h3>
          <p>{{ healthResponse.message }}</p>
          <p class="text-sm mt-2">Timestamp: {{ healthResponse.timestamp }}</p>
        </div>
        
        <div *ngIf="healthError" class="mt-4 p-4 bg-red-50 border border-red-200 text-red-800 rounded-lg">
          <h3 class="font-semibold">Connection Error</h3>
          <p>{{ healthError }}</p>
          <p class="text-sm mt-2">Make sure the backend server is running on port 8080</p>
        </div>
      </div>
      
      <!-- Connection Test -->
      <div class="bg-white dark:bg-gray-800 rounded-lg p-6 border border-gray-200 dark:border-gray-700">
        <h2 class="text-xl font-semibold mb-4 text-gray-900 dark:text-white">Connection Test</h2>
        <button 
          (click)="testConnection()"
          [disabled]="testLoading"
          class="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 disabled:opacity-50 mr-4">
          {{ testLoading ? 'Testing...' : 'Test Connection' }}
        </button>
        
        <div *ngIf="testResponse" class="mt-4 p-4 rounded-lg"
             [class]="testResponse.success ? 'bg-green-50 border border-green-200 text-green-800' : 'bg-red-50 border border-red-200 text-red-800'">
          <h3 class="font-semibold">{{ testResponse.message }}</h3>
          <div *ngIf="testResponse.data" class="mt-2">
            <p><strong>Server:</strong> {{ testResponse.data.server }}</p>
            <p><strong>Version:</strong> {{ testResponse.data.version }}</p>
            <p><strong>Environment:</strong> {{ testResponse.data.environment }}</p>
          </div>
        </div>
        
        <div *ngIf="testError" class="mt-4 p-4 bg-red-50 border border-red-200 text-red-800 rounded-lg">
          <h3 class="font-semibold">Connection Error</h3>
          <p>{{ testError }}</p>
        </div>
      </div>
      
      <!-- Instructions -->
      <div class="bg-blue-50 dark:bg-blue-900/20 rounded-lg p-6 border border-blue-200 dark:border-blue-700">
        <h2 class="text-xl font-semibold mb-4 text-blue-900 dark:text-blue-100">Testing Instructions</h2>
        <ol class="list-decimal list-inside space-y-2 text-blue-800 dark:text-blue-200">
          <li>Make sure the Spring Boot backend is running on port 8080</li>
          <li>The frontend is running on port 4200</li>
          <li>CORS is configured to allow communication between them</li>
          <li>Click the buttons above to test the connection</li>
        </ol>
        
        <div class="mt-4 p-4 bg-white dark:bg-gray-800 rounded border">
          <h3 class="font-semibold mb-2">Expected URLs:</h3>
          <p><strong>Frontend:</strong> http://localhost:4200</p>
          <p><strong>Backend:</strong> http://localhost:8080</p>
          <p><strong>API Health:</strong> http://localhost:8080/api/health</p>
          <p><strong>API Test:</strong> http://localhost:8080/api/test</p>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class BackendTestComponent implements OnInit {
  healthResponse: HealthResponse | null = null;
  testResponse: TestResponse | null = null;
  healthError: string | null = null;
  testError: string | null = null;
  healthLoading = false;
  testLoading = false;

  constructor(private backendTestService: BackendTestService) {}

  ngOnInit() {
    // Automatically check health on component load
    this.checkHealth();
  }

  checkHealth() {
    this.healthLoading = true;
    this.healthError = null;
    this.healthResponse = null;

    this.backendTestService.checkHealth().subscribe({
      next: (response) => {
        this.healthResponse = response;
        this.healthLoading = false;
      },
      error: (error) => {
        this.healthError = `Failed to connect to backend: ${error.message || 'Unknown error'}`;
        this.healthLoading = false;
        console.error('Health check error:', error);
      }
    });
  }

  testConnection() {
    this.testLoading = true;
    this.testError = null;
    this.testResponse = null;

    this.backendTestService.testConnection().subscribe({
      next: (response) => {
        this.testResponse = response;
        this.testLoading = false;
      },
      error: (error) => {
        this.testError = `Failed to test connection: ${error.message || 'Unknown error'}`;
        this.testLoading = false;
        console.error('Connection test error:', error);
      }
    });
  }
}
