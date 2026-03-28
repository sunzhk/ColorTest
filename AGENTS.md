<!--
UpdatedAt: 2026-03-28 11:17:24 +0800
LatestChange: 已按 planning-with-files-ext 执行 bootstrap（规则、hooks、doc/plans 脚本）；未改动既有 doc/plans/xml-to-compose/ 内容。
-->

# Agent 说明

本仓库通过 Git submodule 引用跨项目共享 Cursor Skills，权威源：<https://github.com/sunzhk/shared-skills>。

## Shared Skills

- `.cursor/skills-shared/code-styleguide-skills/styleguide-router/SKILL.md`
- `.cursor/skills-shared/eng-practices/SKILL.md`
- （推荐）`.cursor/skills-shared/unit-test-guide-skills/unit-test-router/SKILL.md`
- （可选，直达某端）`.cursor/skills-shared/unit-test-guide-skills/unit-test-android/SKILL.md` 等同目录下 `unit-test-ios`、`unit-test-wechat-miniprogram`
- `.cursor/skills-shared/planning-with-files-ext/SKILL.md`（本仓库已 bootstrap：见下「Planning-with-files 落地」）

AI 侧路由与优先级以子模块内 `README.ai.md` 为准。

### 使用约定

- 风格问题优先走 `styleguide-router`。
- 评审流程、评论策略、冲突处理优先走 `eng-practices`。
- 单元测试规范与补测策略优先走 `unit-test-router`；若平台已明确，可直达对应 `unit-test-*` 子 skill。
- 需要升级/重装 planning 模板时：对照 `planning-with-files-ext/SKILL.md`，在仓库根执行子模块内 `bootstrap.sh`；若已有 `.cursor/hooks.json` 与模板不一致，需先手动合并再运行。

## Planning-with-files 落地（本仓库）

已执行：`bash .cursor/skills-shared/planning-with-files-ext/bootstrap.sh`（目标根为仓库根）。

产物：

- `.cursor/rules/planning-with-files.mdc`
- `.cursor/hooks.json` 与 `.cursor/hooks/*.sh`
- `doc/plans/new-plan.sh`、`doc/plans/plan.sh`（可执行）

说明：既有迁移文档 `doc/plans/xml-to-compose/*.md` **未修改**；新计划建议使用三文件目录 `doc/plans/<plan-id>/`（`task_plan.md` / `findings.md` / `progress.md`），与 `plan.sh`、`ACTIVE` 指针配合。列表/切换：`./doc/plans/plan.sh list`、`./doc/plans/plan.sh use <plan-id>`。

## 克隆与更新子模块（给协作者 / CI）

```bash
git submodule update --init --recursive
```

跟进上游共享技能：

```bash
git submodule update --remote --recursive
```

回归验证后提交主仓库中子模块指针变更。
