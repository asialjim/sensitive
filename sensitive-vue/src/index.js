/**
 * 敏感数据处理库 - sensitive-vue
 * 提供Vue环境下的MaskData组件，用于处理_mask格式敏感数据
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
 * MaskData组件
 * 用于在Vue应用中渲染敏感数据，自动处理_mask格式
 * 使用方式：<MaskData>{{data}}</MaskData> 或 <MaskData :data="data"></MaskData>
 */
const MaskData = {
  name: 'MaskData',
  props: {
    data: {
      type: [String, Number, Boolean, Object, null, undefined],
      default: ''
    }
  },
  render(h) {
    // 获取要显示的数据
    let displayData = '';
    
    // 优先使用data prop，如果没有则使用默认插槽内容
    if (this.data !== undefined) {
      displayData = parseMaskData(this.data);
    } else if (this.$slots.default && this.$slots.default.length > 0) {
      // 处理插槽内容
      const slotContent = this.$slots.default[0].text;
      displayData = slotContent ? parseMaskData(slotContent) : '';
    }
    
    // 创建span元素，传递所有其他属性和样式
    const attrs = { ...this.$attrs };
    const style = this.$style || {};
    
    return h('span', {
      attrs,
      style,
      class: this.$class
    }, displayData);
  }
};

/**
 * Vue插件安装函数
 */
const install = (Vue, options = {}) => {
  // 全局配置
  Vue.prototype.$sensitive = {
    version,
    parseMaskData,
    options
  };
  
  // 注册全局组件
  Vue.component('MaskData', MaskData);
  
  // 注册全局方法
  Vue.filter('maskData', parseMaskData);
};

// 创建插件对象
const SensitiveVuePlugin = {
  version,
  install,
  MaskData,
  parseMaskData
};

// 自动安装（当Vue通过script标签全局引入时）
if (typeof window !== 'undefined' && window.Vue) {
  window.Vue.use(SensitiveVuePlugin);
}

// 导出，支持CommonJS和ES6 Module
module.exports = SensitiveVuePlugin;
module.exports.default = SensitiveVuePlugin;

// ES Module导出
try {
  exports.MaskData = MaskData;
  exports.parseMaskData = parseMaskData;
  exports.version = version;
  exports.install = install;
} catch (e) {
  // 忽略ES Module导出错误
}

// 全局对象导出
if (typeof window !== 'undefined') {
  window.SensitiveVue = SensitiveVuePlugin;
  window.parseMaskData = parseMaskData;
}