#!/bin/bash

set -euo pipefail

builtin cd "$(dirname -- "${BASH_SOURCE[0]}")"
project_base="$(pwd)"

./gradlew jar

jar_file=( $(readlink -f build/libs/*.jar) )
[ "${#jar_file[@]}" -eq 1 ] || (echo "Output jar file could not be determined"; exit 1)

mkdir -p buildSim
cd buildSim

sim_package="sim.tar.gz"
t1="$(stat -c %y "$sim_package" || true)"
wget -N "https://github.com/Team766/2020Sim/releases/latest/download/$sim_package" || [ -f "$sim_package" ] || exit 1
t2="$(stat -c %y "$sim_package")"

extracted_dir="files"
if [ "$t1" != "$t2" -o ! -d "$extracted_dir" ]; then
    rm -rf "$extracted_dir"
    mkdir -p "$extracted_dir"
    tar --directory="$extracted_dir" -xf "$sim_package"
fi
cd "$extracted_dir"

exec ./run.sh "$project_base" "$jar_file"