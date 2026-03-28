# 调研与结论

> **更新时间：** 2026-03-28 13:34:57 +0800  
> **最近更新：** 阶段 5 结项检索：`R.layout.`、`ViewBinding`、`databinding`/`dataBinding`/`viewBinding`（源码与 Gradle）均无残留；`app/src/main/res/layout/` 无 `.xml`；`com.sunzk.demo` 包名误写已清零（此前已修）。

## 需求摘要

- **目标：** 项目中所有 **layout XML** 转为 Compose；若已有 Compose 等价实现，则 **移除 XML，保留 Compose**。
- **非目标：** 不要求将 `AndroidManifest.xml`、`values`、`res/xml`、纯配置类 XML 改为 Compose。

## 调研发现

### `app` 模块 `res/layout/*.xml` 结项清单（阶段 1 + 滚动更新）

| 布局文件 | Kotlin / 入口 | Compose 与双实现结论 |
|----------|----------------|----------------------|
| （已不存在）`activity_main.xml` | 曾用于 `SplashActivity` | **已完成：** `SplashActivity` 使用 `setContent { }`，工程内无 `activity_main` / `R.layout.activity_main` 引用。 |
| （已删除）`activity_game.xml` | — | **已完成：** `GameActivity` `setContent` + `GameNavHost`；已删 `game_navigation.xml` 与 6 个游戏 Fragment。 |
| （已删除）`activity_find_diff_color.xml` | — | **已完成：** `FindDiffColorFragment` 使用 `ComposeView` + `FindDiffColorScreen`；`FindDiffView` 嵌入 `AndroidView`。 |
| （已删除）`activity_select_pic.xml` | — | **已完成：** `SelectPicActivity` `setContent` + `compose/ui/SelectPicScreen.kt`；`LargeImageView` 经 `AndroidView`。 |
| （已删除）`floating_setting.xml` | — | **已完成：** `FloatingSettingWindowManager` 使用 `ComposeView` + `FloatingSettingScreen`；齿轮背景由 `commonButtonStyle()` 替代原 `bg_floating_setting`。 |
| （已删除）`merge_hsb_color_picker.xml` | — | **已完成：** `HSBColorPicker` 在 `buildUi()` 中程序化添加子视图，不再使用 ViewBinding。 |
| （已删除）`dialog_common_alert.xml` | — | **已完成：** 仅保留 `compose/ui/common/CommonAlertDialog.kt`（双按钮）；已删 View 版 `dialog/CommonAlertDialog.kt`。 |
| （已删除）`dialog_common_confirm.xml` | — | **已完成：** `compose/ui/common/CommonConfirmDialog.kt`（单按钮）；`IntermediateColorFragment` 状态驱动；已删 View 版。 |
| （已删除）`dialog_mock_color_settlement.xml` | — | **已完成：** `compose/ui/common/CommonSettlementDialog.kt`；`MockColorFragment` / `IntermediateColorFragment` 状态驱动；已删 View 版。 |
| （已删除）`activity_guess_color.xml` | — | **已删除：** 阶段 3 全局检索无引用。 |
| （已删除）`item_mode.xml` | — | **已删除：** 同上。 |

### 阶段 1 检索方法说明

- 对每份 layout：在仓库内搜索 `R.layout.<name>`、`databinding.*Binding`、文件名。
- 对 `CommonAlertDialog`：搜索符号 `CommonAlertDialog`，区分 `dialog` 包与 `compose` 包。

### 阶段 4：drawable 与构建（2026-03-28）

| 资源 | 结论 |
|------|------|
| `bg_common_bt.xml` 等 4 个 shape XML | **已删除**；全仓无 Kotlin/XML 引用（`CommonButton` 原 `@drawable/bg_common_bt` 已从 `styles.xml` 移除）。 |
| `thumb2.png`、`seek_fine_tuning_left/right.png` | **保留**：`HSBColorPicker`、`FindDiffColorScreen` 仍 `R.drawable` 引用。 |
| `viewBinding` | **`app`、`base` 已 `enable = false`**；工程无 layout 生成 Binding，`MVVMEx.bindView` 无调用，整文件删除。 |

### 阶段 5：全量残留检索（2026-03-28）

| 检索项 | 结果 |
|--------|------|
| `R.layout.`（`*.kt` / `*.java` / `*.xml`） | **0 处** |
| `ViewBinding` / `viewBinding` / `DataBinding` / `dataBinding`（源码与 `*.gradle.kts`，不含计划文档中的说明文字） | **业务代码与构建脚本 0 处**；仅 `doc/plans/*`、`doc/project/*`、`doc/review/*` 等文档提及历史步骤 |
| `app/**/res/layout/**/*.xml` | **0 个文件** |
| `com.sunzk.demo` | **0 处** |

说明：`LayoutInflater`/`inflate` 仍可能出现在通用扩展（如 `base` 的 `ViewEx.kt`），与已删除的 `res/layout` 无绑定关系。

## 技术决策

| 决策 | 原因 |
|------|------|
| 执行顺序跟随 `doc/plans/xml-to-compose/01`～`06` | 仓库已有分步文档，降低重复设计与遗漏 |
| HSB 取色条允许分阶段 | 自定义 SeekBar + Drawable 逻辑复杂，可先 `AndroidView` 再纯 Compose |
| 孤儿 layout 推迟到阶段 3/4 物理删除 | 与迁移任务解耦，删除前用构建兜底 |
| Kotlin 迁移与新增 UI 代码遵循 `.cursor/skills-shared/code-styleguide-skills/`（入口 `styleguide-router`，Kotlin 见 `styleguide-kotlin`） | 与 `AGENTS.md` 一致；冲突时优先项目既有写法（见 router 优先级） |

## 遇到的问题

| 问题 | 处理结果 |
|------|----------|
| | |
