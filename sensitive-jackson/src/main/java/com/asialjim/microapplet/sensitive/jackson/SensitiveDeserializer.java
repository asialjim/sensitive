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
import com.asialjim.microapplet.sensitive.encrypt.EncryptionContextBean;
import com.asialjim.microapplet.sensitive.encrypt.EncryptionResult;
import com.asialjim.microapplet.sensitive.handler.SensitiveHandler;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("unused")
public class SensitiveDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {
    private final Sensitive sensitive;

    public SensitiveDeserializer() {
        this(null);
    }

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String valueAsString = jsonParser.getValueAsString();
        if (StringUtils.isBlank(valueAsString))
            return valueAsString;

        if (Objects.isNull(this.sensitive))
            return valueAsString;

        // 标准脱敏数据结构
        if (EncryptionResult.isEncryptionMaskData(valueAsString))
            // 直接解密字符串，不需要先脱敏
            return EncryptionContextBean.instance.decrypt(valueAsString);

        // 原数据满足正则表达式
        if (JacksonSensitiveHandler.match(valueAsString, this.sensitive))
            return valueAsString;

        throw new IllegalArgumentException("敏感数据校验失败:不符合校验规则");
    }

    public SensitiveDeserializer(Sensitive sensitive) {
        this.sensitive = sensitive;
    }


    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {
        if (Objects.isNull(beanProperty))
            return this;
        Sensitive annotation = beanProperty.getAnnotation(Sensitive.class);
        if (Objects.isNull(annotation))
            return this;

        return new SensitiveDeserializer(annotation);
    }
}