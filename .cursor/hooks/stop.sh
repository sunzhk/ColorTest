#!/bin/bash
# planning-with-files: Stop hook for Cursor

BASE_DIR="doc/plans"
ACTIVE_FILE="${BASE_DIR}/ACTIVE"

ACTIVE_DIR=""
if [ -f "${ACTIVE_FILE}" ]; then
  ACTIVE_DIR="$(tr -d ' \r\n\t' < "${ACTIVE_FILE}")"
fi

PLAN_FILE="${BASE_DIR}/${ACTIVE_DIR}/task_plan.md"
if [ ! -f "${PLAN_FILE}" ]; then
  exit 0
fi

TOTAL=$(grep -cE "^### (Phase|阶段)" "${PLAN_FILE}" || true)
COMPLETE=$(grep -cF "**Status:** complete" "${PLAN_FILE}" || true)
IN_PROGRESS=$(grep -cF "**Status:** in_progress" "${PLAN_FILE}" || true)
PENDING=$(grep -cF "**Status:** pending" "${PLAN_FILE}" || true)

if [ "${COMPLETE}" -eq 0 ] && [ "${IN_PROGRESS}" -eq 0 ] && [ "${PENDING}" -eq 0 ]; then
  COMPLETE=$(grep -c "\[complete\]" "${PLAN_FILE}" || true)
  IN_PROGRESS=$(grep -c "\[in_progress\]" "${PLAN_FILE}" || true)
  PENDING=$(grep -c "\[pending\]" "${PLAN_FILE}" || true)
fi

: "${TOTAL:=0}"
: "${COMPLETE:=0}"

if [ "${COMPLETE}" -eq "${TOTAL}" ] && [ "${TOTAL}" -gt 0 ]; then
  echo "{\"followup_message\": \"[planning-with-files] ALL PHASES COMPLETE (${COMPLETE}/${TOTAL}) for plan '${ACTIVE_DIR}'. If the user has additional work, add new phases to ${PLAN_FILE} before starting.\"}"
else
  echo "{\"followup_message\": \"[planning-with-files] Task incomplete (${COMPLETE}/${TOTAL} phases done) for plan '${ACTIVE_DIR}'. Update doc/plans/${ACTIVE_DIR}/progress.md, then read ${PLAN_FILE} and continue working on remaining phases.\"}"
fi
exit 0
