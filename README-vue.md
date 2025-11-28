# mask.js - Vue框架使用指南

## 概述

本文档提供了在Vue框架中使用mask.js工具处理敏感数据的详细指导。mask.js是一个轻量级的原生JavaScript工具，专门用于处理敏感数据的显示，而Vue集成方案则为Vue应用提供了更便捷的使用方式。

## 文件结构

```
sensitive/
├── mask.js           # 核心工具文件
├── README-js.md      # 原生JavaScript使用文档
├── README-vue.md     # Vue框架使用文档 (当前文档)
├── README-react.md   # React框架使用文档
└── README-wx-applet.md # 微信小程序使用文档
```

## 特性

- **Vue 2.x 过滤器支持**：直接在模板中使用 `{{ data | mask }}` 语法
- **Vue 3 全局属性**：为Vue 3提供全局属性支持
- **组件级集成**：支持在单个组件中引入和使用
- **TypeScript 兼容**：与TypeScript项目良好配合
- **响应式处理**：与Vue的响应式系统无缝集成

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

## Vue 2.x 使用指南

### 全局注册

在项目入口文件（通常是 `main.js`）中全局注册过滤器：

```javascript
// main.js
import Vue from 'vue';
import App from './App.vue';

// 引入mask.js
import { mask } from './path/to/mask.js';

// 注册过滤器
Vue.filter('mask', mask);
Vue.filter('sensitive', mask); // 别名

Vue.config.productionTip = false;

new Vue({
  render: h => h(App),
}).$mount('#app');
```

### 组件中使用

在Vue 2.x模板中，可以直接使用过滤器语法：

```vue
<template>
  <div class="user-profile">
    <h2>用户信息</h2>
    
    <!-- 使用过滤器处理敏感数据 -->
    <p>用户名: {{ user.name | mask }}</p>
    <p>手机号: {{ user.phone | mask }}</p>
    <p>邮箱: {{ user.email | mask }}</p>
    <p>身份证号: {{ user.idCard | mask }}</p>
    
    <!-- 也可以在v-bind中使用 -->
    <div v-bind:title="user.description | mask">
      悬停查看描述
    </div>
    
    <!-- 在指令中使用 -->
    <input v-model="formData.name" :placeholder="placeholderText | mask">
  </div>
</template>

<script>
export default {
  name: 'UserProfile',
  data() {
    return {
      user: {
        name: '_mask|1|a1b2c3|d4e5f6|789abc|张*',
        phone: '_mask|1|a1b2c3|d4e5f6|789abc|138****1234',
        email: '_mask|1|a1b2c3|d4e5f6|789abc|exa****@example.com',
        idCard: '_mask|1|a1b2c3|d4e5f6|789abc|1101**********1234',
        description: '_mask|1|a1b2c3|d4e5f6|789abc|这是一段包含敏感信息的描述...'
      },
      formData: {
        name: ''
      },
      placeholderText: '_mask|1|a1b2c3|d4e5f6|789abc|请输入用户名（显示已脱敏）'
    };
  }
};
</script>

<style scoped>
.user-profile {
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  max-width: 500px;
  margin: 20px auto;
}

h2 {
  margin-top: 0;
  color: #333;
}

p {
  margin: 10px 0;
  color: #666;
}
</style>
```

### 组件级别过滤器注册

如果你不想全局注册过滤器，也可以在组件级别注册：

```vue
<template>
  <div>
    <p>用户名: {{ userName | mask }}</p>
  </div>
</template>

<script>
import { mask } from './path/to/mask.js';

export default {
  name: 'LocalComponent',
  data() {
    return {
      userName: '_mask|1|a1b2c3|d4e5f6|789abc|李*'
    };
  },
  filters: {
    mask: mask
  }
};
</script>
```

## Vue 3 使用指南

Vue 3 移除了内置的过滤器功能，但我们可以使用全局属性或组合式API来实现类似的功能。

### 方法一：使用全局属性

在 `main.js` 中设置全局属性：

```javascript
// main.js
import { createApp } from 'vue';
import App from './App.vue';

// 引入mask.js
import { mask } from './path/to/mask.js';

const app = createApp(App);

// 添加全局属性
app.config.globalProperties.$mask = mask;
app.config.globalProperties.$filters = {
  mask: mask
};

app.mount('#app');
```

