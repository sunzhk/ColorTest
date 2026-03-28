# 计划 01：迁移 SplashActivity 到 Compose

> 预估工作量：30 分钟 | 优先级：高 | 依赖：无

## 概述

SplashActivity 是应用启动页，当前使用 `setContentView(R.layout.activity_main)` 加载 XML 布局。页面极其简单，仅有两个静态 TextView，无交互逻辑，是最低成本的迁移目标。

## 当前实现

**文件：**
- `app/src/main/kotlin/com/sunzk/colortest/SplashActivity.kt`
- `app/src/main/res/layout/activity_main.xml`

**布局结构：**
```
ConstraintLayout (背景色: @color/app_base)
├── TextView "Color Test" (58sp, bold, 居中偏上)
└── TextView "for 寂书予&柯基是猫" (21sp, 居中偏下)
```

**逻辑：**
- `onCreate` 中设置状态栏颜色
- 延迟 1.5 秒后跳转到 `GameActivity`
- 拦截返回键（不做任何操作）

## 迁移步骤

1. 修改 `SplashActivity.kt`：
   - 将 `setContentView(R.layout.activity_main)` 替换为 `setContent { ... }`
   - 使用 Compose 重建布局：`Column` + 两个 `Text` 组件
   - 保持状态栏颜色设置逻辑不变

2. 删除 `app/src/main/res/layout/activity_main.xml`

3. 验证启动流程正常

## 验收标准

- [ ] SplashActivity 使用 `setContent {}` 渲染 UI
- [ ] 视觉效果与原 XML 布局一致
- [ ] 启动延迟 + 跳转逻辑正常
- [ ] `activity_main.xml` 已删除
