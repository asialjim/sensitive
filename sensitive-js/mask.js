/*
 *    Copyright 2014-2025 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/**
 * mask.js - 敏感数据处理和H5标签过滤器
 * 提供敏感数据格式解析和原生H5标签过滤器功能
 */

/**
 * 敏感数据处理核心函数
 * @param {string} data - 可能包含敏感数据格式的字符串
 * @returns {string} - 提取后的掩码内容或原始数据
 */
function mask(data) {
  // 参数验证
  if (typeof data !== 'string') {
    return data;
  }
  
  // 检查是否符合敏感数据格式: _mask|algorithm|nonce|encrypt|mac|mask
  const regex = /^_mask\|([A-Za-z0-9-_=]+)\|([A-Za-z0-9-_=]+)\|([A-Za-z0-9-_=]+)\|([A-Za-z0-9-_=]+)\|(.*)$/;
  const match = data.match(regex);
  
  if (match) {
    // 格式匹配成功，返回最后一部分（mask数据段）
    return match[5];
  }
  
  // 不是敏感数据格式，原样返回
  return data;
}

/**
 * 注册H5标签过滤器
 * 支持在HTML标签中使用 {{ data | mask }} 语法
 */
(function() {
  // 检查是否在浏览器环境
  if (typeof window === 'undefined') {
    return;
  }
  
  // 定义过滤器容器
  window.filters = window.filters || {};
  window.filters.mask = mask;
  
  // 模板解析函数
  function parseTemplates() {
    // 查找所有包含 {{ }} 模板语法的元素
    const elements = document.querySelectorAll('*');
    
    elements.forEach(element => {
      if (element.children.length === 0 && element.nodeType === 3) {
        // 处理文本节点
        processTextNode(element);
      } else {
        // 处理元素的文本内容
        if (element.textContent && element.textContent.includes('{{')) {
          let hasChanges = false;
          let processedContent = element.textContent;
          
          // 匹配所有模板表达式 {{ expression }}
          const templateRegex = /\{\{([^{}]+)\}\}/g;
          
          processedContent = processedContent.replace(templateRegex, (match, expression) => {
            hasChanges = true;
            return evaluateExpression(expression.trim());
          });
          
          if (hasChanges) {
            element.textContent = processedContent;
          }
        }
        
        // 处理属性中的模板
        Array.from(element.attributes).forEach(attr => {
          if (attr.value && attr.value.includes('{{')) {
            let hasChanges = false;
            let processedValue = attr.value;
            
            const templateRegex = /\{\{([^{}]+)\}\}/g;
            
            processedValue = processedValue.replace(templateRegex, (match, expression) => {
              hasChanges = true;
              return evaluateExpression(expression.trim());
            });
            
            if (hasChanges) {
              element.setAttribute(attr.name, processedValue);
            }
          }
        });
      }
    });
  }
  
  // 表达式求值函数，支持过滤器语法
  function evaluateExpression(expression) {
    // 检查是否包含过滤器语法: data | filter1 | filter2
    if (expression.includes('|')) {
      const parts = expression.split('|').map(part => part.trim());
      let result = evaluateData(parts[0]);
      
      // 应用过滤器链
      for (let i = 1; i < parts.length; i++) {
        const filterName = parts[i];
        const filterFn = window.filters[filterName];
        
        if (typeof filterFn === 'function') {
          result = filterFn(result);
        }
      }
      
      return result;
    }
    
    // 直接求值
    return evaluateData(expression);
  }
  
  // 数据求值函数
  function evaluateData(dataExpr) {
    try {
      // 尝试从window对象获取数据
      // 简单实现：支持点号访问，如 user.name
      if (dataExpr.includes('.')) {
        const keys = dataExpr.split('.');
        let value = window;
        
        for (const key of keys) {
          if (value && typeof value === 'object' && key in value) {
            value = value[key];
          } else {
            return '';
          }
        }
        
        return value !== undefined ? value : '';
      } else {
        // 直接访问window属性
        return window[dataExpr] !== undefined ? window[dataExpr] : '';
      }
    } catch (e) {
      console.error('Error evaluating expression:', e);
      return '';
    }
  }
  
  // 处理文本节点
  function processTextNode(node) {
    if (node.textContent && node.textContent.includes('{{')) {
      let processedContent = node.textContent;
      const templateRegex = /\{\{([^{}]+)\}\}/g;
      
      processedContent = processedContent.replace(templateRegex, (match, expression) => {
        return evaluateExpression(expression.trim());
      });
      
      node.textContent = processedContent;
    }
  }
  
  // DOM加载完成后执行解析
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', parseTemplates);
  } else {
    // 如果DOM已加载，立即执行
    setTimeout(parseTemplates, 0);
  }
  
  // 提供手动触发解析的方法
  window.parseTemplates = parseTemplates;
})();

// 多环境导出
if (typeof module !== 'undefined' && module.exports) {
  // Node.js 环境
  module.exports = {
    mask: mask
  };
} else if (typeof define === 'function' && define.amd) {
  // AMD 环境
  define([], function() {
    return {
      mask: mask
    };
  });
} else if (typeof window !== 'undefined') {
  // 浏览器全局变量
  window.mask = mask;
}