在组件中使用：

```vue
<template>
  <div class="user-profile">
    <h2>用户信息</h2>
    
    <!-- 使用全局属性处理敏感数据 -->
    <p>用户名: {{ $mask(user.name) }}</p>
    
    <!-- 或者使用filters对象 -->
    <p>手机号: {{ $filters.mask(user.phone) }}</p>
  </div>
</template>

<script>
export default {
  name: 'UserProfile',
  data() {
    return {
      user: {
        name: '_mask|1|a1b2c3|d4e5f6|789abc|张*',
        phone: '_mask|1|a1b2c3|d4e5f6|789abc|138****1234'
      }
    };
  }
};
</script>
```

### 方法二：使用组合式API（推荐）

使用Vue 3的组合式API创建一个自定义Hook：

```javascript
// composables/useMask.js
import { mask } from '../path/to/mask.js';

export function useMask() {
  return {
    mask,
    // 可以添加更多辅助方法
    maskUser(user) {
      if (!user) return user;
      return {
        ...user,
        name: mask(user.name),
        phone: mask(user.phone),
        email: mask(user.email)
      };
    }
  };
}
```

在组件中使用：

```vue
<template>
  <div class="user-profile">
    <h2>用户信息</h2>
    
    <!-- 使用从组合式API获取的mask函数 -->
    <p>用户名: {{ mask(user.name) }}</p>
    <p>手机号: {{ mask(user.phone) }}</p>
    
    <!-- 使用处理整个对象的辅助方法 -->
    <div v-if="processedUser">
      <h3>处理后的用户信息</h3>
      <p>用户名: {{ processedUser.name }}</p>
      <p>手机号: {{ processedUser.phone }}</p>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useMask } from './composables/useMask.js';

export default {
  name: 'UserProfile',
  setup() {
    // 获取mask相关函数
    const { mask, maskUser } = useMask();
    
    // 用户数据
    const user = ref({
      name: '_mask|1|a1b2c3|d4e5f6|789abc|王*',
      phone: '_mask|1|a1b2c3|d4e5f6|789abc|159****6789',
      email: '_mask|1|a1b2c3|d4e5f6|789abc|exa****@example.com'
    });
    
    // 处理后的用户数据
    const processedUser = computed(() => maskUser(user.value));
    
    // 模拟从API加载数据
    onMounted(() => {
      // 假设从API获取数据
      // fetchUser().then(data => user.value = data);
    });
    
    return {
      user,
      processedUser,
      mask
    };
  }
};
</script>
```

### 方法三：创建自定义指令

创建一个自定义指令来自动处理元素内容：

```javascript
// directives/mask.js
import { mask } from '../path/to/mask.js';

export default {
  mounted(el, binding) {
    // 设置元素内容为处理后的值
    el.textContent = mask(binding.value);
  },
  updated(el, binding) {
    // 当值更新时重新处理
    if (binding.oldValue !== binding.value) {
      el.textContent = mask(binding.value);
    }
  }
};
```

在 `main.js` 中注册指令：

```javascript
// main.js
import { createApp } from 'vue';
import App from './App.vue';
import vMask from './directives/mask.js';

const app = createApp(App);
app.directive('mask', vMask);
app.mount('#app');
```

在组件中使用：

```vue
<template>
  <div class="user-profile">
    <h2>用户信息</h2>
    
    <!-- 使用自定义指令 -->
    <p>用户名: <span v-mask="user.name"></span></p>
    <p>手机号: <span v-mask="user.phone"></span></p>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  name: 'UserProfile',
  setup() {
    const user = ref({
      name: '_mask|1|a1b2c3|d4e5f6|789abc|刘*',
      phone: '_mask|1|a1b2c3|d4e5f6|789abc|136****5678'
    });
    
    return {
      user
    };
  }
};
</script>
```

## 最佳实践

### 1. 数据预处理

在数据进入组件前进行预处理，减少模板中的函数调用：

