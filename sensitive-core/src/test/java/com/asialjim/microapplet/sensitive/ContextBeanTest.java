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

import com.asialjim.microapplet.sensitive.encrypt.AlgorithmMode;
import com.asialjim.microapplet.sensitive.encrypt.AlgorithmModeConfig;
import com.asialjim.microapplet.sensitive.encrypt.EncryptionContext;
import com.asialjim.microapplet.sensitive.encrypt.EncryptionContextBean;
import com.asialjim.microapplet.sensitive.encrypt.KeyManager;
import com.asialjim.microapplet.sensitive.encrypt.SecretKeyRepository;
import org.junit.Test;

import java.util.Collections;

/**
 * 测试EncryptionContextBean的初始化
 */
public class ContextBeanTest {

    @Test
    public void testContextBeanInitialization() {
        System.out.println("[INFO] 测试EncryptionContextBean初始化状态");
        
        // 检查当前instance是否为null
        System.out.println("[DEBUG] EncryptionContextBean.instance是否为null: " + (EncryptionContextBean.instance == null));
        
        try {
            // 手动初始化EncryptionContextBean
            System.out.println("[INFO] 手动初始化EncryptionContextBean...");
            
            EncryptionContext ctx = new EncryptionContext(AlgorithmMode.MODERN);
            AlgorithmModeConfig cfg = () -> AlgorithmMode.MODERN;
            SecretKeyRepository repository = mode -> {
                SecretKeyRepository.Pair pair = new SecretKeyRepository.Pair();
                try {
                    switch (mode) {
                        case MODERN -> pair.setEncKey(KeyManager.generateModernEncryptionKey());
                        case GM -> {
                            pair.setEncKey(KeyManager.generateGMEncryptionKey());
                            pair.setMacKey(KeyManager.generateGMMacKey());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[ERROR] 生成密钥失败: " + e.getMessage());
                }
                return pair;
            };
            
            EncryptionContextBean bean = new EncryptionContextBean(
                    Collections.singletonList(ctx), cfg, repository
            );
            bean.init();
            
            // 再次检查instance是否已初始化
            System.out.println("[DEBUG] 初始化后，EncryptionContextBean.instance是否为null: " + (EncryptionContextBean.instance == null));
            
            if (EncryptionContextBean.instance != null) {
                System.out.println("[INFO] EncryptionContextBean初始化成功！");
            } else {
                System.out.println("[ERROR] EncryptionContextBean初始化失败！");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] 初始化过程中发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}