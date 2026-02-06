# 发布 lib_websocket 到 Maven Central

## 前置准备

### 1. 注册 Sonatype 账户

1. 访问 [Sonatype JIRA](https://issues.sonatype.org/secure/Signup!default.jspa) 创建账户
2. 创建 New Project: "Community Support - Open Source Project Repository Hosting"
3. 等待审核（通常 1-3 个工作日）

### 2. 配置 GPG 密钥

```bash
# 生成 GPG 密钥
gpg --full-generate-key

# 选择：RSA and RSA (4096 bits)
# 选择：Key does not expire
# 输入用户信息
```

```bash
# 导出私钥（用于 Gradle）
gpg --armor --export-secret-keys YOUR_EMAIL > ~/.gnupg/private-key.asc

# 导出公钥
gpg --armor --export YOUR_EMAIL > ~/.gnupg/public-key.asc
```

### 3. 配置 Gradle 凭据

在 `~/.gradle/gradle.properties` 中添加：

```properties
# Sonatype 凭据
sonatypeUsername=your_jira_username
sonatypePassword=your_jira_password

# GPG 密钥库密码
signing.keyId=YOUR_KEY_ID
signing.password=YOUR_KEY_PASSWORD
signing.secretKeyRingFile=/path/to/secret-key.gpg
```

## 发布流程

### 1. 准备版本

1. 更新版本号（如果需要）：
   ```properties
   # gradle.properties
   lib_websocket_version=1.0.0
   ```

2. 构建：
   ```bash
   ./gradlew :libs:lib_websocket:build
   ```

### 2. 生成并检查发布文件

```bash
# 生成 POM 文件和签名文件
./gradlew :libs:lib_websocket:generatePomFileForReleasePublication
./gradlew :libs:lib_websocket:signArchives

# 检查发布文件
./gradlew :libs:lib_websocket:publishAllPublicationsToSonatypeRepository
```

### 3. 发布到 Maven Central

```bash
# 发布到 Nexus
./gradlew :libs:lib_websocket:publishAllPublicationsToSonatypeRepository

# 关闭并同步发布
./gradlew closeAndReleaseRepository
```

### 4. 验证发布

1. 访问 [Maven Central](https://central.sonatype.com/)
2. 搜索 `com.xiaotimel.im:websocket`
3. 等待 10-30 分钟后生效

## 自动化发布脚本

创建 `scripts/publish.sh`：

```bash
#!/bin/bash

# 清理之前的构建
./gradlew clean

# 检查 Git 是否有未提交的更改
if [ -n "$(git status --porcelain)" ]; then
    echo "⚠️  有未提交的更改，请先提交"
    exit 1
fi

# 检查是否有 Git tag
if [ -z "$(git describe --exact-match --tags HEAD 2>/dev/null)" ]; then
    echo "⚠️  当前 HEAD 没有 Git tag，请先创建 tag"
    exit 1
fi

# 构建并测试
./gradlew :libs:lib_websocket:build :libs:lib_websocket:test

# 生成 POM 文件
./gradlew :libs:lib_websocket:generatePomFileForReleasePublication

# 签名并发布
./gradlew :libs:lib_websocket:signArchives
./gradlew :libs:lib_websocket:publishAllPublicationsToSonatypeRepository

echo "✅ 已发布到 Nexus"
echo "⏳ 请在 Sonatype 网站上点击 'Close'，然后 'Release'"
echo "📦 发布成功后可在 Maven Central 搜索到"
```

## 发布失败排查

### 常见问题

1. **权限错误**
   ```
   Could not publish publication 'release'
   ```
   - 检查 Sonatype 凭据是否正确
   - 确认项目已获得发布权限

2. **GPG 签名失败**
   ```
   No such key: XXXXXXXX
   ```
   - 检查 GPG 密钥配置
   - 确认密钥已导入到系统中

3. **POM 文件错误**
   ```
   POM is invalid
   ```
   - 检查所有必需的 POM 字段
   - 确认版本号格式正确

### 手动发布命令

```bash
# 仅发布 Snapshot
./gradlew publishToSonatype

# 关闭仓库
./gradlew closeRepository

# 释放仓库
./gradlew releaseRepository
```

## 发布历史

| 版本 | 发布日期 | 说明 |
|------|---------|------|
| 1.0.0 | 2026-02-06 | 首次发布 |

## 注意事项

1. 发布前确保所有测试通过
2. 版本号遵循 [语义化版本](https://semver.org/)
3. 每次发布前更新 README.md 中的版本信息
4. 发布后及时更新 pom.xml 中的发布日期
5. 考虑使用 Gradle Maven Publish Plugin 2.0+ 提供的 features