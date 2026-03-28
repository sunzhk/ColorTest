# 计划 06：清理废弃的 XML 资源

> **更新时间：** 2026-03-28 13:34:57 +0800  
> **最近更新：** 阶段 5 确认：全仓无 layout/ViewBinding 残留；编译已由用户验证通过。

> 预估工作量：2-3 小时 | 优先级：低 | 依赖：计划 01-05 全部完成

## 概述

在所有页面和组件迁移到 Compose 后，项目中会残留大量不再使用的 XML 布局文件和 Drawable 资源。本计划负责清理这些废弃资源，确保项目彻底摆脱 XML 布局依赖。

## 待清理的 XML 布局文件

| 文件 | 原使用方 | 迁移计划 |
|------|----------|----------|
| `activity_main.xml` | SplashActivity | 计划 01 |
| `activity_game.xml` | GameActivity | 计划 05 |
| `activity_find_diff_color.xml` | FindDiffColorFragment | 计划 03 |
| `activity_guess_color.xml` | 未确认 | 直接删除 |
| `dialog_common_alert.xml` | CommonAlertDialog | 计划 02 |
| `dialog_common_confirm.xml` | CommonConfirmDialog | 计划 02 |
| `dialog_mock_color_settlement.xml` | CommonSettlementDialog | 计划 02 |
| `floating_setting.xml` | FloatingSettingWindowManager | 计划 04 |
| `merge_hsb_color_picker.xml` | ColorPicker 相关 | 确认后删除 |
| `item_mode.xml` | ModeSelectFragment | 确认后删除 |

## 待清理的 XML Drawable 文件

| 文件 | 类型 | 说明 |
|------|------|------|
| `dialog_common_bg.xml` | Shape | 对话框背景 |
| `bg_common_bt.xml` | Shape | 按钮背景 |
| `bg_floating_setting.xml` | Shape | 悬浮窗口背景 |
| `bg_common_switch.xml` | Shape | 开关背景 |
| `bg_common_bt_dark.xml` | Shape | 深色按钮背景 |
| `shape_mode_select.xml` | Shape | 模式选择背景 |

## 待清理的 PNG 资源

| 文件 | 说明 |
|------|------|
| `thumb.png`, `thumb2.png` | 滑块图标 |
| `seek_fine_tuning_left.png` | 微调滑块左端 |
| `seek_fine_tuning_right.png` | 微调滑块右端 |

## 清理步骤

1. **确认无引用**：对每个待删除文件执行全局搜索，确认无代码引用
2. **删除 XML 布局文件**：从 `app/src/main/res/layout/` 删除
3. **删除 XML Drawable**：从 `app/src/main/res/drawable/` 删除
4. **删除 PNG 资源**：确认 Compose 中已有替代方案后删除
5. **清理 ViewBinding**：确认 `build.gradle.kts` 中 `viewBinding` 是否仍需要保留
6. **运行构建验证**：确保删除后项目可正常编译

## 验收标准

- [x] 所有废弃 XML 布局文件已删除
- [x] 所有废弃 XML Drawable 已删除（本仓库 `app` 模块 drawable 目录已无 XML；仍被引用的 PNG 未删）
- [ ] 不再需要的 PNG 资源已删除（`thumb2` / `seek_fine_tuning_*` 仍被代码使用，保留）
- [x] 项目可正常编译通过（2026-03-28：用户本地 Gradle 验证通过）
- [x] `viewBinding` 配置已根据需要移除或保留（已从 `app`、`base` 移除）
