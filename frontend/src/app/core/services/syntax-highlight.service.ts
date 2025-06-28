import { Injectable } from '@angular/core';
import hljs from 'highlight.js';

@Injectable({
  providedIn: 'root'
})
export class SyntaxHighlightService {
  private initialized = false;

  constructor() {
    this.initializeHighlighting();
  }

  private initializeHighlighting(): void {
    if (this.initialized) return;

    // Configure highlight.js with common languages
    hljs.configure({
      languages: [
        'javascript', 'typescript', 'python', 'java', 'csharp', 'cpp', 'c',
        'html', 'css', 'scss', 'json', 'xml', 'yaml', 'markdown',
        'sql', 'bash', 'shell', 'dockerfile', 'nginx', 'apache',
        'php', 'ruby', 'go', 'rust', 'kotlin', 'swift', 'dart',
        'r', 'matlab', 'perl', 'lua', 'vim', 'powershell'
      ],
      classPrefix: 'hljs-',
      ignoreUnescapedHTML: true
    });

    this.initialized = true;
  }

  /**
   * Highlight code with automatic language detection
   */
  highlightAuto(code: string): { value: string; language?: string } {
    this.initializeHighlighting();
    
    try {
      const result = hljs.highlightAuto(code);
      return {
        value: result.value,
        language: result.language
      };
    } catch (error) {
      console.warn('Error in auto-highlighting:', error);
      return { value: this.escapeHtml(code) };
    }
  }

  /**
   * Highlight code with specified language
   */
  highlight(code: string, language: string): { value: string; language: string } {
    this.initializeHighlighting();
    
    try {
      // Normalize language names
      const normalizedLanguage = this.normalizeLanguage(language);
      
      if (hljs.getLanguage(normalizedLanguage)) {
        const result = hljs.highlight(code, { language: normalizedLanguage });
        return {
          value: result.value,
          language: normalizedLanguage
        };
      } else {
        // Fallback to auto-detection if language not supported
        const autoResult = this.highlightAuto(code);
        return {
          value: autoResult.value,
          language: autoResult.language || language
        };
      }
    } catch (error) {
      console.warn(`Error highlighting ${language}:`, error);
      return { value: this.escapeHtml(code), language };
    }
  }

  /**
   * Get list of supported languages
   */
  getSupportedLanguages(): string[] {
    return hljs.listLanguages().sort();
  }

  /**
   * Check if a language is supported
   */
  isLanguageSupported(language: string): boolean {
    const normalizedLanguage = this.normalizeLanguage(language);
    return hljs.getLanguage(normalizedLanguage) !== undefined;
  }

  /**
   * Get language display name
   */
  getLanguageDisplayName(language: string): string {
    const languageMap: { [key: string]: string } = {
      'javascript': 'JavaScript',
      'typescript': 'TypeScript',
      'python': 'Python',
      'java': 'Java',
      'csharp': 'C#',
      'cpp': 'C++',
      'c': 'C',
      'html': 'HTML',
      'css': 'CSS',
      'scss': 'SCSS',
      'json': 'JSON',
      'xml': 'XML',
      'yaml': 'YAML',
      'markdown': 'Markdown',
      'sql': 'SQL',
      'bash': 'Bash',
      'shell': 'Shell',
      'dockerfile': 'Dockerfile',
      'php': 'PHP',
      'ruby': 'Ruby',
      'go': 'Go',
      'rust': 'Rust',
      'kotlin': 'Kotlin',
      'swift': 'Swift',
      'dart': 'Dart'
    };

    return languageMap[language.toLowerCase()] || language.charAt(0).toUpperCase() + language.slice(1);
  }

  /**
   * Get language color for UI display
   */
  getLanguageColor(language: string): string {
    const colorMap: { [key: string]: string } = {
      'javascript': '#f7df1e',
      'typescript': '#3178c6',
      'python': '#3776ab',
      'java': '#f89820',
      'csharp': '#512bd4',
      'cpp': '#f34b7d',
      'c': '#a8b9cc',
      'html': '#e34c26',
      'css': '#1572b6',
      'scss': '#cf649a',
      'json': '#292929',
      'xml': '#ff6600',
      'yaml': '#cb171e',
      'markdown': '#083fa1',
      'sql': '#336791',
      'bash': '#89e051',
      'shell': '#89e051',
      'dockerfile': '#384d54',
      'php': '#777bb4',
      'ruby': '#cc342d',
      'go': '#00add8',
      'rust': '#dea584',
      'kotlin': '#a97bff',
      'swift': '#fa7343',
      'dart': '#0175c2'
    };

    return colorMap[language.toLowerCase()] || '#6b7280';
  }

  private normalizeLanguage(language: string): string {
    const languageMap: { [key: string]: string } = {
      'js': 'javascript',
      'ts': 'typescript',
      'py': 'python',
      'cs': 'csharp',
      'c++': 'cpp',
      'c#': 'csharp',
      'yml': 'yaml',
      'sh': 'shell',
      'md': 'markdown'
    };

    const normalized = language.toLowerCase().trim();
    return languageMap[normalized] || normalized;
  }

  private escapeHtml(text: string): string {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }
}
