import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface AiHistoryItem {
  id: number;
  requestType: string;
  question: string;
  response: string;
  timestamp: Date;
  language?: string;
}

@Component({
  selector: 'app-ai-history',
  imports: [CommonModule, RouterModule],
  templateUrl: './ai-history.component.html',
  styleUrl: './ai-history.component.css'
})
export class AiHistoryComponent implements OnInit {
  historyItems: AiHistoryItem[] = [];
  isLoading = true;

  ngOnInit() {
    this.loadHistory();
  }

  private loadHistory() {
    // TODO: Implement real AI history service
    // For now, show empty history until AI service is implemented
    this.historyItems = [];
    this.isLoading = false;
  }

  deleteItem(id: number) {
    this.historyItems = this.historyItems.filter(item => item.id !== id);
  }

  copyToClipboard(text: string) {
    navigator.clipboard.writeText(text);
  }
}
