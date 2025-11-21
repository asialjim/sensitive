# 敏感数据处理框架 (Sensitive)

## 项目简介

`Sensitive` 是一个模块化的敏感数据处理框架，用于在应用程序中对敏感信息进行脱敏、加密和安全处理，保护用户隐私数据。该框架采用分层设计，支持多种使用场景，从独立使用到与Spring Boot应用集成。

### 模块化架构

项目采用清晰的模块化结构，分为三个核心模块：

1. **sensitive-core**: 核心脱敏和加密功能实现
2. **sensitive-jackson**: Jackson序列化/反序列化集成
3. **sensitive-spring**: Spring Boot自动配置支持

## 核心特性

- **模块化设计**: 清晰的责任分离，支持按需引入
- **多种敏感类型**: 内置8种常见敏感数据类型的处理
- **注解驱动**: 通过`@Sensitive`注解轻松标记需要脱敏的字段
- **自动序列化/反序列化**: 与Jackson无缝集成，实现JSON转换过程中的自动处理
- **策略模式**: 支持不同的加密算法策略切换（现代算法和国产密码算法）
- **Spring Boot集成**: 提供自动配置，开箱即用
- **灵活扩展**: 支持自定义敏感数据类型和处理规则
- **数据验证**: 内置正则表达式验证，确保敏感数据格式正确

## 支持的敏感数据类型

| 敏感类型 | 描述 | 默认保留前几位 | 默认保留后几位 | 正则校验 |
|---------|------|--------------|--------------|--------|
| BankCard | 银行卡号 | 6 | 4 | `^[1-9]\d{12,18}$` |
| EMail | 电子邮箱 | 3 | 4 | `^(?i)[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$` |
| ChineseCitizenIdCard | 中国身份证号 | 6 | 4 | `(^[1-9]\d{5}\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{2}[0-9Xx]$)|(^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$)` |
| ChineseMobilePhone | 中国手机号 | 3 | 4 | `^1[3-9]\d{9}$` |
| ChineseTellPhone | 中国电话号码 | 3 | 4 | `^(0\d{2,3}[-\s]?)?\d{7,8}([-\s]?\d{1,6})?$` |
| ChineseName | 中文姓名 | 1 | 1 | `^[\u4e00-\u9fa5]+(·[\u4e00-\u9fa5]+)*$` |
| EnglishName | 英文姓名 | 1 | 1 | `^[A-Za-z][A-Za-z'\-.]{1,19}(?:\s+[A-Za-z][A-Za-z'\-.]{1,19})*$` |
| Customer | 自定义敏感类型 | 1 | 1 | `^[\p{L}\p{N}]*$` |

## 模块功能详解

### 1. sensitive-core

核心模块，提供基础的敏感数据处理功能，包括：

- **SensitiveHandler**: 敏感数据处理的抽象基类，定义了脱敏和处理的核心接口
- **SensitiveType**: 定义支持的敏感数据类型枚举
- **各种具体处理器实现**: 如BankCardSensitiveHandler、ChineseMobilePhoneSensitiveHandler等
- **加密功能**: 基于策略模式的加密实现，支持多种加密算法
- **密钥管理**: 提供密钥生成和管理功能

### 2. sensitive-jackson

Jackson集成模块，提供JSON序列化和反序列化过程中的敏感数据处理：

- **@Sensitive注解**: 用于标记需要脱敏的字段
- **SensitiveSerializer**: 序列化器，负责在JSON序列化时对敏感字段进行加密和脱敏
- **SensitiveDeserializer**: 反序列化器，负责在JSON反序列化时对敏感字段进行解密和还原
- **JacksonSensitiveHandler**: 提供Jackson环境下的敏感数据处理功能

### 3. sensitive-spring

Spring Boot集成模块，提供自动配置功能：

- **MicroBankWebSensitiveBean**: 自动配置类，负责注册所有敏感数据处理器和加密相关的Bean
- **自动扫描和注册**: 自动扫描并注册敏感数据处理器
- **可自定义配置**: 支持自定义算法模式和密钥管理

## 快速开始

### 1. 添加依赖

根据您的需求选择合适的模块依赖：

**完整依赖（推荐用于Spring Boot项目）**
```xml
<dependency>
    <groupId>com.asialjim.microapplet</groupId>
    <artifactId>sensitive-spring</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

**仅使用Jackson集成**
```xml
<dependency>
    <groupId>com.asialjim.microapplet</groupId>
    <artifactId>sensitive-jackson</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

**仅使用核心功能**
```xml
<dependency>
    <groupId>com.asialjim.microapplet</groupId>
    <artifactId>sensitive-core</artifactId>
    <version>3.0.0-SNAPSHOT</version>
</dependency>
```

### 2. 在Spring Boot项目中使用

**在实体类中使用注解**

```java
public class User {
    private Long id;
    private String username;
    
    @Sensitive(SensitiveType.ChineseMobilePhone)
    private String phoneNumber;
    
    @Sensitive(SensitiveType.EMail)
    private String email;
    
    @Sensitive(value = SensitiveType.ChineseCitizenIdCard, prefix = 8, suffix = 4)
    private String idCard;
    
    @Sensitive(value = SensitiveType.ChineseName)
    private String realName;
    
    // getter and setter
}
```

