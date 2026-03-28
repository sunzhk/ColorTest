# Task Plan: xml-to-compose-full

> **更新时间：** 2026-03-28 13:34:57 +0800  
> **最近更新：** **阶段 5 已完成**：全仓检索无 `R.layout.`、无 `res/layout/*.xml`、Kotlin/Java 中无 `ViewBinding`/`databinding`/`dataBinding` 业务引用；`app`/`base` `build.gradle.kts` 无 `viewBinding` 开关。结项文档与 `findings.md` / `progress.md` 已同步。**剩余：** 主要流程手测（验收标准最后一项）。

## 编码规范（本计划执行约束）

迁移与新增 **Kotlin / Compose** 代码时，须遵循仓库 `AGENTS.md` 约定：**风格问题优先走** `.cursor/skills-shared/code-styleguide-skills/styleguide-router/SKILL.md`，并按语言分发到 **`styleguide-kotlin/SKILL.md`**（与 Android Kotlin Style Guide 对齐）。

**落地检查点（执行代码前自检，合并前 IDE/ktlint 若有则跑一遍）：**

| 项 | 依据 |
|----|------|
| 源文件 UTF-8；**缩进仅空格**，每级 **4 空格**（禁止 Tab） | `styleguide-kotlin` |
| **`import` 单组、ASCII 排序**；禁止通配符 `import …*` | 同上 |
| 列宽上限 **100**（`package`/`import`/可复制命令行可例外） | 同上 |
| 与 **本仓库既有文件** 冲突时：以项目现有写法 + 可读性为准（参见 router「冲突裁决」） | `styleguide-router` |

## 目标

将 **面向界面的 XML 布局**（`res/layout/*.xml`）全部迁移为 **Jetpack Compose** 实现；若某功能已存在等价的 Compose 实现，则 **删除 XML 与 ViewBinding 路径，统一保留 Compose**，避免双轨维护。

## 范围说明（重要）

| 类别 | 是否纳入本计划「转 Compose」 |
|------|------------------------------|
| `res/layout/*.xml` | **是** — 迁移到 Compose 后删除布局文件 |
| `res/drawable/*.xml`（shape、selector 等） | **视情况** — 迁移时改为 `Brush`/`Modifier.background`/`painterResource` 等；若仍被主题或非 UI 代码引用可阶段性保留 |
| `AndroidManifest.xml`、`res/xml/*`、`res/values/*.xml` | **否** — 仍使用 XML（非 View 布局），不改为 Compose |

## 验收标准

- [x] `app` 模块 `res/layout/` 下无仍被引用的布局文件；可删除的已删除。（2026-03-28：`res/layout/` 已无 `.xml` 布局文件。）
- [x] 无冗余双实现：同一界面/对话框仅保留 Compose 一套。（2026-03-28 阶段 5：无 layout XML、无 `R.layout`、无 ViewBinding 路径。）
- [x] 新增/改动 Kotlin 符合 **「编码规范」** 与 `code-styleguide-skills`（Kotlin 子 skill）可执行检查点。（迁移过程按 `task_plan.md`「编码规范」执行；合并前仍可跑 IDE/ktlint 若有。）
- [x] `./gradlew :app:assembleDebug`（或项目常规构建命令）通过。（2026-03-28：本地编译验证已通过）
- [ ] 主要流程：启动 → 游戏壳 → 各模式/设置/取色 **手测无回归**。

## 与既有子计划的关系

详细步骤与文件级清单见 **`doc/plans/xml-to-compose/`**（01～06）。本总计划按依赖顺序调度，执行时以子计划为操作手册，并在本目录的 `findings.md` / `progress.md` 记录偏差与结论。

| 子计划 | 主题 |
|--------|------|
| 01 | Splash |
| 02 | 通用对话框 |
| 03 | 找色差 Fragment |
| 04 | 悬浮设置 |
| 05 | GameActivity + Navigation |
| 06 | 清理废弃 XML/Drawable |

## 阶段拆解

### 阶段 1：现状盘点与依赖排序

- [x] 对照 `findings.md` 中的布局清单，确认每文件对应的 Kotlin 入口与是否已有 Compose。
- [x] 标记「双实现」与调用关系（详见 `findings.md` 结项表）。
- [x] **Status:** complete

### 阶段 2：按子计划 01→05 迁移（优先无重复、后合并重复）

- [x] 01 Splash（若已 Compose，仅删残留 `activity_main.xml` 引用与资源）。
- [x] 02 三对话框 → Compose 或复用已有 Compose 组件，删除 `dialog_*.xml` 与 View 版 Dialog。
- [x] 03 `FindDiffColorFragment` + `activity_find_diff_color.xml`。
- [x] 04 `FloatingSettingWindowManager` + `floating_setting.xml`。
- [x] 05 `GameActivity` + `activity_game.xml`（Compose `NavHost` + 各 `*Screen`）。
- [x] **Status:** complete

### 阶段 3：剩余布局与复杂组件

- [x] `SelectPicActivity` + `activity_select_pic.xml` → Compose + 删 XML。
- [x] `HSBColorPicker`：去掉 `merge_hsb_color_picker.xml`，在 `init` 中 `buildUi()` 程序化构建（保留 `SeekBar`/`GradientDrawable` 逻辑）。
- [x] `activity_guess_color.xml`、`item_mode.xml`：确认无引用后删除。
- [x] **Status:** complete

### 阶段 4：子计划 06 清理与构建配置

- [x] 执行 `doc/plans/xml-to-compose/06-cleanup.md`：删废弃 drawable、评估 `viewBinding` / `dataBinding` 开关。（`dataBinding` 未在工程启用；`viewBinding` 已从 `app`、`base` 移除。）
- [x] **Status:** complete

### 阶段 5：回归与结项

- [x] 全量搜索 `layout/`、`databinding`、`ViewBinding` 残留。（见 `findings.md`「阶段 5」）
- [x] 更新 `progress.md` 与本文档勾选状态。
- [x] **Status:** complete（自动化结项；手测见验收标准末项）

## 决策记录

| 决策 | 原因 |
|------|------|
| 保留 Manifest/values/xml 非 layout 资源为 XML | 符合 Android 惯例，与「界面布局 Compose 化」目标一致 |
| 双实现统一保留 Compose | 用户明确要求去 XML 保留 Compose |
| Kotlin 迁移代码遵循 `code-styleguide-skills`（经 `styleguide-router` → `styleguide-kotlin`） | 与 `AGENTS.md` 一致，减少风格漂移与评审成本 |

## 问题与错误记录

| 问题/错误 | 尝试次数 | 解决方式 |
|-----------|----------|----------|
| | | |
