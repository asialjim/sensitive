# mask.js - 微信小程序使用指南

## 概述

本文档提供了在微信小程序环境中使用mask.js工具处理敏感数据的详细指导。mask.js是一个轻量级的原生JavaScript工具，专门用于处理敏感数据的显示，本文档将重点介绍如何在微信小程序的特殊环境中正确集成和使用该工具。

## 文件结构

```
sensitive/
├── mask.js           # 核心工具文件
├── README-js.md      # 原生JavaScript使用文档
├── README-vue.md     # Vue框架使用文档
├── README-react.md   # React框架使用文档
└── README-wx-applet.md # 微信小程序使用文档 (当前文档)
```

## 特性

- **小程序环境兼容**：完全适配微信小程序运行时环境
- **WXML模板支持**：提供自定义过滤器或辅助函数
- **全局/局部使用**：支持全局注册和页面级局部引入
- **组件封装**：可封装为自定义组件方便复用
- **性能优化**：适配小程序特有的性能考量
- **云开发支持**：与微信云开发环境良好集成

## 安装与引入

### 方法一：复制文件

1. 将`mask.js`文件复制到小程序项目的`utils`目录下
2. 在需要使用的页面或组件中引入

```
小程序项目结构示例：
├── pages/
│   ├── index/
│   └── user/
├── components/
├── utils/
│   ├── mask.js          # 复制到这里
│   └── other-utils.js
├── app.js
├── app.json
└── app.wxss
```

### 方法二：NPM安装（推荐）

微信小程序支持NPM包引入，可按以下步骤操作：

1. 在小程序项目根目录初始化npm：
```bash
npm init -y
```

2. 将sensitive目录下的文件复制到项目中，或创建自定义npm包

3. 在微信开发者工具中执行"工具 > 构建npm"

## 全局配置方式

将mask.js配置为全局工具，方便在任意页面和组件中使用：

### 方式1：在app.js中全局引入

```javascript
// app.js
// 引入mask.js
const { mask } = require('./utils/mask.js');

App({
  // 全局属性，可在任意页面通过getApp().globalData访问
  globalData: {
    // 其他全局数据
  },
  
  // 全局方法，挂载到App实例
  mask: function(data) {
    return mask(data);
  },
  
  onLaunch: function() {
    // 应用启动时的初始化逻辑
  }
});
```

### 方式2：使用全局mixins

创建全局mixin，自动为所有页面注入处理方法：

```javascript
// utils/global-mixin.js
const { mask } = require('./mask.js');

module.exports = {
  // 页面数据
  data: {
    // 可添加全局数据
  },
  
  // 页面方法
  methods: {
    // 敏感数据处理方法
    maskSensitiveData: function(data) {
      return mask(data);
    },
    
    // 处理对象中的敏感字段
    maskObjectFields: function(obj, fields) {
      if (!obj || typeof obj !== 'object') return obj;
      
      const result = { ...obj };
      fields.forEach(field => {
        if (result[field] !== undefined) {
          result[field] = mask(result[field]);
        }
      });
      return result;
    }
  },
  
  // 生命周期方法
  onLoad: function() {
    // 可以在这里执行一些通用逻辑
  }
};

// 在app.js中注册全局mixin
const globalMixin = require('./utils/global-mixin.js');

// 使用wxpage-mixins或其他mixin库实现全局混入
// 或者在每个页面单独引入并混入
```

## 页面级使用方式

### 基础使用

在单个页面中引入和使用mask.js：

```javascript
// pages/user/index.js
// 引入mask.js
const { mask } = require('../../utils/mask.js');

Page({
  data: {
    userInfo: null,
    processedUserInfo: null
  },
  
  onLoad: function() {
    // 模拟获取用户数据
    const userData = {
      id: 123,
      name: '_mask|1|a1b2c3|d4e5f6|789abc|张*',
      phone: '_mask|1|a1b2c3|d4e5f6|789abc|138****1234',
      email: '_mask|1|a1b2c3|d4e5f6|789abc|exa****@example.com',
      address: '北京市朝阳区某某街道123号'
    };
    
    // 方式1：在获取数据后直接处理并存储
    const processedUserInfo = {
      ...userData,
      name: mask(userData.name),
      phone: mask(userData.phone),
      email: mask(userData.email)
    };
    
    this.setData({
      userInfo: userData,
      processedUserInfo: processedUserInfo
    });
  },
  
  // 方式2：定义处理方法，在需要时调用
  maskData: function(data) {
    return mask(data);
  },
  
  // 处理对象中的多个敏感字段
  processSensitiveFields: function(obj, fields) {
    if (!obj) return obj;
    
    const result = { ...obj };
    fields.forEach(field => {
      if (result[field]) {
        result[field] = mask(result[field]);
      }
    });
    return result;
  }
});
```

