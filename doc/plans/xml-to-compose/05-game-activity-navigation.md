# 计划 05：迁移 GameActivity 到 Compose Navigation

> **更新时间：** 2026-03-28 12:46:45 +0800  
> **最近更新：** 已落地：`GameActivity` + `GameNavHost` + 6 个 `*Screen`；已删除 `activity_game.xml`、`game_navigation.xml` 及原 Fragment 类。手测与完整构建请本地确认。

> 预估工作量：8-12 小时 | 优先级：中 | 依赖：计划 03 完成

## 概述

`GameActivity` 是游戏主页面，原使用 Navigation Component（XML navigation graph + FragmentContainerView）管理 6 个游戏 Fragment。现已改为 **Compose Navigation**（`NavHost` + `composable`），各页面为独立 `@Composable` Screen，`ViewModel` 由 `viewModel()` 在对应 `composable { }` 内获取。

## 当前架构（已实现）

```
GameActivity (setContent {})
└── GameNavHost
    └── NavHost (Compose Navigation)
        ├── mode_select → ModeSelectScreen
        ├── mock_color → MockColorScreen
        ├── intermediate_color → IntermediateColorScreen
        ├── find_diff_color → FindDiffColorScreen
        ├── find_same_color → FindSameColorScreen
        └── sort_color → SortColorScreen
```

**路由常量：** `game/navigation/GameRoutes.kt`  
**模式列表导航：** `RouteInfo.GameMap` 使用 `route: String`（原 `R.id.action_*` 已移除）。

## 验收标准

- [x] `GameActivity` 使用 `setContent {}` + `NavHost`
- [x] 6 个游戏页面均作为 `@Composable` Screen 存在
- [ ] 页面间导航、返回、系统返回键 **手测** 正常
- [x] `game_navigation.xml` 已删除
- [x] `activity_game.xml` 已删除
- [x] 旧 Fragment 类已删除
