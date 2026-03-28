#!/bin/bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PLANS_DIR="${ROOT_DIR}/doc/plans"

PLAN_ID="${1:-}"
if [[ -z "${PLAN_ID}" ]]; then
  echo "用法: ./doc/plans/new-plan.sh <plan-id>" >&2
  exit 1
fi
if [[ ! "${PLAN_ID}" =~ ^[a-zA-Z0-9._-]+$ ]]; then
  echo "plan-id 非法：仅允许字母、数字、点号、下划线、连字符。" >&2
  exit 1
fi

PLAN_DIR="${PLANS_DIR}/${PLAN_ID}"
mkdir -p "${PLAN_DIR}"

TASK_PLAN_FILE="${PLAN_DIR}/task_plan.md"
FINDINGS_FILE="${PLAN_DIR}/findings.md"
PROGRESS_FILE="${PLAN_DIR}/progress.md"
ACTIVE_FILE="${PLANS_DIR}/ACTIVE"
NOW="$(date "+%Y-%m-%d %H:%M:%S")"

if [[ ! -f "${TASK_PLAN_FILE}" ]]; then
  cat > "${TASK_PLAN_FILE}" <<EOF2
# Task Plan: ${PLAN_ID}
## 目标

-

## 阶段拆解

### 阶段 1：需求澄清与现状调研

- [ ] 明确用户意图与验收标准
- [ ] 梳理约束条件与边界
- [ ] 将调研结论记录到 findings.md
- **Status:** in_progress

### 阶段 2：方案设计与任务拆分

- [ ] 明确技术方案与实施路径
- [ ] 记录关键决策与取舍理由
- **Status:** pending

### 阶段 3：实施与自测

- [ ] 按计划逐步实施
- [ ] 每步完成后进行最小验证
- **Status:** pending

### 阶段 4：验证与回归

- [ ] 验证需求覆盖与边界场景
- [ ] 将测试结果记录到 progress.md
- **Status:** pending

### 阶段 5：交付与总结

- [ ] 确认交付物完整可用
- [ ] 输出结果与后续建议
- **Status:** pending

## 决策记录

| 决策 | 原因 |
|------|------|
| | |

## 问题与错误记录

| 问题/错误 | 尝试次数 | 解决方式 |
|-----------|----------|----------|
| | 1 | |
EOF2
fi

if [[ ! -f "${FINDINGS_FILE}" ]]; then
  cat > "${FINDINGS_FILE}" <<'EOF2'
# 调研与结论

## 需求摘要

-

## 调研发现

-

## 技术决策

| 决策 | 原因 |
|------|------|
| | |

## 遇到的问题

| 问题 | 处理结果 |
|------|----------|
| | |
EOF2
fi

if [[ ! -f "${PROGRESS_FILE}" ]]; then
  cat > "${PROGRESS_FILE}" <<EOF2
# 进度日志

## 会话：${NOW}

### 阶段 1：需求澄清与现状调研

- **Status:** in_progress
- **Started:** ${NOW}

- 已完成动作：
  -

- 影响文件：
  -

## 测试结果

| 测试项 | 输入 | 预期 | 实际 | 状态 |
|--------|------|------|------|------|
| | | | | |

## 错误日志

-
EOF2
fi

echo "${PLAN_ID}" > "${ACTIVE_FILE}"
echo "计划已初始化: ${PLAN_DIR}"
echo "已切换当前激活计划: ${PLAN_ID}"
echo "已准备文件:"
echo "  - ${TASK_PLAN_FILE}"
echo "  - ${FINDINGS_FILE}"
echo "  - ${PROGRESS_FILE}"
echo "  - （按需）${PLAN_DIR}/execution_brief.md"
echo "  - ${ACTIVE_FILE}"
