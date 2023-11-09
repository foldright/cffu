#!/bin/bash
set -eEuo pipefail
cd "$(dirname "$(readlink -f "$0")")"/..

log_and_run() {
  if [[ -t 1 ]]; then
    printf '\e[1;33;44m%s\e[0m\n' "run command(PWD: $PWD): $*"
  else
    printf '%s\n' "run command(PWD: $PWD): $*"
  fi

  time "$@"
}


# quarto render README.md -o index.html -s
pandoc README.md -o index.html -s --metadata title='cffu API docs'
sed -i '/id="cffu-api-docs"/d' index.html