在WXML中使用处理后的数据：

```html
<!-- pages/user/index.wxml -->
<view class="user-container">
  <view class="title">用户信息</view>
  
  <!-- 方式1：使用已处理的数据 -->
  <view class="info-item">
    <text class="label">用户名：</text>
    <text class="value">{{processedUserInfo.name}}</text>
  </view>
  <view class="info-item">
    <text class="label">手机号：</text>
    <text class="value">{{processedUserInfo.phone}}</text>
  </view>
  <view class="info-item">
    <text class="label">邮箱：</text>
    <text class="value">{{processedUserInfo.email}}</text>
  </view>
  
  <!-- 方式2：在渲染时通过方法处理 -->
  <!-- 注意：微信小程序不支持直接在WXML中调用函数处理数据，需要提前处理 -->
</view>
```

### 列表数据处理

处理列表中的敏感数据：

```javascript
// pages/user-list/index.js
const { mask } = require('../../utils/mask.js');

Page({
  data: {
    userList: [],
    processedUserList: []
  },
  
  onLoad: function() {
    this.fetchUserList();
  },
  
  fetchUserList: function() {
    // 模拟API请求获取用户列表
    wx.showLoading({ title: '加载中' });
    
    // 模拟数据
    setTimeout(() => {
      const userList = [
        {
          id: 1,
          name: '_mask|1|a1b2c3|d4e5f6|789abc|张*',
          phone: '_mask|1|a1b2c3|d4e5f6|789abc|138****1234'
        },
        {
          id: 2,
          name: '_mask|1|a1b2c3|d4e5f6|789abc|李*',
          phone: '_mask|1|a1b2c3|d4e5f6|789abc|139****5678'
        }
      ];
      
      // 处理列表中的敏感数据
      const processedUserList = userList.map(user => ({
        ...user,
        name: mask(user.name),
        phone: mask(user.phone)
      }));
      
      this.setData({
        userList: userList,
        processedUserList: processedUserList
      });
      
      wx.hideLoading();
    }, 1000);
  }
});
```

对应的WXML：

```html
<!-- pages/user-list/index.wxml -->
<view class="user-list">
  <view class="list-header">用户列表</view>
  
  <block wx:if="{{processedUserList.length > 0}}">
    <view class="list-item" wx:for="{{processedUserList}}" wx:key="id">
      <view class="user-info">
        <text class="user-name">{{item.name}}</text>
        <text class="user-phone">{{item.phone}}</text>
      </view>
    </view>
  </block>
  
  <view class="empty" wx:else>
    暂无用户数据
  </view>
</view>
```

## 自定义组件封装

创建一个敏感数据显示组件，方便在多处复用：

### 1. 创建组件

**组件目录结构**：
```
components/
└── sensitive-text/
    ├── sensitive-text.js
    ├── sensitive-text.json
    ├── sensitive-text.wxml
    └── sensitive-text.wxss
```

**组件JS文件**：
```javascript
// components/sensitive-text/sensitive-text.js
const { mask } = require('../../utils/mask.js');

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    // 原始文本内容
    text: {
      type: String,
      value: '',
      // 监听器，当属性变化时自动处理
      observer: function(newVal) {
        this.processText(newVal);
      }
    },
    
    // 默认显示文本（当原始文本为空时）
    fallback: {
      type: String,
      value: ''
    },
    
    // 是否使用自定义样式
    useCustomStyle: {
      type: Boolean,
      value: false
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    processedText: ''
  },

  /**
   * 组件的方法列表
   */
  methods: {
    // 处理文本内容
    processText: function(text) {
      try {
        if (!text && text !== 0) {
          this.setData({
            processedText: this.properties.fallback
          });
          return;
        }
        
        const processed = mask(String(text));
        this.setData({
          processedText: processed
        });
      } catch (error) {
        console.error('处理敏感数据时出错:', error);
        this.setData({
          processedText: this.properties.fallback
        });
      }
    }
  },
  
  /**
   * 生命周期函数
   */
  lifetimes: {
    // 组件实例进入页面节点树时执行
    attached: function() {
      this.processText(this.properties.text);
    }
  }
});
```

