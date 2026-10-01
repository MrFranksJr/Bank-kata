#!/usr/bin/env bash
set -e

PORT=${1:-8080}

echo "🚇 Exposing local port $PORT to public internet via localtunnel..."
npx localtunnel --port "$PORT"
