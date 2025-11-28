# mask.js - React框架使用指南

## 概述

本文档提供了在React框架中使用mask.js工具处理敏感数据的详细指导。mask.js是一个轻量级的原生JavaScript工具，专门用于处理敏感数据的显示，而React集成方案则为React应用提供了更便捷的使用方式。

## 文件结构

```
sensitive/
├── mask.js           # 核心工具文件
├── README-js.md      # 原生JavaScript使用文档
├── README-vue.md     # Vue框架使用文档
├── README-react.md   # React框架使用文档 (当前文档)
└── README-wx-applet.md # 微信小程序使用文档
```

## 特性

- **自定义Hook支持**：使用`useSensitiveData` Hook轻松集成到函数组件
- **React组件封装**：提供`SensitiveText`组件，使用更加声明式
- **TypeScript支持**：完整的TypeScript类型定义
- **性能优化**：使用`useCallback`和`memo`避免不必要的重渲染
- **灵活配置**：支持自定义处理逻辑和全局配置

## 安装与引入

### 方法一：直接引入

在HTML中直接引入mask.js文件：

```html
<!-- 引入mask.js -->
<script src="path/to/mask.js"></script>
```

### 方法二：NPM安装（推荐）

```bash
# 将sensitive目录下的文件复制到你的项目中
# 然后在你的项目中引入
```

## React Hooks 方式

### 基础Hook使用

创建一个自定义Hook来处理敏感数据：

```jsx
// hooks/useSensitiveData.js
import { useCallback } from 'react';
import { mask } from '../path/to/mask.js';

/**
 * 敏感数据处理Hook
 * @returns {Object} 包含处理函数的对象
 */
export function useSensitiveData() {
  // 使用useCallback缓存函数，避免不必要的重渲染
  const processData = useCallback((data) => {
    return mask(data);
  }, []);

  // 处理对象中的多个敏感字段
  const processObjectFields = useCallback((obj, fields) => {
    if (!obj || typeof obj !== 'object') return obj;
    
    const result = { ...obj };
    fields.forEach(field => {
      if (result[field] !== undefined) {
        result[field] = mask(result[field]);
      }
    });
    return result;
  }, []);

  return {
    mask: processData,
    processObjectFields
  };
}
```

在组件中使用：

```jsx
// components/UserProfile.jsx
import React, { useState, useEffect } from 'react';
import { useSensitiveData } from '../hooks/useSensitiveData';

const UserProfile = ({ userId }) => {
  const [user, setUser] = useState(null);
  const { mask, processObjectFields } = useSensitiveData();

  useEffect(() => {
    // 模拟从API获取数据
    fetch(`/api/users/${userId}`)
      .then(res => res.json())
      .then(data => {
        // 使用自定义Hook处理敏感数据
        const processedUser = processObjectFields(data, ['name', 'phone', 'email', 'idCard']);
        setUser(processedUser);
      });
  }, [userId, processObjectFields]);

  if (!user) return <div>Loading...</div>;

  return (
    <div className="user-profile">
      <h2>用户信息</h2>
      <p>用户名: {user.name}</p>
      <p>手机号: {user.phone}</p>
      <p>邮箱: {user.email}</p>
      <p>身份证号: {user.idCard}</p>
      
      {/* 也可以单独处理某个字段 */}
      <p>地址: {mask(user.address)}</p>
    </div>
  );
};

export default UserProfile;
```

### 列表数据处理

处理列表中的敏感数据：

```jsx
// components/UserList.jsx
import React, { useMemo } from 'react';
import { useSensitiveData } from '../hooks/useSensitiveData';

const UserList = ({ users }) => {
  const { processObjectFields } = useSensitiveData();

  // 使用useMemo缓存处理后的列表，避免每次渲染都重新处理
  const processedUsers = useMemo(() => {
    return users.map(user => 
      processObjectFields(user, ['name', 'phone'])
    );
  }, [users, processObjectFields]);

  return (
    <div className="user-list">
      <h2>用户列表</h2>
      <ul>
        {processedUsers.map(user => (
          <li key={user.id}>
            {user.name} - {user.phone}
          </li>
        ))}
      </ul>
    </div>
  );
};

// 使用React.memo避免不必要的重渲染
export default React.memo(UserList);
```