**组件WXML文件**：
```html
<!-- components/sensitive-text/sensitive-text.wxml -->
<text 
  class="sensitive-text {{useCustomStyle ? 'custom-style' : ''}}"
  bindtap="onTextTap"
>
  {{processedText}}
</text>
```

**组件WXSS文件**：
```css
/* components/sensitive-text/sensitive-text.wxss */
.sensitive-text {
  display: inline;
  word-break: break-all;
}

.sensitive-text.custom-style {
  color: #333333;
  font-size: 14px;
}
```

**组件JSON文件**：
```json
{
  "component": true,
  "usingComponents": {}
}
```

### 2. 使用组件

在页面中使用自定义组件：

**页面JSON文件**：
```json
{
  "usingComponents": {
    "sensitive-text": "../../components/sensitive-text/sensitive-text"
  }
}
```

**页面WXML文件**：
```html
<!-- pages/user-detail/index.wxml -->
<view class="user-detail">
  <view class="detail-header">用户详情</view>
  
  <view class="detail-section">
    <view class="detail-item">
      <text class="item-label">用户名：</text>
      <sensitive-text text="{{userInfo.name}}" />
    </view>
    
    <view class="detail-item">
      <text class="item-label">手机号：</text>
      <sensitive-text text="{{userInfo.phone}}" />
    </view>
    
    <view class="detail-item">
      <text class="item-label">邮箱：</text>
      <sensitive-text text="{{userInfo.email}}" />
    </view>
    
    <view class="detail-item">
      <text class="item-label">身份证号：</text>
      <sensitive-text 
        text="{{userInfo.idCard}}" 
        fallback="未知"
        useCustomStyle="true"
      />
    </view>
    
    <!-- 对于非敏感数据，直接显示 -->
    <view class="detail-item">
      <text class="item-label">注册时间：</text>
      <text>{{userInfo.registerTime}}</text>
    </view>
  </view>
</view>
```

## 云开发集成

在微信小程序云开发环境中使用mask.js：

### 1. 云函数中处理

在云函数中预处理敏感数据，减少小程序端的处理压力：

```javascript
// cloudfunctions/processUserData/index.js
// 注意：需要将mask.js文件复制到云函数目录
const { mask } = require('./mask.js');

// 云函数入口函数
exports.main = async (event, context) => {
  const wxContext = cloud.getWXContext();
  const userId = event.userId;
  
  try {
    // 从数据库获取用户数据
    const db = cloud.database();
    const userResult = await db.collection('users').doc(userId).get();
    const userData = userResult.data;
    
    // 在云函数中处理敏感数据
    const processedData = {
      ...userData,
      name: mask(userData.name),
      phone: mask(userData.phone),
      email: mask(userData.email),
      idCard: mask(userData.idCard)
    };
    
    return {
      success: true,
      data: processedData
    };
  } catch (error) {
    console.error('获取用户数据失败:', error);
    return {
      success: false,
      error: error.message
    };
  }
};
```

### 2. 小程序端调用云函数

```javascript
// pages/cloud-data/index.js
Page({
  data: {
    userData: null,
    loading: false
  },
  
  onLoad: function() {
    this.getUserDataFromCloud();
  },
  
  getUserDataFromCloud: function() {
    this.setData({ loading: true });
    
    // 调用云函数
    wx.cloud.callFunction({
      name: 'processUserData',
      data: {
        userId: '用户ID'
      }
    }).then(res => {
      if (res.result && res.result.success) {
        // 直接使用处理后的数据
        this.setData({
          userData: res.result.data
        });
      } else {
        wx.showToast({
          title: '获取数据失败',
          icon: 'none'
        });
      }
    }).catch(error => {
      console.error('云函数调用失败:', error);
      wx.showToast({
        title: '系统错误',
        icon: 'none'
      });
    }).finally(() => {
      this.setData({ loading: false });
    });
  }
});
```

