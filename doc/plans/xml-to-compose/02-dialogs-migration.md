# 计划 02：迁移 XML 对话框到 Compose

> 预估工作量：6-9 小时 | 优先级：高 | 依赖：无

## 概述

项目中有 3 个基于 XML 布局的对话框类，均使用 ViewBinding 渲染 UI。这些对话框在多个已迁移的 Compose 页面中仍被调用，迁移后可以消除对 XML 布局的直接依赖。

## 待迁移对话框

### 2.1 CommonAlertDialog

**文件：**
- `app/src/main/kotlin/com/sunzk/colortest/dialog/CommonAlertDialog.kt`
- `app/src/main/res/layout/dialog_common_alert.xml`

**功能：** 单按钮提示对话框（标题 + 内容 + 确认按钮）

**迁移方案：**
- 使用 Compose `AlertDialog` 或自定义 `Dialog` 组件
- 保持与 `LifecycleDialog` 的生命周期集成
- 封装为 `@Composable fun CommonAlertDialog()`

**工作量：** 1-2 小时

---

### 2.2 CommonConfirmDialog

**文件：**
- `app/src/main/kotlin/com/sunzk/colortest/dialog/CommonConfirmDialog.kt`
- `app/src/main/res/layout/dialog_common_confirm.xml`

**功能：** 双按钮确认对话框（标题 + 内容 + 取消/确认按钮）

**迁移方案：**
- 使用 Compose `AlertDialog` 的 `confirmButton` / `dismissButton` 槽位
- 支持 DSL 风格配置（`title = ...`, `message = ...`）
- 封装为 `@Composable fun CommonConfirmDialog()`

**工作量：** 1-2 小时

---

### 2.3 CommonSettlementDialog

**文件：**
- `app/src/main/kotlin/com/sunzk/colortest/dialog/CommonSettlementDialog.kt`
- `app/src/main/res/layout/dialog_mock_color_settlement.xml`

**功能：** 游戏结算对话框（带图标 + CardView + 结果文案 + 双按钮）

**复杂点：**
- 重叠的图标效果（`translationZ`）
- CardView 圆角卡片样式
- 动态切换正确/错误状态

**迁移方案：**
- 使用 Compose `Card` + `Box` 实现重叠图标
- 通过状态控制正确/错误的视觉差异
- 封装为 `@Composable fun CommonSettlementDialog()`

**工作量：** 2-3 小时

---

## 调用方适配

迁移后需要更新以下调用方（当前通过 `dialog.show()` 触发）：

| 调用方 | 对话框 | 当前状态 |
|--------|--------|----------|
| `MockColorFragment` | `CommonSettlementDialog` | 已迁移到 Compose |
| `IntermediateColorFragment` | `CommonSettlementDialog` + `CommonConfirmDialog` | 已迁移到 Compose |
| `FindSameColorFragment` | 暂停对话框 | 已在 Compose 中实现 |

**方案：** 对话框迁移为 `@Composable` 后，改为在调用方通过状态控制显示/隐藏，而非 `dialog.show()`。

## 迁移步骤

1. 逐个实现 Compose 版本的对话框组件
2. 更新调用方，将 `dialog.show()` 替换为 Compose 状态驱动
3. 删除 XML 布局文件
4. 删除旧的对话框类

## 验收标准

- [ ] 3 个对话框均使用 `@Composable` 实现
- [ ] 视觉效果与原 XML 布局一致
- [ ] 所有调用方已适配 Compose 状态驱动方式
- [ ] XML 布局文件已删除
- [ ] 旧对话框类已删除