## React 组件方式

创建专门处理敏感数据的组件，使用更加声明式：

```jsx
// components/SensitiveText.jsx
import React, { memo } from 'react';
import { mask } from '../path/to/mask.js';

/**
 * 敏感文本显示组件
 * @param {Object} props - 组件属性
 * @param {string} props.text - 要显示的文本（可能是敏感数据格式）
 * @param {string} props.tag - 要渲染的HTML标签，默认为span
 * @param {Object} props.className - CSS类名
 * @param {Object} props.style - 样式对象
 * @param {Object} props.attributes - 其他HTML属性
 */
const SensitiveText = ({ 
  text, 
  tag = 'span', 
  className,
  style,
  ...attributes 
}) => {
  // 处理文本内容
  const processedText = mask(text);

  // 动态创建标签
  const Tag = tag;

  return (
    <Tag 
      className={className} 
      style={style}
      {...attributes}
    >
      {processedText}
    </Tag>
  );
};

// 使用memo优化性能
export default memo(SensitiveText);
```

在应用中使用：

```jsx
// App.jsx
import React, { useState } from 'react';
import SensitiveText from './components/SensitiveText';

function App() {
  const [userData] = useState({
    name: '_mask|1|a1b2c3|d4e5f6|789abc|张*',
    phone: '_mask|1|a1b2c3|d4e5f6|789abc|138****1234',
    email: '_mask|1|a1b2c3|d4e5f6|789abc|exa****@example.com',
    idCard: '_mask|1|a1b2c3|d4e5f6|789abc|1101**********1234'
  });

  return (
    <div className="app">
      <h1>敏感数据处理示例</h1>
      
      <div className="user-info">
        <h2>用户信息</h2>
        
        {/* 使用默认span标签 */}
        <p>用户名: <SensitiveText text={userData.name} /></p>
        
        {/* 指定不同的标签 */}
        <p>手机号: <SensitiveText text={userData.phone} tag="strong" /></p>
        
        {/* 添加样式和类名 */}
        <p>
          邮箱: 
          <SensitiveText 
            text={userData.email} 
            className="email-text"
            style={{ color: '#0066cc' }}
          />
        </p>
        
        {/* 添加自定义属性 */}
        <p>
          身份证号: 
          <SensitiveText 
            text={userData.idCard} 
            title="已脱敏显示"
            data-testid="sensitive-idcard"
          />
        </p>
      </div>
    </div>
  );
}

export default App;
```

## 高级用法

### 1. 创建上下文Provider

对于大型应用，可以创建一个上下文Provider来全局配置敏感数据处理：

```jsx
// context/SensitiveDataContext.jsx
import React, { createContext, useContext, useCallback } from 'react';
import { mask } from '../path/to/mask.js';

// 创建上下文
const SensitiveDataContext = createContext(null);

/**
 * 敏感数据上下文Provider组件
 * @param {Object} props - 组件属性
 * @param {React.ReactNode} props.children - 子组件
 * @param {Object} props.config - 配置对象
 */
export const SensitiveDataProvider = ({ children, config = {} }) => {
  // 自定义处理函数
  const processData = useCallback((data) => {
    // 可以根据config进行不同的处理逻辑
    return mask(data);
  }, [config]);

  // 上下文值
  const contextValue = {
    mask: processData,
    config,
    // 可以添加更多工具函数
    processUserData: useCallback((user) => {
      if (!user) return user;
      return {
        ...user,
        name: processData(user.name),
        phone: processData(user.phone),
        email: processData(user.email)
      };
    }, [processData])
  };

  return (
    <SensitiveDataContext.Provider value={contextValue}>
      {children}
    </SensitiveDataContext.Provider>
  );
};

// 自定义Hook用于访问上下文
export const useSensitiveContext = () => {
  const context = useContext(SensitiveDataContext);
  if (!context) {
    throw new Error('useSensitiveContext must be used within a SensitiveDataProvider');
  }
  return context;
};
```

