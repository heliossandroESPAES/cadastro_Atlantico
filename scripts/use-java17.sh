#!/usr/bin/env bash

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
export JAVA_HOME="$PROJECT_ROOT/.local/java17-root/usr/lib/jvm/java-17-openjdk-amd64"
export PATH="$JAVA_HOME/bin:$PATH"
hash -r 2>/dev/null || true

echo "JAVA_HOME=$JAVA_HOME"
java --version
