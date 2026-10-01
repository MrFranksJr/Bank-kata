#!/usr/bin/env bash
set -e

PORT=${PORT:-8080}
BIC=${BIC:-BANKAXXX}
BANK_NAME=${BANK_NAME:-"Bank Alpha"}
SWIFT_HUB_URL=${SWIFT_HUB_URL:-"http://localhost:9000"}

echo "🚀 Starting Participant Bank Node '$BANK_NAME' ($BIC) on port $PORT..."
echo "SWIFT Hub URL: $SWIFT_HUB_URL"

PORT=$PORT BIC=$BIC BANK_NAME="$BANK_NAME" SWIFT_HUB_URL="$SWIFT_HUB_URL" ./gradlew :bank-starter:run
