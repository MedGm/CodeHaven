-- Initial data for CodeHaven application

-- Insert test users (if they don't exist)
INSERT INTO users (id, username, email, password, first_name, last_name, created_at, updated_at)
VALUES 
    (1, 'testuser', 'test@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Test', 'User', NOW(), NOW()),
    (2, 'snippetdev', 'snippetdev@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Snippet', 'Developer', NOW(), NOW()),
    (3, 'codemaster', 'codemaster@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Code', 'Master', NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- Insert test snippets
INSERT INTO snippets (id, title, description, code, language, is_public, views, likes, user_id, created_at, updated_at)
VALUES 
    (1, 'React useState Hook Example', 'A comprehensive example of using the useState hook in React', 
     'import React, { useState } from ''react'';

function Counter() {
  const [count, setCount] = useState(0);

  return (
    <div>
      <p>You clicked {count} times</p>
      <button onClick={() => setCount(count + 1)}>
        Click me
      </button>
    </div>
  );
}

export default Counter;', 
     'javascript', true, 0, 0, 1, NOW(), NOW()),
     
    (2, 'Python Data Processing', 'Efficient data processing with pandas', 
     'import pandas as pd
import numpy as np

def process_data(file_path):
    df = pd.read_csv(file_path)
    df.dropna(inplace=True)
    return df.describe()', 
     'python', true, 0, 0, 2, NOW(), NOW()),
     
    (3, 'Java Spring Controller', 'REST API controller example', 
     '@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping
    public List<User> getUsers() {
        return userService.findAll();
    }
}', 
     'java', true, 0, 0, 3, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Insert tags for snippets
INSERT INTO snippet_tags (snippet_id, tag)
VALUES 
    (1, 'react'),
    (1, 'hooks'),
    (1, 'javascript'),
    (2, 'python'),
    (2, 'pandas'),
    (2, 'data-processing'),
    (3, 'java'),
    (3, 'spring-boot'),
    (3, 'rest-api')
ON CONFLICT DO NOTHING;

-- Reset sequences to avoid conflicts
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('snippets_id_seq', (SELECT MAX(id) FROM snippets));
