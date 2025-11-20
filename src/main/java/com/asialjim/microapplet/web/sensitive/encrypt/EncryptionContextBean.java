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

package com.asialjim.microapplet.web.sensitive.encrypt;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加密上下文 - 策略模式上下文
 */
@Component
public class EncryptionContextBean {
    public static EncryptionContextBean instance;

    private static final Map<AlgorithmMode, EncryptionContext> CONTEXT_MAP = new ConcurrentHashMap<>();
    @Resource
    private List<EncryptionContext> contexts;
    @Resource
    private AlgorithmModeConfig algorithmModeConfig;
    @Resource
    private SecretKeyRepository secretKeyRepository;

    @PostConstruct
    public void init() {
        instance = this;
    }

    @SneakyThrows
    public EncryptionResult encrypt(String source) {
        AlgorithmMode mode = this.algorithmModeConfig.currentMode();

        EncryptionContext ctx = encryptionContextOf(mode);
        SecretKeyRepository.Pair pair = this.secretKeyRepository.pairOf(mode);
        return ctx.encrypt(source, pair.getEncKey(), pair.getMacKey());
    }

    @SneakyThrows
    public String decrypt(String source) {
        AlgorithmMode mode = this.algorithmModeConfig.currentMode();

        EncryptionContext ctx = encryptionContextOf(mode);
        SecretKeyRepository.Pair pair = this.secretKeyRepository.pairOf(mode);

        return ctx.decrypt(source, pair.getEncKey(), pair.getMacKey());
    }


    private EncryptionContext encryptionContextOf(AlgorithmMode mode) {
        EncryptionContext encryptionContext = CONTEXT_MAP.get(mode);
        if (Objects.nonNull(encryptionContext))
            return encryptionContext;
        synchronized (CONTEXT_MAP) {
            encryptionContext = CONTEXT_MAP.get(mode);
            if (Objects.nonNull(encryptionContext))
                return encryptionContext;
            encryptionContext = contexts.stream()
                    .filter(Objects::nonNull)
                    .filter(item -> mode.equals(item.getCurrentAlgorithmMode()))
                    .findAny().orElse(null);

            CONTEXT_MAP.put(mode, encryptionContext);

            return encryptionContext;
        }
    }
}