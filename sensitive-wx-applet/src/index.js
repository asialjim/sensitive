/**
 * 敏感数据处理库 - sensitive-wx-applet
 * 提供微信小程序环境下的MaskData组件和parseMaskData函数，用于处理_mask格式敏感数据
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
 * 微信小程序MaskData组件
 * 用于在小程序页面中渲染敏感数据，自动处理_mask格式
 */
const MaskDataComponent = {
  // 组件的属性列表
  properties: {
    data: {
      type: [String, Number, Boolean, Object, null, undefined],
      value: ''
    },
    // 可选的自定义类名
    customClass: {
      type: String,
      value: ''
    }
  },

  // 组件的初始数据
  data: {
    displayData: ''
  },

  // 属性监听器
  observers: {
    'data': function(newData) {
      this.updateDisplayData(newData);
    }
  },

  // 组件生命周期
  lifetimes: {
    attached() {
      // 组件实例进入页面节点树时执行
      this.updateDisplayData(this.properties.data);
    }
  },

  // 组件的方法列表
  methods: {
    /**
     * 更新显示的数据
     * @param {*} data - 要处理的数据
     */
    updateDisplayData(data) {
      const displayData = parseMaskData(data);
      this.setData({
        displayData
      });
    }
  }
};

/**
 * 工具函数集合
 */
const utils = {
  /**
   * 解析敏感数据
   * @param {string|*} data - 要解析的数据
   * @returns {string} 解析后的数据
   */
  parseMaskData
};

// 导出，支持CommonJS和ES6 Module
module.exports = {
  version,
  MaskDataComponent,
  parseMaskData,
  utils
};

// 尝试ES Module导出
try {
  exports.version = version;
  exports.MaskDataComponent = MaskDataComponent;
  exports.parseMaskData = parseMaskData;
  exports.utils = utils;
} catch (e) {
  // 忽略ES Module导出错误
}

// 全局对象导出（如果在全局环境中运行）
if (typeof global !== 'undefined') {
  global.SensitiveWxApplet = module.exports;
}

// 微信小程序全局变量导出
if (typeof wx !== 'undefined') {
  // 注册为全局工具函数
  wx.parseMaskData = parseMaskData;
  wx.SensitiveWxApplet = module.exports;
}