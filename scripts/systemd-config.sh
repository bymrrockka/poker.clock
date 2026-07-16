#!/usr/bin/env bash
set -Eeuo pipefail

SERVICE_NAME="poker-app.service"
SERVICE_PATH="/etc/systemd/system/${SERVICE_NAME}"
SUDOERS_NAME="poker-app-service"
SUDOERS_PATH="/etc/sudoers.d/${SUDOERS_NAME}"

SYSTEMCTL_BIN="$(command -v systemctl)"

if [ "${EUID}" -eq 0 ]; then
  echo "Exception: do not run this script with sudo/root."
  echo "Run it as the app user. The script will use sudo only when system permissions are required."
  exit 1
fi

if [ -z "${APP_USER:-}" ]; then
  echo "Exception: USER environment variable is not set."
  exit 1
fi

if [ -z "${SYSTEMCTL_BIN}" ]; then
  echo "Exception: systemctl was not found."
  exit 1
fi

APP_DIR="/home/${APP_USER}/app"
RUN_SCRIPT="${APP_DIR}/scripts/run.sh"

if sudo test -e "${SERVICE_PATH}"; then
  echo "Exception: systemd service file already exists: ${SERVICE_PATH}"
  echo "Remove it manually if you want to recreate it:"
  echo "sudo rm ${SERVICE_PATH}"
  exit 1
fi

if sudo test -e "${SUDOERS_PATH}"; then
  echo "Exception: sudoers file already exists: ${SUDOERS_PATH}"
  echo "Remove it manually if you want to recreate it:"
  echo "sudo rm ${SUDOERS_PATH}"
  exit 1
fi

SERVICE_TEMP_FILE="$(mktemp)"
SUDOERS_TEMP_FILE="$(mktemp)"

cleanup() {
  rm -f "${SERVICE_TEMP_FILE}" "${SUDOERS_TEMP_FILE}"
}

trap cleanup EXIT

cat > "${SERVICE_TEMP_FILE}" <<EOF
[Unit]
Description=Poker App Java Application
Wants=network-online.target
After=network-online.target

[Service]
Type=simple
User=${APP_USER}
WorkingDirectory=${APP_DIR}

EnvironmentFile=${APP_DIR}/.env
Environment="APP_DIR=${APP_DIR}"
Environment="JAVA_BIN=/usr/bin/java"
Environment="JAVA_OPTS=-Xmx512m -Djava.net.preferIPv6Addresses=true"

ExecStart=${RUN_SCRIPT} start-foreground
ExecStop=${RUN_SCRIPT} stop

Restart=always
RestartSec=10
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
EOF

cat > "${SUDOERS_TEMP_FILE}" <<EOF
${APP_USER} ALL=(root) NOPASSWD: ${SYSTEMCTL_BIN} start ${SERVICE_NAME}
${APP_USER} ALL=(root) NOPASSWD: ${SYSTEMCTL_BIN} stop ${SERVICE_NAME}
${APP_USER} ALL=(root) NOPASSWD: ${SYSTEMCTL_BIN} restart ${SERVICE_NAME}
${APP_USER} ALL=(root) NOPASSWD: ${SYSTEMCTL_BIN} status ${SERVICE_NAME}
EOF

if [ ! -s "${SUDOERS_TEMP_FILE}" ]; then
  echo "Exception: generated sudoers file is empty."
  exit 1
fi

sudo visudo -cf "${SUDOERS_TEMP_FILE}"

sudo install -o root -g root -m 0644 "${SERVICE_TEMP_FILE}" "${SERVICE_PATH}"
sudo install -o root -g root -m 0440 "${SUDOERS_TEMP_FILE}" "${SUDOERS_PATH}"

sudo systemctl daemon-reload
sudo systemctl enable "${SERVICE_NAME}"

echo "Systemd service was created successfully: ${SERVICE_PATH}"
echo "Limited sudoers permissions were created successfully: ${SUDOERS_PATH}"
echo ""
echo "The user '${APP_USER}' can now manage only this service with:"
echo "sudo systemctl start ${SERVICE_NAME}"
echo "sudo systemctl stop ${SERVICE_NAME}"
echo "sudo systemctl restart ${SERVICE_NAME}"
echo "sudo systemctl status ${SERVICE_NAME}"
echo ""
echo "Service was enabled and will start automatically after system restart."
echo ""
echo "To start it now, run:"
echo "sudo systemctl start ${SERVICE_NAME}"