在应用中使用：

```jsx
// index.jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { SensitiveDataProvider } from './context/SensitiveDataContext';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <SensitiveDataProvider config={{ /* 配置项 */ }}>
      <App />
    </SensitiveDataProvider>
  </React.StrictMode>
);

// 在组件中使用
import { useSensitiveContext } from '../context/SensitiveDataContext';

const MyComponent = () => {
  const { mask, processUserData } = useSensitiveContext();
  
  // 使用上下文提供的函数
  // ...
};
```

### 2. 创建高阶组件(HOC)

为现有组件添加敏感数据处理能力：

```jsx
// HOCs/withSensitiveData.jsx
import React from 'react';
import { mask } from '../path/to/mask.js';

/**
 * 为组件添加敏感数据处理能力的高阶组件
 * @param {React.ComponentType} WrappedComponent - 要包装的组件
 * @param {string[]} sensitiveFields - 需要处理的敏感字段名数组
 * @returns {React.ComponentType} 增强后的组件
 */
const withSensitiveData = (WrappedComponent, sensitiveFields = []) => {
  return function WithSensitiveData(props) {
    // 处理敏感字段
    const processedProps = React.useMemo(() => {
      if (sensitiveFields.length === 0) return props;
      
      const result = { ...props };
      sensitiveFields.forEach(field => {
        if (result[field] !== undefined) {
          result[field] = mask(result[field]);
        }
      });
      return result;
    }, [props, sensitiveFields]);

    return <WrappedComponent {...processedProps} />;
  };
};

export default withSensitiveData;
```

使用示例：

```jsx
// components/DisplayCard.jsx
import React from 'react';
import withSensitiveData from '../HOCs/withSensitiveData';

const DisplayCard = ({ title, content, sensitiveInfo }) => {
  return (
    <div className="card">
      <h3>{title}</h3>
      <p>{content}</p>
      {sensitiveInfo && <p className="sensitive">{sensitiveInfo}</p>}
    </div>
  );
};

// 包装组件，指定需要处理的敏感字段
export default withSensitiveData(DisplayCard, ['sensitiveInfo']);

// 使用时，敏感字段会自动处理
<DisplayCard 
  title="用户信息" 
  content="这是用户信息卡片" 
  sensitiveInfo="_mask|1|a1b2c3|d4e5f6|789abc|敏感内容已脱敏" 
/>
```

### 3. TypeScript 支持

为工具添加TypeScript类型定义：

```typescript
// types/mask.d.ts
declare function mask(data: string): string;

export { mask };

// types/react.d.ts
import { FC, PropsWithChildren } from 'react';

export interface SensitiveTextProps {
  text: string;
  tag?: string;
  className?: string;
  style?: React.CSSProperties;
  [key: string]: any;
}

export const SensitiveText: FC<SensitiveTextProps>;

export function useSensitiveData(): {
  mask: (data: string) => string;
  processObjectFields: <T extends Record<string, any>>(obj: T, fields: (keyof T)[]) => T;
};

interface SensitiveDataContextValue {
  mask: (data: string) => string;
  config: Record<string, any>;
  processUserData: <T extends Record<string, any>>(user: T) => T;
}

export const SensitiveDataProvider: FC<PropsWithChildren<{
  config?: Record<string, any>;
}>>;

export function useSensitiveContext(): SensitiveDataContextValue;

export function withSensitiveData<P extends Record<string, any>>(
  WrappedComponent: FC<P>,
  sensitiveFields?: Array<keyof P>
): FC<P>;
```

## 最佳实践

### 1. 数据预加载时处理

在数据获取阶段就处理敏感数据，而不是在渲染时处理：

