#!/bin/bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
PLANS_DIR="${ROOT_DIR}/doc/plans"
ACTIVE_FILE="${PLANS_DIR}/ACTIVE"
NEW_PLAN_SCRIPT="${PLANS_DIR}/new-plan.sh"

usage() {
  cat <<'EOT'
用法:
  ./doc/plans/plan.sh list
  ./doc/plans/plan.sh use <plan-id>
  ./doc/plans/plan.sh new <plan-id>
EOT
}

is_valid_plan_id() {
  local plan_id="${1:-}"
  [[ "${plan_id}" =~ ^[a-zA-Z0-9._-]+$ ]]
}

read_active() {
  if [[ -f "${ACTIVE_FILE}" ]]; then
    tr -d ' \r\n\t' < "${ACTIVE_FILE}"
  fi
}

cmd_list() {
  local active
  active="$(read_active)"
  local found=0
  echo "计划列表（目录: ${PLANS_DIR}）"
  if [[ -n "${active}" ]]; then
    echo "当前 ACTIVE: ${active}"
  else
    echo "当前 ACTIVE: （未设置）"
  fi
  echo
  local dir
  for dir in "${PLANS_DIR}"/*; do
    [[ -d "${dir}" ]] || continue
    local plan_id
    plan_id="$(basename "${dir}")"
    [[ "${plan_id}" == "." || "${plan_id}" == ".." ]] && continue
    found=1
    if [[ "${plan_id}" == "${active}" ]]; then
      echo "* ${plan_id} (ACTIVE)"
    else
      echo "* ${plan_id}"
    fi
  done
  if [[ "${found}" -eq 0 ]]; then
    echo "（暂无计划目录）"
  fi
}

cmd_use() {
  local plan_id="${1:-}"
  if [[ -z "${plan_id}" ]]; then
    echo "错误: 缺少 plan-id。" >&2
    usage >&2
    exit 1
  fi
  if ! is_valid_plan_id "${plan_id}"; then
    echo "错误: plan-id 非法，仅允许字母、数字、点号、下划线、连字符。" >&2
    exit 1
  fi
  local plan_dir="${PLANS_DIR}/${plan_id}"
  if [[ ! -d "${plan_dir}" ]]; then
    echo "错误: 计划不存在: ${plan_id}" >&2
    exit 1
  fi
  echo "${plan_id}" > "${ACTIVE_FILE}"
  echo "已切换当前激活计划: ${plan_id}"
}

cmd_new() {
  local plan_id="${1:-}"
  if [[ ! -x "${NEW_PLAN_SCRIPT}" ]]; then
    echo "错误: new-plan.sh 不存在或不可执行: ${NEW_PLAN_SCRIPT}" >&2
    exit 1
  fi
  "${NEW_PLAN_SCRIPT}" "${plan_id}"
}

main() {
  local action="${1:-}"
  case "${action}" in
    list)
      cmd_list
      ;;
    use)
      cmd_use "${2:-}"
      ;;
    new)
      cmd_new "${2:-}"
      ;;
    *)
      usage
      exit 1
      ;;
  esac
}

main "$@"
