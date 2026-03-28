# 计划 04：迁移悬浮设置窗口到 Compose

> **更新时间：** 2026-03-28 12:22:45 +0800  
> **最近更新：** 代码迁移已完成：`FloatingSettingScreen` + `ComposeView` 挂载；`floating_setting.xml` 与 `bg_floating_setting.xml` 已删除。手测与完整 Gradle 构建待本地确认。

> 预估工作量：3-4 小时 | 优先级：中 | 依赖：无

## 概述

`FloatingSettingWindowManager` 管理一个悬浮在应用界面上的设置窗口，当前使用 XML 布局和 ViewBinding。窗口支持拖拽移动、折叠/展开，并提供 BGM 开关、深色模式切换、色彩选择器类型切换等功能。

## 当前实现

**文件：**
- `app/src/main/kotlin/com/sunzk/colortest/tools/FloatingSettingWindowManager.kt`
- ~~`app/src/main/res/layout/floating_setting.xml`~~（已删除）
- ~~`app/src/main/res/drawable/bg_floating_setting.xml`~~（已删除；齿轮外框由 Compose `commonButtonStyle()` 替代）
- `app/src/main/res/drawable/bg_common_switch.xml`（开关背景；Compose 版改用 `icon_common_switch_on/off` mipmap，本 selector 仍可为其他 View 保留）

**功能：**
- 悬浮窗口：挂载到 Activity 的 `decorView` 上
- 拖拽移动：手动处理 `ACTION_DOWN/ACTION_MOVE/ACTION_UP`
- 折叠/展开：切换图标和内容区域的可见性
- 设置项：BGM 开关、深色模式、色彩选择器类型

## 迁移方案

### 核心挑战

悬浮窗口的挂载机制（添加到 decorView）与 Compose 的声明式模型不同。

### 推荐方案

1. 将设置内容 UI 迁移到 `@Composable fun FloatingSettingScreen()`
2. 拖拽逻辑：本实现保留在 `ComposeView` 上的 `ItemViewTouchListener`（与原根 View 行为一致）；若后续需与 Compose 手势完全统一，可再评估 `pointerInput`。
3. 折叠/展开使用 `AnimatedVisibility`
4. 窗口挂载保持现有机制，内部嵌入 `ComposeView`

## 迁移步骤

1. 创建 `@Composable fun FloatingSettingScreen()` 实现设置内容 UI
2. 使用 `AnimatedVisibility` 实现折叠/展开动画
3. 修改 `FloatingSettingWindowManager` 使用 Compose 版本
4. 删除 `floating_setting.xml` 及仅被其引用的 `bg_floating_setting.xml`

## 验收标准

- [x] 悬浮设置窗口使用 Compose 渲染
- [ ] 拖拽移动功能正常（待手测）
- [x] 折叠/展开动画正常（`AnimatedVisibility`）
- [ ] BGM、深色模式、色彩选择器切换功能正常（待手测）
- [x] `floating_setting.xml` 已删除
- [x] ViewBinding 引用已移除