```javascript
// Vue 2.x
created() {
  // 从API获取数据后预处理
  fetchUserData().then(data => {
    // 预处理敏感字段
    this.processedUser = {
      ...data,
      name: this.$options.filters.mask(data.name),
      phone: this.$options.filters.mask(data.phone),
      email: this.$options.filters.mask(data.email)
    };
  });
}

// Vue 3 (组合式API)
setup() {
  const processedUser = ref({});
  
  onMounted(() => {
    fetchUserData().then(data => {
      const { mask } = useMask();
      processedUser.value = {
        ...data,
        name: mask(data.name),
        phone: mask(data.phone),
        email: mask(data.email)
      };
    });
  });
  
  return { processedUser };
}
```

### 2. 列表性能优化

在处理列表数据时，避免在循环中重复调用过滤器：

```vue
<template>
  <!-- 不推荐的做法 -->
  <div v-for="item in rawItems" :key="item.id">
    <p>{{ item.sensitiveData | mask }}</p>
  </div>
  
  <!-- 推荐的做法：预处理列表数据 -->
  <div v-for="item in processedItems" :key="item.id">
    <p>{{ item.sensitiveData }}</p>
  </div>
</template>

<script>
import { computed } from 'vue';
import { mask } from './path/to/mask.js';

export default {
  name: 'ItemList',
  props: {
    rawItems: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    processedItems() {
      return this.rawItems.map(item => ({
        ...item,
        sensitiveData: mask(item.sensitiveData)
      }));
    }
  }
};
</script>
```

### 3. 响应式处理

当数据动态变化时，确保敏感数据也能正确更新：

```vue
<template>
  <div>
    <p>当前手机号: {{ maskedPhone }}</p>
    <input v-model="phoneInput" @input="updatePhone">
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { mask } from './path/to/mask.js';

export default {
  name: 'PhoneEditor',
  setup() {
    const phoneInput = ref('');
    const rawPhone = ref('');
    
    // 计算属性确保响应式更新
    const maskedPhone = computed(() => mask(rawPhone.value));
    
    const updatePhone = () => {
      // 模拟API调用返回新的敏感数据格式
      // 实际项目中，这通常是API返回的数据
      rawPhone.value = `_mask|1|a1b2c3|d4e5f6|789abc|${phoneInput.value.substring(0, 3)}****${phoneInput.value.substring(7)}`;
    };
    
    return {
      phoneInput,
      maskedPhone,
      updatePhone
    };
  }
};
</script>
```

## 常见问题与解决方案

### 1. Vue 3中过滤器不可用

**问题**：在Vue 3中使用 `{{ data | mask }}` 语法报错。

**解决方案**：Vue 3移除了过滤器功能，请使用全局属性或组合式API替代：

```vue
<!-- Vue 2.x -->
<p>{{ data | mask }}</p>

<!-- Vue 3替代方案 -->
<p>{{ $mask(data) }}</p> <!-- 使用全局属性 -->
<!-- 或者 -->
<p>{{ mask(data) }}</p> <!-- 使用组合式API导入的函数 -->
```

### 2. 数据更新后敏感数据没有变化

**问题**：当原始数据更新后，使用过滤器处理的敏感数据没有更新。

**解决方案**：确保数据是响应式的，并在数据变化时重新处理：

```vue
<template>
  <p>{{ maskedData }}</p>
  <button @click="updateData">更新数据</button>
</template>

<script>
import { ref, computed } from 'vue';
import { mask } from './path/to/mask.js';

export default {
  name: 'DataUpdater',
  setup() {
    const rawData = ref('_mask|1|a1b2c3|d4e5f6|789abc|初始数据');
    
    // 使用计算属性确保响应式更新
    const maskedData = computed(() => mask(rawData.value));
    
    const updateData = () => {
      rawData.value = '_mask|1|a1b2c3|d4e5f6|789abc|更新后的数据';
    };
    
    return {
      maskedData,
      updateData
    };
  }
};
</script>
```

### 3. 与TypeScript一起使用

**问题**：在TypeScript项目中使用时出现类型错误。

**解决方案**：添加类型声明：

```typescript
// src/types/mask.d.ts
declare function mask(data: string): string;

declare module 'vue' {
  interface ComponentCustomProperties {
    $mask: typeof mask;
    $filters: {
      mask: typeof mask;
    };
  }
}

export {}
```

## 版本信息

- **版本**: 1.0.0
- **支持的Vue版本**: Vue 2.x 和 Vue 3
- **特性**: Vue框架集成的敏感数据处理功能
- **作者**: Generated by AI Assistant
- **日期**: 2024-01-15
