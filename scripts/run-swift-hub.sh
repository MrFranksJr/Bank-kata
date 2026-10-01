#!/usr/bin/env bash
set -e

PORT=${PORT:-9000}

echo "🌐 Starting Central SWIFT Network Hub & Live Scoreboard on port $PORT..."
echo "👉 Dashboard available at: http://localhost:$PORT"

PORT=$PORT ./gradlew :swift-hub:run
