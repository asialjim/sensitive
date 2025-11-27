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
import com.asialjim.microapplet.sensitive.encrypt.EncryptionContext;
import com.asialjim.microapplet.sensitive.encrypt.EncryptionResult;
import com.asialjim.microapplet.sensitive.encrypt.KeyManager;
import com.asialjim.microapplet.sensitive.handler.*;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * 重构后的双算法加密方案使用示例
 */
public class EncryptionKeyTest {
    @Before
    public void before() {
        new BankCardSensitiveHandler().init();
        new ChineseCitizenIdCardSensitiveHandler().init();
        new ChineseMobilePhoneSensitiveHandler().init();
        new ChineseNameSensitiveHandler().init();
        new ChineseTellPhoneSensitiveHandler().init();
        new CustomerSensitiveHandler().init();
        new EMailSensitiveHandler().init();
        new EnglishNameSensitiveHandler().init();
    }

    @Test
    public void test() throws Exception {
        // 初始化密钥
        SecretKey modernKey = KeyManager.getOrGenerateKey(KeyManager.KEY_TYPE_MODERN);
        SecretKey gmEncryptionKey = KeyManager.getOrGenerateKey(KeyManager.KEY_TYPE_GM_ENCRYPTION);
        SecretKey gmMacKey = KeyManager.getOrGenerateKey(KeyManager.KEY_TYPE_GM_MAC);
        String modernKeyStr = Base64.getEncoder().encodeToString(modernKey.getEncoded());
        String gmEncKeyStr = Base64.getEncoder().encodeToString(gmEncryptionKey.getEncoded());
        String gmMacKeyStr = Base64.getEncoder().encodeToString(gmMacKey.getEncoded());

        System.out.print("1.\t");
        System.out.println(modernKeyStr);
        System.out.print("2.\t");
        System.out.println(gmEncKeyStr);
        System.out.print("3.\t");
        System.out.println(gmMacKeyStr);
    }

}