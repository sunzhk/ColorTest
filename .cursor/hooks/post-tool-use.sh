#!/bin/bash
# planning-with-files: Post-tool-use hook for Cursor

BASE_DIR="doc/plans"
ACTIVE_FILE="${BASE_DIR}/ACTIVE"

ACTIVE_DIR=""
if [ -f "${ACTIVE_FILE}" ]; then
  ACTIVE_DIR="$(tr -d ' \r\n\t' < "${ACTIVE_FILE}")"
fi

PLAN_FILE="${BASE_DIR}/${ACTIVE_DIR}/task_plan.md"
PROGRESS_FILE="${BASE_DIR}/${ACTIVE_DIR}/progress.md"

if [ -f "${PLAN_FILE}" ]; then
  echo "[planning-with-files] Update ${PROGRESS_FILE} with what you just did. If a phase is now complete, update ${PLAN_FILE} status."
fi
exit 0
