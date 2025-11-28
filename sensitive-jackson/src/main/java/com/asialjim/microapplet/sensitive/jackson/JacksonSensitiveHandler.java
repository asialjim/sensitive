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

package com.asialjim.microapplet.sensitive.jackson;

import com.asialjim.microapplet.sensitive.annotation.Sensitive;
import com.asialjim.microapplet.sensitive.handler.SensitiveHandler;
import com.asialjim.microapplet.sensitive.handler.SensitiveType;

import static com.asialjim.microapplet.sensitive.handler.SensitiveHandler.*;

/**
 * 基于jackson的敏感数据处理器
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/11/21, &nbsp;&nbsp; <em>version:1.0</em>
 */
public class JacksonSensitiveHandler {

    public static boolean match(String source, Sensitive sensitive) {
        SensitiveType type = sensitive.value();
        String regex;
        if (SensitiveType.Customer.equals(type)) {
            regex = sensitive.regex();
        } else {
            regex = type.getRegex();
        }


        return patternOf(regex).matcher(source).matches();
    }


    public static String mask(Sensitive sensitive, String source) {
        SensitiveType type = sensitive.value();
        SensitiveHandler handler = SensitiveHandler.holder.handlerOf(type);
        int prefix, suffix;
        String regex;
        boolean match;
        if (SensitiveType.Customer.equals(type)) {
            prefix = sensitive.prefix();
            suffix = sensitive.suffix();
            regex = sensitive.regex();
            match = sensitive.match();
        } else {
            prefix = type.getPrefix();
            suffix = type.getSuffix();
            regex = type.getRegex();
            match = true;
        }

        return SensitiveHandler.mask(type, source, prefix, suffix, regex, match, handler.function());
    }
}