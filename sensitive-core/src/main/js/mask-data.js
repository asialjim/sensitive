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

class MaskData extends HTMLElement {
    // 定义需要监听的属性
    static get observedAttributes() {
        return ['value'];
    }

    constructor() {
        super();
        // 创建 Shadow DOM 实现样式封装
        const shadow = this.attachShadow({ mode: 'open' });

        // 组件模板
        shadow.innerHTML = `<span id="display-text"></span>`;
        this.displayElement = shadow.getElementById('display-text');
    }

    // 生命周期：当元素被插入到 DOM 时调用
    connectedCallback() {
        this._render();
    }

    // 生命周期：当监听的属性变化时调用
    attributeChangedCallback(name, oldValue, newValue) {
        if (name === 'value' && oldValue !== newValue) {
            this._render();
        }
    }

    // 解析数据的核心逻辑
    _parseValue(value) {
        if (!value || typeof value !== 'string') {
            return value || '';
        }

        // 检查是否符合 _mask|...|mask 结构
        const parts = value.split('|');
        if (parts.length === 6 && parts[0] === '_mask') {
            return parts[5]; // 返回最后一段的 mask 值
        }

        return value; // 返回普通字符串
    }

    // 渲染逻辑
    _render() {
        const value = this.getAttribute('value');
        const displayText = this._parseValue(value);
        if (this.displayElement) {
            this.displayElement.textContent = displayText;
        }
    }
}

// 注册自定义元素
customElements.define('mask-data', MaskData);