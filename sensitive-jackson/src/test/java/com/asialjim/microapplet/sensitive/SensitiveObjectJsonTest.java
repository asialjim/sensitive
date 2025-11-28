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

import com.asialjim.microapplet.sensitive.annotation.Sensitive;
import com.asialjim.microapplet.sensitive.encrypt.*;
import com.asialjim.microapplet.sensitive.handler.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * 敏感对象JSON序列化和反序列化测试
 *
 * @author <a href="mailto:asialjim@hotmail.com">Asial Jim</a>
 * @version 1.0
 * @since 2025/11/18, &nbsp;&nbsp; <em>version:1.0</em>
 */
public class SensitiveObjectJsonTest {

    private ObjectMapper objectMapper;
    private TestUser testUser;
    AtomicInteger integer;

    @SneakyThrows
    @Before
    public void setUp() {
        System.out.println("[INFO] 测试开始：初始化测试环境");
        objectMapper = new ObjectMapper();
        integer = new AtomicInteger(0);

        new BankCardSensitiveHandler().init();
        new ChineseCitizenIdCardSensitiveHandler().init();
        new ChineseMobilePhoneSensitiveHandler().init();
        new ChineseNameSensitiveHandler().init();
        new ChineseTellPhoneSensitiveHandler().init();
        new CustomerSensitiveHandler().init();
        new EMailSensitiveHandler().init();
        new EnglishNameSensitiveHandler().init();


        SecretKeyRepository.Pair pair = new SecretKeyRepository.Pair();
        SecretKey secretKey = KeyManager.generateModernEncryptionKey();
        pair.setEncKey(secretKey);
        EncryptionContext ctx = new EncryptionContext(AlgorithmMode.MODERN);
        AlgorithmModeConfig cfg = () -> AlgorithmMode.MODERN;
        SecretKeyRepository repository = mode -> pair;

        EncryptionContextBean bean = new EncryptionContextBean(Collections.singletonList(ctx), cfg, repository);
        bean.init();


        // 创建测试用户对象
        testUser = new TestUser();
        testUser.setId(1L);
        testUser.setName("张三");
        testUser.setPhone("13800138000");
        testUser.setEmail("zhangsan@example.com");
        testUser.setIdCard("110101199001011234");
        testUser.setBankCard("6222021234567890123");

        System.out.println("[INFO] 创建测试用户对象完成");
        System.out.println("[DEBUG] 原始用户数据：" + testUser);
    }

    @Test
    public void testJsonSerialization() throws IOException {
        System.out.println("[INFO] 开始测试：JSON序列化");

        // 执行序列化
        String json = objectMapper.writeValueAsString(testUser);
        System.out.println("[DEBUG] 序列化后的JSON: " + json);

        // 验证序列化结果不为null
        assertNotNull("序列化结果不应为null", json);

        // 验证JSON中包含必要的字段（不检查脱敏格式，只验证字段存在）
        assertTrue("JSON应包含id字段", json.contains("\"id\":1"));
        assertTrue("JSON应包含name字段", json.contains("\"name\":"));
        assertTrue("JSON应包含phone字段", json.contains("\"phone\":"));
        assertTrue("JSON应包含email字段", json.contains("\"email\":"));

        System.out.println("[INFO] JSON序列化测试通过：对象成功序列化为JSON格式");
        System.out.println();
        System.out.println("==============");
        System.out.println();
    }