## 最佳实践

### 1. 数据预处理原则

- **在数据获取阶段处理**：无论是从本地缓存、API接口还是云开发获取数据，都应该在数据到达展示层之前进行处理
- **避免重复处理**：对于频繁使用的数据，处理一次后缓存起来
- **按需处理**：只处理需要显示的敏感字段，减少不必要的计算

### 2. 性能优化

- **列表渲染优化**：对于大量数据的列表，使用分页加载，并在数据处理后再设置到页面
- **数据缓存**：对于不变的敏感数据，处理后缓存到本地
- **组件复用**：使用自定义组件封装敏感数据处理逻辑，提高复用性

```javascript
// 性能优化示例
const { mask } = require('../../utils/mask.js');

Page({
  data: {
    userList: [],
    page: 1,
    size: 20,
    hasMore: true
  },
  
  onLoad: function() {
    this.loadUserList();
  },
  
  // 分页加载并预处理
  loadUserList: function() {
    if (!this.data.hasMore) return;
    
    wx.showLoading({ title: '加载中' });
    
    // 模拟API请求
    setTimeout(() => {
      // 假设获取到了新数据
      const newData = []; // 模拟新获取的数据
      
      // 在设置到data之前预处理敏感数据
      const processedData = newData.map(item => ({
        ...item,
        name: mask(item.name),
        phone: mask(item.phone)
      }));
      
      // 合并数据
      const userList = [...this.data.userList, ...processedData];
      
      this.setData({
        userList: userList,
        page: this.data.page + 1,
        hasMore: processedData.length === this.data.size
      });
      
      wx.hideLoading();
    }, 1000);
  },
  
  // 上拉加载更多
  onReachBottom: function() {
    this.loadUserList();
  }
});
```

### 3. 错误处理和兜底显示

- **添加try-catch**：在处理敏感数据时添加错误捕获
- **设置兜底内容**：当处理失败或数据为空时，显示合理的兜底内容
- **日志记录**：记录关键错误，便于排查问题

```javascript
// 安全的处理函数
function safeProcessSensitiveData(data, fallback = '---') {
  try {
    if (!data && data !== 0) return fallback;
    return mask(String(data));
  } catch (error) {
    console.error('敏感数据处理错误:', error);
    return fallback;
  }
}

// 在组件或页面中使用
const processedName = safeProcessSensitiveData(userInfo.name, '未知');
```

### 4. 数据更新策略

- **监听数据变化**：使用observer或watch监听数据变化，自动重新处理
- **手动触发更新**：提供方法允许在需要时手动更新处理结果

## 常见问题与解决方案

### 1. 小程序环境兼容性问题

**问题**：在某些机型或系统版本上出现兼容性问题

**解决方案**：
- 避免使用ES6+高级特性，或者使用Babel转译
- 在使用前检查环境，对不支持的功能进行降级处理
- 使用微信小程序提供的polyfill方案

### 2. WXML中无法直接调用函数

**问题**：微信小程序WXML不支持像Vue那样在模板中直接调用函数处理数据

**解决方案**：
- 提前处理数据并设置到data中
- 使用自定义组件封装处理逻辑
- 使用计算属性（如果使用了wxs或其他支持计算属性的框架）

### 3. 性能问题

**问题**：在处理大量数据或频繁更新时出现性能问题

**解决方案**：
- 使用分页加载减少单次处理的数据量
- 缓存处理结果避免重复计算
- 优化处理逻辑，减少不必要的操作
- 使用云函数在服务端进行数据处理

### 4. 分包加载问题

**问题**：在分包中使用mask.js时的路径问题

**解决方案**：
- 确保引入路径正确，分包中引入根目录文件需要使用相对路径
- 可以将mask.js复制到每个分包中单独使用
- 使用微信小程序的全局函数或全局数据方式

## 版本信息

- **版本**: 1.0.0
- **支持的微信小程序基础库版本**: 2.10.0+
- **特性**: 微信小程序环境下的敏感数据处理支持
- **作者**: Generated by AI Assistant
- **日期**: 2024-01-15