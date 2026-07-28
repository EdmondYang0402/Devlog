#!/bin/sh
set -eu

upload_dir="${APP_UPLOAD_DIR:-/app/uploads}"

mkdir -p "$upload_dir"
chown -R appuser:appuser "$upload_dir"

if ! runuser -u appuser -- test -w "$upload_dir"; then
    echo "上传目录不可写: $upload_dir" >&2
    exit 1
fi

exec runuser -u appuser -- sh -c 'exec java $JAVA_OPTS -jar /app/app.jar'