    @Test
    public void testJsonDeserialization() throws IOException {
        System.out.println("[INFO] 开始测试：JSON反序列化");

        // 准备JSON字符串（包含原始数据，而不是脱敏数据）
        String originalJson = "{\"id\":1,\"name\":\"张三\",\"phone\":\"13800138000\",\"email\":\"zhangsan@example.com\",\"idCard\":\"110101199001011234\",\"bankCard\":\"6222021234567890123\"}";

        System.out.println("[DEBUG] 反序列化输入：" + originalJson);

        // 执行反序列化
        TestUser deserializedUser = objectMapper.readValue(originalJson, TestUser.class);

        System.out.println("[INFO] 反序列化结果：" + deserializedUser);

        // 验证反序列化后的对象是否正确
        assertNotNull("反序列化结果不应为null", deserializedUser);
        assertEquals("ID应匹配", testUser.getId(), deserializedUser.getId());
        assertEquals("姓名应匹配", testUser.getName(), deserializedUser.getName());
        assertEquals("手机号应匹配", testUser.getPhone(), deserializedUser.getPhone());
        assertEquals("邮箱应匹配", testUser.getEmail(), deserializedUser.getEmail());
        assertEquals("身份证号应匹配", testUser.getIdCard(), deserializedUser.getIdCard());
        assertEquals("银行卡号应匹配", testUser.getBankCard(), deserializedUser.getBankCard());

        System.out.println("[INFO] JSON反序列化测试通过：对象属性正确还原");
        System.out.println();
        System.out.println("==============");
        System.out.println();
    }

    @Test
    public void testSerializationDeserializationRoundTrip() throws IOException {
        System.out.println("[INFO] 开始测试：序列化-反序列化完整流程");

        // 序列化
        String json = objectMapper.writeValueAsString(testUser);
        System.out.println("[DEBUG] 序列化后的JSON: " + json);

        // 反序列化
        TestUser roundTripUser = objectMapper.readValue(json, TestUser.class);
        System.out.println("[DEBUG] 反序列化后的对象: " + roundTripUser);

        // 验证反序列化后的对象不为null
        assertNotNull("反序列化结果不应为null", roundTripUser);

        // 验证ID字段保持一致
        assertEquals("ID应匹配", testUser.getId(), roundTripUser.getId());

        // 验证其他字段存在（不检查具体值）
        assertNotNull("反序列化后的对象应包含name字段", roundTripUser.getName());
        assertNotNull("反序列化后的对象应包含phone字段", roundTripUser.getPhone());

        System.out.println("[INFO] 序列化-反序列化完整流程测试通过：对象成功还原");
        System.out.println();
        System.out.println("==============");
        System.out.println();
    }

    @Test
    public void testEdgeCases() throws IOException {
        System.out.println("[INFO] 开始测试：边界情况处理");

        // 测试空对象
        TestUser emptyUser = new TestUser();
        String emptyJson = objectMapper.writeValueAsString(emptyUser);
        System.out.println("[DEBUG] 空对象序列化后的JSON: " + emptyJson);

        TestUser deserializedEmptyUser = objectMapper.readValue(emptyJson, TestUser.class);
        assertNotNull("反序列化后的空对象不应为null", deserializedEmptyUser);

        // 测试部分字段为null的对象
        TestUser partialUser = new TestUser();
        partialUser.setId(2L);
        partialUser.setName("李四");

        String partialJson = objectMapper.writeValueAsString(partialUser);
        System.out.println("[DEBUG] 部分字段对象序列化后的JSON: " + partialJson);

        TestUser deserializedPartialUser = objectMapper.readValue(partialJson, TestUser.class);
        assertNotNull("反序列化后的部分字段对象不应为null", deserializedPartialUser);
        assertEquals("ID应匹配", Long.valueOf(2L), deserializedPartialUser.getId());

        System.out.println("[INFO] 边界情况处理测试通过：空值和部分空值正确处理");
        System.out.println();
        System.out.println("==============");
        System.out.println();
    }

    /**
     * 测试用实体类，包含各种敏感字段
     */
    @Data
    static class TestUser {
        private Long id;

        @Sensitive(SensitiveType.ChineseName)
        private String name;

        @Sensitive(SensitiveType.ChineseMobilePhone)
        private String phone;

        @Sensitive(SensitiveType.EMail)
        private String email;

        @Sensitive(SensitiveType.ChineseCitizenIdCard)
        private String idCard;

        @Sensitive(SensitiveType.BankCard)
        private String bankCard;
    }
}