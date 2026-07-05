#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOCAL_ROOT="$PROJECT_ROOT/.local/java17-root"
JDK_HOME="$LOCAL_ROOT/usr/lib/jvm/java-17-openjdk-amd64"
ETC_ROOT="$LOCAL_ROOT/etc/java-17-openjdk"

if [[ ! -x "$JDK_HOME/bin/java" ]]; then
  echo "JDK 17 local nao encontrado em: $JDK_HOME" >&2
  exit 1
fi

while IFS= read -r -d '' link; do
  absolute_target="$(readlink "$link")"
  relative_target="${absolute_target#/etc/java-17-openjdk/}"
  extracted_target="$ETC_ROOT/$relative_target"

  if [[ -e "$extracted_target" ]]; then
    portable_target="$(realpath --relative-to="$(dirname "$link")" "$extracted_target")"
    ln -sfn "$portable_target" "$link"
  fi
done < <(find "$JDK_HOME" -type l -lname '/etc/java-17-openjdk/*' -print0)

echo "Ligacoes internas do JDK 17 local corrigidas."