```jsx
// services/userService.js
import { mask } from '../path/to/mask.js';

// 服务层处理敏感数据
export async function getUserProfile(userId) {
  const response = await fetch(`/api/users/${userId}`);
  const data = await response.json();
  
  // 在服务层预处理敏感数据
  return {
    ...data,
    name: mask(data.name),
    phone: mask(data.phone),
    email: mask(data.email)
  };
}

// 在组件中直接使用处理后的数据
const UserProfile = () => {
  const [user, setUser] = useState(null);
  
  useEffect(() => {
    getUserProfile(123).then(setUser);
  }, []);
  
  // 直接使用，不需要在渲染时再处理
  return user ? <p>手机号: {user.phone}</p> : null;
};
```

### 2. 避免不必要的渲染

使用`useMemo`和`useCallback`优化性能：

```jsx
const OptimizedComponent = ({ data }) => {
  // 使用useCallback缓存mask函数
  const maskData = useCallback((value) => {
    return mask(value);
  }, []);
  
  // 使用useMemo缓存处理后的结果
  const processedData = useMemo(() => {
    if (!data) return null;
    return {
      ...data,
      sensitiveField1: maskData(data.sensitiveField1),
      sensitiveField2: maskData(data.sensitiveField2)
    };
  }, [data, maskData]);
  
  return (
    <div>
      {processedData && (
        <>
          <p>{processedData.sensitiveField1}</p>
          <p>{processedData.sensitiveField2}</p>
        </>
      )}
    </div>
  );
};
```

### 3. 集中式数据处理

对于大型应用，在状态管理层面处理敏感数据：

```jsx
// 使用Redux Toolkit
import { createSlice } from '@reduxjs/toolkit';
import { mask } from '../path/to/mask.js';

const userSlice = createSlice({
  name: 'user',
  initialState: null,
  reducers: {
    setUser: (state, action) => {
      // 在reducer中处理敏感数据
      const userData = action.payload;
      return {
        ...userData,
        name: mask(userData.name),
        phone: mask(userData.phone),
        email: mask(userData.email)
      };
    }
  }
});
```

### 4. 条件渲染和错误处理

添加条件渲染和错误处理，提高应用稳定性：

```jsx
const SafeSensitiveText = ({ text, fallback = '' }) => {
  try {
    if (!text) return fallback;
    return <span>{mask(String(text))}</span>;
  } catch (error) {
    console.error('处理敏感数据时出错:', error);
    return <span>{fallback}</span>;
  }
};
```

## 常见问题与解决方案

### 1. 性能问题

**问题**：在大型列表中使用时，性能较差。

**解决方案**：
- 使用`React.memo`包装组件
- 使用`useMemo`缓存处理结果
- 在数据获取层预处理敏感数据
- 避免在循环中直接调用处理函数

### 2. 状态更新后数据未刷新

**问题**：当原始数据更新时，敏感数据没有重新处理。

**解决方案**：确保依赖项数组中包含所有相关数据：

```jsx
// 正确做法
const processedData = useMemo(() => {
  return mask(rawData);
}, [rawData]); // 包含rawData作为依赖

// 不正确做法 - 会导致数据不更新
const processedData = mask(rawData); // 每次渲染都会重新执行
```

### 3. 与服务器端渲染(SSR)的兼容性

**问题**：在Next.js等SSR框架中使用时出现问题。

**解决方案**：确保代码在服务器和客户端都能正常运行：

```jsx
// 在Next.js中使用
import dynamic from 'next/dynamic';

// 动态导入，仅在客户端渲染
const SensitiveText = dynamic(() => import('../components/SensitiveText'), {
  ssr: false
});

// 或者确保mask.js在服务器端也能正常工作
// 在组件中添加条件判断
if (typeof window !== 'undefined') {
  // 客户端特定代码
}
```

## 版本信息

- **版本**: 1.0.0
- **支持的React版本**: React 16.8+ (支持Hooks)
- **特性**: React自定义Hook和组件支持的敏感数据处理
- **作者**: Generated by AI Assistant
- **日期**: 2024-01-15
