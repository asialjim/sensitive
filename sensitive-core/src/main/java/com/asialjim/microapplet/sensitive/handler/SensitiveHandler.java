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

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 敏感数据处理器
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/11/17, &nbsp;&nbsp; <em>version:1.0</em>
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class SensitiveHandler {
    private static final Map<String, Pattern> PATTERN_MAP = new ConcurrentHashMap<>();
    public static final Holder holder = new Holder();

    @PostConstruct
    public void init() {
        holder.register(this);
    }

    public abstract SensitiveType type();

    public Function<String, String> function() {
        return s -> {
            SensitiveType type = type();
            int prefix = type.getPrefix();
            int suffix = type.getSuffix();
            return maskWithIndex(s, prefix, suffix);
        };
    }


    public static String mask(SensitiveType type, String source) {
        int prefix = type.getPrefix();
        int suffix = type.getSuffix();
        SensitiveHandler handler = holder.handlerOf(type);
        return mask(type, source, prefix, suffix, type.getRegex(), !SensitiveType.Customer.equals(type), handler.function());
    }


    public static String mask(SensitiveType type,
                              String source,
                              int prefix, int suffix,
                              Function<String, String> function) {

        return mask(type, source, prefix, suffix, StringUtils.EMPTY, false, function);
    }


    public static String mask(SensitiveType type,
                              String source,
                              String regex, boolean match,
                              Function<String, String> function) {

        return mask(type, source, type.getPrefix(), type.getSuffix(), regex, match, function);
    }


    public static String mask(SensitiveType type,
                              String source,
                              int prefix, int suffix,
                              String regex, boolean match,
                              Function<String, String> function) {

        if (StringUtils.isNotBlank(regex)) {
            boolean matches = patternOf(regex).matcher(source).matches();
            if (match && !matches)
                throw new IllegalArgumentException("敏感数据校验失败:不符合校验规则");
        }

        if (SensitiveType.Customer.equals(type))
            return maskWithIndex(source, prefix, suffix);

        return function.apply(source);
    }

    protected static String maskWithIndex(String s, int prefix, int suffix) {
        if (StringUtils.isBlank(s))
            return s;
        int length = StringUtils.length(s);

        if (prefix + suffix >= length)
            throw new IllegalArgumentException("敏感数据脱敏失败:敏感数据长度小于脱敏规则最低长度");
        int maskLen = length - prefix - suffix;

        return s.substring(0, prefix) + StringUtils.repeat('*', maskLen) + s.substring(length - suffix);
    }

    public static Pattern patternOf(String regex) {
        Pattern pattern = PATTERN_MAP.get(regex);
        if (Objects.nonNull(pattern))
            return pattern;

        synchronized (PATTERN_MAP) {
            pattern = PATTERN_MAP.get(regex);
            if (Objects.nonNull(pattern))
                return pattern;


            pattern = Pattern.compile(regex);
            PATTERN_MAP.put(regex, pattern);
        }
        return pattern;
    }

    public static class Holder {
        private static final Map<SensitiveType, SensitiveHandler> HANDLER_MAP = new ConcurrentHashMap<>();

        public void register(SensitiveHandler handler) {
            if (Objects.isNull(handler))
                return;

            HANDLER_MAP.put(handler.type(), handler);
        }

        public SensitiveHandler handlerOf(SensitiveType type) {
            SensitiveHandler handler = HANDLER_MAP.get(type);
            if (Objects.isNull(handler))
                throw new IllegalArgumentException("不支持的敏感数据类型");
            return handler;
        }
    }
}