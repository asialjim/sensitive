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

package com.asialjim.microapplet.sensitive;

import com.asialjim.microapplet.sensitive.encrypt.*;
import com.asialjim.microapplet.sensitive.handler.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 微银 WebMVC Spring Bean 扫描
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/3/4, &nbsp;&nbsp; <em>version:1.0</em>
 */
@Slf4j
@Configuration
@ComponentScan
@Import({
        BankCardSensitiveHandler.class,
        ChineseCitizenIdCardSensitiveHandler.class,
        ChineseMobilePhoneSensitiveHandler.class,
        ChineseNameSensitiveHandler.class,
        ChineseTellPhoneSensitiveHandler.class,
        CustomerSensitiveHandler.class,
        EMailSensitiveHandler.class,
        EnglishNameSensitiveHandler.class,
        EncryptionContextBean.class
})
public class MicroBankWebSensitiveBean {

    @Bean
    @ConditionalOnMissingBean(AlgorithmModeConfig.class)
    public AlgorithmModeConfig algorithmModeConfig() {
        return () -> AlgorithmMode.MODERN;
    }

    @Bean
    @ConditionalOnMissingBean(SecretKeyRepository.class)
    public SecretKeyRepository secretKeyRepository() {
        return new SecretKeyRepository() {
            private static final Map<AlgorithmMode, Pair> PAIR_MAP = new ConcurrentHashMap<>();

            @Override
            @SneakyThrows
            public Pair pairOf(AlgorithmMode mode) {
                log.warn("默认的密钥管理仓库:SecretKeyRepository 在重启时将会导致密钥变更导致业务异常，请配置可信的中央密钥管理仓库");
                if (Objects.isNull(mode))
                    return null;
                Pair pair = PAIR_MAP.get(mode);
                if (Objects.nonNull(pair))
                    return pair;

                synchronized (PAIR_MAP) {
                    pair = PAIR_MAP.get(mode);
                    if (Objects.nonNull(pair))
                        return pair;

                    pair = new Pair();
                    switch (mode) {
                        case MODERN -> pair.setEncKey(KeyManager.generateModernEncryptionKey());
                        case GM -> {
                            pair.setEncKey(KeyManager.generateGMEncryptionKey());
                            pair.setMacKey(KeyManager.generateGMMacKey());
                        }
                    }
                    PAIR_MAP.put(mode, pair);
                }
                return pair;
            }
        };
    }
}