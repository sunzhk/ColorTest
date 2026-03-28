# 计划 03：迁移 FindDiffColorFragment 到 Compose

> 预估工作量：3-4 小时 | 优先级：中 | 依赖：无

## 概述

`FindDiffColorFragment` 是最后一个仍在使用 XML 布局的 Fragment。页面包含一个自定义 View `FindDiffView`，用于渲染颜色方块网格。迁移策略为：Fragment 外层迁移到 Compose，`FindDiffView` 先通过 `AndroidView` 互操作保留。

## 当前实现

**文件：**
- `app/src/main/kotlin/com/sunzk/colortest/game/findDiffColor/FindDiffColorFragment.kt`
- `app/src/main/res/layout/activity_find_diff_color.xml`
- `app/src/main/kotlin/com/sunzk/colortest/view/FindDiffView.kt`（自定义 View，249 行）

**布局结构：**
```
ConstraintLayout
├── ImageView ivLevelLeft (难度左箭头)
├── TextView tvLevel (当前难度数字)
├── ImageView ivLevelRight (难度右箭头)
├── FindDiffView findDiffColor (颜色方块网格)
└── Button btChange (换一题按钮)
```

**数据流：**
- `FindDiffColorViewModel` 暴露 `MutableStateFlow<FindDiffColorPageData>`
- `pageData` 包含：`level`, `countPerSide`, `baseColor`, `diffColor`, `diffIndex`
- 通过 `collect` 订阅数据变化更新 UI

## 迁移方案

### 推荐：分两步进行

**第一步：迁移 Fragment 外层 + 控制区域**
- 使用 `ComposeView` + `setContent {}` 替代 XML 布局
- 难度选择器（左箭头、数字、右箭头）用 Compose Row 实现
- 换一题按钮用 Compose Button 实现
- `FindDiffView` 通过 `AndroidView` 包装嵌入

**第二步（可选）：重写 FindDiffView 为 Compose Canvas**
- 使用 `Canvas` + `drawRect` 绘制颜色网格
- 使用 `pointerInput` 处理点击事件
- 参考 `SortColorView.kt`（已完全 Compose 实现）的模式

## 迁移步骤

1. 将 `FindDiffColorFragment.onCreateView()` 改为返回 `ComposeView`
2. 用 Compose 重建控制区域（难度选择 + 换一题按钮）
3. 用 `AndroidView(factory = { FindDiffView(it) })` 嵌入自定义 View
4. 通过 `update {}` 块将 Compose 状态传递给 `FindDiffView`
5. 删除 `activity_find_diff_color.xml`

## 验收标准

- [x] Fragment 使用 `ComposeView` + `setContent {}`
- [ ] 难度选择和按钮逻辑正常（需手测）
- [ ] 颜色网格显示和点击交互正常（需手测）
- [x] `activity_find_diff_color.xml` 已删除
- [x] ViewBinding 引用已移除

> **更新时间：** 2026-03-28 11:51:15 +0800  
> **最近更新：** 代码侧已完成迁移；手测项待本地安装验证。
