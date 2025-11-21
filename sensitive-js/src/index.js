/**
 * 敏感数据处理库 - sensitive-js
 * 用于解析_mask格式的敏感数据
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
 * 处理页面中所有的MaskData标签
 * 扫描页面中所有MaskData标签，并将其内容解析后替换
 */
function processMaskDataTags() {
  // 获取所有MaskData标签
  const maskDataTags = document.querySelectorAll('MaskData');
  
  maskDataTags.forEach(tag => {
    // 获取标签内的文本内容
    const content = tag.textContent.trim();
    // 解析内容
    const parsedContent = parseMaskData(content);
    
    // 创建文本节点来替换标签
    const textNode = document.createTextNode(parsedContent);
    // 替换标签为文本内容
    tag.parentNode.replaceChild(textNode, tag);
  });
}

/**
 * 监听DOM加载完成事件，自动处理MaskData标签
 */
if (typeof window !== 'undefined') {
  // 当DOM加载完成时处理MaskData标签
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', processMaskDataTags);
  } else {
    // 如果DOM已经加载完成，立即处理
    processMaskDataTags();
  }
}

// 主模块导出
const SensitiveJS = {
  version,
  parseMaskData,
  processMaskDataTags
};

// 导出，支持CommonJS和ES6 Module
module.exports = SensitiveJS;
module.exports.default = SensitiveJS;

// ES Module导出
try {
  exports.version = version;
  exports.parseMaskData = parseMaskData;
  exports.processMaskDataTags = processMaskDataTags;
  exports.default = SensitiveJS;
} catch (e) {
  // 忽略ES Module导出错误
}

// 全局对象导出
if (typeof window !== 'undefined') {
  window.SensitiveJS = SensitiveJS;
  window.parseMaskData = parseMaskData;
  window.processMaskDataTags = processMaskDataTags;
}