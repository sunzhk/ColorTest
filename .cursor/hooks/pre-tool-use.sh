#!/bin/bash
# planning-with-files: Pre-tool-use hook for Cursor

BASE_DIR="doc/plans"
ACTIVE_FILE="${BASE_DIR}/ACTIVE"

ACTIVE_DIR=""
if [ -f "${ACTIVE_FILE}" ]; then
  ACTIVE_DIR="$(tr -d ' \r\n\t' < "${ACTIVE_FILE}")"
fi

PLAN_FILE="${BASE_DIR}/${ACTIVE_DIR}/task_plan.md"
if [ -f "${PLAN_FILE}" ]; then
  head -30 "${PLAN_FILE}" >&2
fi

echo '{"decision": "allow"}'
exit 0
