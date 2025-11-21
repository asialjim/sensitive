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

package com.asialjim.microapplet.sensitive.handler;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * 中国名脱敏工具
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/11/18, &nbsp;&nbsp; <em>version:1.0
 * </em>
 */
public class ChineseNameSensitiveHandler extends SensitiveHandler {
    @Override
    public SensitiveType type() {
        return SensitiveType.ChineseName;
    }

    @Override
    public Function<String, String> function() {
        return s -> {
            if (StringUtils.isBlank(s))
                return s;

            int length = StringUtils.length(s);
            if (length < 2)
                throw new IllegalArgumentException("敏感数据脱敏失败:敏感数据长度小于脱敏规则最低长度");

            if (length < 3)
                return maskWithIndex(s, 1, 0);

            if (length < 4)
                return maskWithIndex(s, 1, 1);

            return maskWithIndex(s, 2, 1);
        };
    }
}