# AI Assistant Memory & Rules - Trackail Project

## 核心开发规则与记忆 (Core Development Rules)

1. **界面文字多语言本地化 (UI Text Localization)**
   - **强制要求**：所有在 UI 上展示的字符串绝对不能在代码中硬编码（Hardcode）。
   - **实施标准**：每次添加或修改涉及界面文字的代码时，必须主动检查本地化情况。
   - **Android 规范**：必须将所有字符串抽离到 `strings.xml` 文件中，并通过资源引用（如 Compose 中的 `stringResource` 或 XML 中的 `@string/`）。
   - **语言支持**：确保中英文双语支持同步更新。
     - 英文/默认：`app/src/main/res/values/strings.xml`
     - 中文（简体）：`app/src/main/res/values-zh/strings.xml`

## 历史经验教训 (Lessons Learned)
- 覆盖安装环境下的签名冲突：使用 GitHub Actions 打包时，不要使用 Runner 随机生成的 Debug Keystore。需要将固定的 Keystore 转为 Base64 存入 GitHub Secrets，并在 `build.gradle.kts` 中通过环境变量读取进行统一签名配置。
- `SplashScreen` 底部版权文字应该进行本地化，不能受限于仅支持特定语言的硬编码。

*(后续所有的系统性习惯和规则都将补充记录在此)*
