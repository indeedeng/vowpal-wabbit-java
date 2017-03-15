#!/usr/bin/env bash

# =============================================================================
# Run this script only on MacOS.  
#
# This script:
# - installs packages required for building SO files;
# - builds in docker Linux verion of shared object;
# - builds Macos version of share object.
# =============================================================================

set -e
set -x

source install-required-software-on-macos.sh

# =============================================================================
# Prepare 
# =============================================================================

configure_macos
start_docker

# =============================================================================
# Build Linux shared object 
# =============================================================================

docker build -t vw-linux-build-docker-img  vw-linux-build-docker-img

run_docker "vw-linux-build-docker-img" "/build-scripts/build.sh"
mv transient/lib/vw_wrapper/vw_jni.lib ../resources/lib/vw_jni.Linux.lib

# =============================================================================
# Build Macos shared object 
# =============================================================================

./build.sh
mv transient/lib/vw_wrapper/vw_jni.lib ../resources/lib/vw_jni.Darwin.lib
