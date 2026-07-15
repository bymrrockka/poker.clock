#!/usr/bin/env bash
set -Eeuo pipefail

SERVICE_NAME="poker-app.service"
SERVICE_PATH="/etc/systemd/system/${SERVICE_NAME}"

if [ "${EUID}" -eq 0 ]; then
  echo "Exception: do not run this script with sudo/root."
  echo "Run it as the app user. The script will use sudo only when system permissions are required."
  exit 1
fi

if [ -z "${USER:-}" ]; then
  echo "Exception: USER environment variable is not set."
  exit 1
fi

APP_DIR="/home/${USER}/app"
RUN_SCRIPT="${APP_DIR}/scripts/run.sh"

if sudo test -e "${SERVICE_PATH}"; then
  echo "Exception: systemd service file already exists: ${SERVICE_PATH}"
  echo "Remove it manually if you want to recreate it:"
  echo "sudo rm ${SERVICE_PATH}"
  exit 1
fi

sudo tee "${SERVICE_PATH}" > /dev/null <<EOF
[Unit]
Description=Poker App Java Application
Wants=network-online.target
After=network-online.target

[Service]
Type=simple
User=${USER}
WorkingDirectory=${APP_DIR}

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

sudo systemctl daemon-reload
sudo systemctl enable "${SERVICE_NAME}"

echo "Systemd service was created successfully: ${SERVICE_PATH}"
echo "Service was enabled and will start automatically after system restart."
echo ""
echo "To start it now, run:"
echo "sudo systemctl start ${SERVICE_NAME}"
echo ""
echo "To check status, run:"
echo "sudo systemctl status ${SERVICE_NAME}"