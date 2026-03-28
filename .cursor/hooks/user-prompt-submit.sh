#!/bin/bash
# planning-with-files: User prompt submit hook for Cursor
# Injects plan context on every user message.

BASE_DIR="doc/plans"
ACTIVE_FILE="${BASE_DIR}/ACTIVE"

extract_prompt_from_stdin() {
  local stdin_content
  stdin_content="$(cat 2>/dev/null || true)"
  if [ -z "${stdin_content}" ]; then
    echo ""
    return
  fi

  local parsed
  parsed="$(printf '%s' "${stdin_content}" | sed -nE 's/.*"prompt"[[:space:]]*:[[:space:]]*"([^"]*)".*/\1/p' | head -n 1)"
  if [ -z "${parsed}" ]; then
    parsed="$(printf '%s' "${stdin_content}" | sed -nE 's/.*"message"[[:space:]]*:[[:space:]]*"([^"]*)".*/\1/p' | head -n 1)"
  fi
  if [ -z "${parsed}" ]; then
    parsed="${stdin_content}"
  fi
  echo "${parsed}"
}

parse_plan_id_from_prompt() {
  local prompt_text="${1:-}"
  if [ -z "${prompt_text}" ]; then
    echo ""
    return
  fi

  local plan_id
  plan_id="$(printf '%s' "${prompt_text}" | sed -nE 's/.*\[(计划|plan)[[:space:]]*:[[:space:]]*([a-zA-Z0-9._-]+)\].*/\2/p' | head -n 1)"
  echo "${plan_id}"
}

switch_active_if_needed() {
  local plan_id="${1:-}"
  if [ -z "${plan_id}" ]; then
    return
  fi

  local plan_dir="${BASE_DIR}/${plan_id}"
  if [ ! -d "${plan_dir}" ]; then
    echo "[planning-with-files] 提示中指定的计划不存在，忽略切换: ${plan_id}" >&2
    return
  fi

  echo "${plan_id}" > "${ACTIVE_FILE}"
  echo "[planning-with-files] 已根据提示切换 ACTIVE: ${plan_id}" >&2
}

PROMPT_FROM_STDIN="$(extract_prompt_from_stdin)"
PLAN_ID_FROM_PROMPT="$(parse_plan_id_from_prompt "${PROMPT_FROM_STDIN}")"
switch_active_if_needed "${PLAN_ID_FROM_PROMPT}"

ACTIVE_DIR=""
if [ -f "${ACTIVE_FILE}" ]; then
  ACTIVE_DIR="$(tr -d ' \r\n\t' < "${ACTIVE_FILE}")"
fi

PLAN_FILE="${BASE_DIR}/${ACTIVE_DIR}/task_plan.md"
FINDINGS_FILE="${BASE_DIR}/${ACTIVE_DIR}/findings.md"
PROGRESS_FILE="${BASE_DIR}/${ACTIVE_DIR}/progress.md"

if [ -f "${PLAN_FILE}" ]; then
  echo "[planning-with-files] ACTIVE PLAN (${ACTIVE_DIR}) — current state:"
  head -50 "${PLAN_FILE}"
  echo ""
  echo "=== recent progress ==="
  tail -20 "${PROGRESS_FILE}" 2>/dev/null
  echo ""
  echo "[planning-with-files] Read ${FINDINGS_FILE} for research context. Continue from the current phase."
fi
exit 0
