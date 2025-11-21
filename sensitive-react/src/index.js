/**
 * 敏感数据处理库 - sensitive-react
 * 提供React环境下的MaskData组件，用于处理_mask格式敏感数据
 */

// 版本信息
const version = '1.0.0';

/**
 * 解析_mask格式的数据
 * @param {string|*} data - 可能包含_mask格式的数据
 * @returns {string} 解析后的数据（如果是_mask格式则返回mask部分，否则返回原始数据）
 */
function parseMaskData(data) {
  // 检查数据是否为字符串类型
  if (typeof data !== 'string') {
    return String(data);
  }
  
  // 检查是否符合_mask格式：_mask|algorithm_flag|nonce_hex|encrypt_hex|mac_hex|mask
  const maskPattern = /^_mask\|([^|]+)\|([^|]+)\|([^|]+)\|([^|]+)\|(.+)$/;
  const match = data.match(maskPattern);
  
  if (match) {
    // 如果匹配成功，返回最后一个部分（mask部分）
    return match[5];
  }
  
  // 如果不是_mask格式，返回原始数据
  return data;
}

/**
 * 处理页面中所有的MaskData标签（非React管理的）
 */
function processRawMaskDataTags() {
  // 获取所有MaskData标签
  const maskDataTags = document.querySelectorAll('MaskData');
  
  maskDataTags.forEach(tag => {
    // 检查是否已经被React处理过
    if (!tag.hasAttribute('data-reactroot') && !tag.hasAttribute('data-reactid')) {
      const content = tag.textContent.trim();
      const parsedContent = parseMaskData(content);
      
      // 创建文本节点来替换标签
      const textNode = document.createTextNode(parsedContent);
      tag.parentNode.replaceChild(textNode, tag);
    }
  });
}

/**
 * React MaskData组件
 * 用于在React应用中渲染敏感数据，自动处理_mask格式
 * 使用方式：<MaskData>{data}</MaskData> 或 <MaskData data={data}></MaskData>
 */
function MaskData(props) {
  // 获取要显示的数据
  let displayData = '';
  
  // 优先使用data prop，如果没有则使用children
  if (props.data !== undefined) {
    displayData = parseMaskData(props.data);
  } else if (props.children !== undefined) {
    displayData = parseMaskData(String(props.children));
  }
  
  // 过滤掉data属性，将其他属性传递给span元素
  const { data, ...restProps } = props;
  
  return React.createElement('span', restProps, displayData);
}

// 为了支持JSX语法，当使用React.createElement时需要引用React
// 这里我们假设React已经全局可用，或者在使用时会导入

/**
 * 监听DOM加载完成事件，自动处理非React管理的MaskData标签
 */
if (typeof window !== 'undefined') {
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', processRawMaskDataTags);
  } else {
    processRawMaskDataTags();
  }
}

// 主模块导出
const SensitiveReact = {
  version,
  parseMaskData,
  MaskData,
  processRawMaskDataTags
};

// 导出，支持CommonJS和ES6 Module
module.exports = SensitiveReact;
module.exports.default = SensitiveReact;

// ES Module导出
try {
  exports.version = version;
  exports.parseMaskData = parseMaskData;
  exports.MaskData = MaskData;
  exports.processRawMaskDataTags = processRawMaskDataTags;
  exports.default = SensitiveReact;
} catch (e) {
  // 忽略ES Module导出错误
}

// 全局对象导出
if (typeof window !== 'undefined') {
  window.SensitiveReact = SensitiveReact;
  window.parseMaskData = parseMaskData;
  window.MaskData = MaskData;
  window.processRawMaskDataTags = processRawMaskDataTags;
}