**自动配置**

在Spring Boot应用中，只需引入依赖，框架会自动配置所有必要的组件。无需额外配置即可使用。

### 3. 非Spring环境使用

**手动注册处理器**

```java
// 创建处理器实例
SensitiveHandler mobileHandler = new ChineseMobilePhoneSensitiveHandler();
SensitiveHandler emailHandler = new EMailSensitiveHandler();

// 注册处理器
SensitiveHandler.holder.register(mobileHandler);
SensitiveHandler.holder.register(emailHandler);

// 使用核心方法进行脱敏
String maskedPhone = SensitiveHandler.mask(SensitiveType.ChineseMobilePhone, "13800138000");
```

**配置Jackson**

```java
// 创建ObjectMapper
ObjectMapper mapper = new ObjectMapper();

// 配置序列化器和反序列化器
SimpleModule module = new SimpleModule();
module.addSerializer(String.class, new SensitiveSerializer());
module.addDeserializer(String.class, new SensitiveDeserializer());
mapper.registerModule(module);

// 使用配置好的ObjectMapper
```

## 使用示例

### 示例1: 基本使用（Spring Boot环境）

```java
@RestController
public class UserController {
    
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        // 从数据库获取用户信息
        User user = userService.findById(id);
        // 返回时会自动脱敏
        return user;
    }
    
    @PostMapping("/user")
    public User createUser(@RequestBody User user) {
        // 接收请求时会自动解密
        return userService.save(user);
    }
}
```

### 示例2: 自定义脱敏规则

```java
public class CustomEntity {
    // 自定义脱敏规则，保留前2位，后3位，使用自定义正则表达式
    @Sensitive(value = SensitiveType.Customer, prefix = 2, suffix = 3, regex = "^[A-Za-z0-9]{6,}$")
    private String customData;
    
    // getter and setter
}
```

### 示例3: 直接使用核心API

```java
// 直接使用SensitiveHandler进行脱敏
String phone = "13800138000";
String maskedPhone = SensitiveHandler.mask(SensitiveType.ChineseMobilePhone, phone);
System.out.println(maskedPhone); // 输出: 138****8000

// 使用自定义参数
String idCard = "110101199001011234";
String maskedIdCard = SensitiveHandler.mask(SensitiveType.ChineseCitizenIdCard, idCard, 8, 4, idCard -> {
    // 自定义脱敏逻辑
    return idCard.substring(0, 8) + "********" + idCard.substring(16);
});
```

## 自定义配置

### 1. 自定义加密算法模式

```java
@Component
public class CustomAlgorithmModeConfig implements AlgorithmModeConfig {
    @Override
    public AlgorithmMode getAlgorithmMode() {
        // 返回MODERN或GM
        return AlgorithmMode.GM; // 使用国产密码算法
    }
}
```

### 2. 自定义密钥存储

```java
@Component
public class CustomSecretKeyRepository implements SecretKeyRepository {
    @Override
    public Pair pairOf(AlgorithmMode mode) {
        // 实现自定义的密钥管理逻辑
        // 从安全的密钥管理系统获取密钥
        Pair pair = new Pair();
        // 设置加密密钥和MAC密钥
        pair.setEncKey(/* 从安全存储获取的加密密钥 */);
        pair.setMacKey(/* 从安全存储获取的MAC密钥 */);
        return pair;
    }
}
```

### 3. 自定义敏感数据处理器

```java
@Component
public class CustomSensitiveHandler extends SensitiveHandler {
    @Override
    public SensitiveType type() {
        return SensitiveType.Customer;
    }
    
    @Override
    public Function<String, String> function() {
        return source -> {
            // 实现自定义的脱敏逻辑
            return "自定义脱敏结果";
        };
    }
}
```

## 技术实现原理

### 1. 脱敏机制

- 使用`SensitiveHandler`的静态`mask`方法进行通用脱敏
- 基于前缀和后缀保留规则，中间部分用`*`替换
- 支持自定义脱敏函数，实现更复杂的脱敏逻辑

### 2. 加密机制

- 采用策略模式实现多种加密算法
- 支持现代加密算法和国产密码算法
- 加密后的数据包含算法标识、随机数、密文和MAC值
- 使用复合格式存储加密信息和脱敏显示值

### 3. 序列化/反序列化流程

- 序列化时：先进行脱敏，然后加密原始数据，最后将两者组合为特殊格式
- 反序列化时：识别特殊格式，解密原始数据，或验证原始数据格式

## 项目依赖

- Java 21+
- Spring Boot 3.2.9
- Jackson 库
- Apache Commons Lang3
- Lombok
- Bouncy Castle 加密库

## 版本要求

- JDK 21+（核心功能）
- Spring Boot 3.x（使用Spring集成时）
- Jackson 2.x（使用Jackson集成时）

## 许可证

本项目基于 Apache License 2.0 许可证开源。

## 贡献指南

欢迎提交 Issue 和 Pull Request 来改进本项目。

## 联系作者

- Email: asialjim@qq.com
- Email: asialjim@hotmail.com