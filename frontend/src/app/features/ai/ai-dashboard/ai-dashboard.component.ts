import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-ai-dashboard',
  imports: [CommonModule, RouterModule],
  templateUrl: './ai-dashboard.component.html',
  styleUrl: './ai-dashboard.component.css'
})
export class AiDashboardComponent {
  aiFeatures = [
    {
      title: 'Code Review',
      description: 'Get AI-powered code reviews with suggestions for improvements',
      icon: 'review',
      route: '/ai/code-review',
      color: 'blue'
    },
    {
      title: 'Bug Fix',
      description: 'Identify and fix bugs in your code automatically',
      icon: 'bug',
      route: '/ai/bug-fix',
      color: 'red'
    },
    {
      title: 'Code Generation',
      description: 'Generate code snippets and functions from descriptions',
      icon: 'generate',
      route: '/ai/code-generation',
      color: 'green'
    },
    {
      title: 'Code Optimization',
      description: 'Optimize your code for better performance and readability',
      icon: 'optimize',
      route: '/ai/optimize',
      color: 'purple'
    },
    {
      title: 'Code Explanation',
      description: 'Get detailed explanations of complex code',
      icon: 'explain',
      route: '/ai/explain',
      color: 'orange'
    },
    {
      title: 'AI History',
      description: 'View your past AI interactions and results',
      icon: 'history',
      route: '/ai/history',
      color: 'gray'
    }
  ];

  getIconPath(iconType: string): string {
    const paths: { [key: string]: string } = {
      review: `M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z`,
      bug: `M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z`,
      generate: `M12 6v6m0 0v6m0-6h6m-6 0H6`,
      optimize: `M13 10V3L4 14h7v7l9-11h-7z`,
      explain: `M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z`,
      history: `M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z`
    };
    return paths[iconType] || paths['review'];
  }

  getIconSvg(iconType: string): string {
    const icons: { [key: string]: string } = {
      review: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>`,
      bug: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z"/>`,
      generate: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>`,
      optimize: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>`,
      explain: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>`,
      history: `<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>`
    };
    return icons[iconType] || icons['review'];
  }

  getColorClasses(color: string): { bg: string; text: string; hover: string } {
    const colors: { [key: string]: { bg: string; text: string; hover: string } } = {
      blue: { 
        bg: 'bg-blue-50 dark:bg-blue-900/20', 
        text: 'text-blue-600 dark:text-blue-400', 
        hover: 'hover:bg-blue-100 dark:hover:bg-blue-900/30' 
      },
      red: { 
        bg: 'bg-red-50 dark:bg-red-900/20', 
        text: 'text-red-600 dark:text-red-400', 
        hover: 'hover:bg-red-100 dark:hover:bg-red-900/30' 
      },
      green: { 
        bg: 'bg-green-50 dark:bg-green-900/20', 
        text: 'text-green-600 dark:text-green-400', 
        hover: 'hover:bg-green-100 dark:hover:bg-green-900/30' 
      },
      purple: { 
        bg: 'bg-purple-50 dark:bg-purple-900/20', 
        text: 'text-purple-600 dark:text-purple-400', 
        hover: 'hover:bg-purple-100 dark:hover:bg-purple-900/30' 
      },
      orange: { 
        bg: 'bg-orange-50 dark:bg-orange-900/20', 
        text: 'text-orange-600 dark:text-orange-400', 
        hover: 'hover:bg-orange-100 dark:hover:bg-orange-900/30' 
      },
      gray: { 
        bg: 'bg-gray-50 dark:bg-gray-800', 
        text: 'text-gray-600 dark:text-gray-400', 
        hover: 'hover:bg-gray-100 dark:hover:bg-gray-700' 
      }
    };
    return colors[color] || colors['blue'];
  }
}
