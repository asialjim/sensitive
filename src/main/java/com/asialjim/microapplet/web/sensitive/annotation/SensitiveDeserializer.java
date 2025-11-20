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

package com.asialjim.microapplet.web.sensitive.annotation;

import com.asialjim.microapplet.web.sensitive.encrypt.EncryptionContextBean;
import com.asialjim.microapplet.web.sensitive.encrypt.EncryptionResult;
import com.asialjim.microapplet.web.sensitive.handler.SensitiveHandler;
import com.asialjim.microapplet.web.sensitive.handler.SensitiveType;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
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

        String mask = SensitiveHandler.mask(sensitive, valueAsString);
        return EncryptionContextBean.instance.decrypt(valueAsString);
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