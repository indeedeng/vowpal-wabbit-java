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

source set_up_macos_functions.sh

start_docker() {
  echo $(docker-machine create --driver virtualbox default)
  echo $(docker-machine start default)
  eval $(docker-machine env default)
}

run_docker() {
  local machine=$1
  local script=$2
  docker run --rm -v $(pwd):/build-jni -v $(pwd)/../vw_jni:/vw_jni $machine /bin/bash "$script"
}

# =============================================================================
# Prepare 
# =============================================================================

configure_macos
start_docker

# =============================================================================
# Build Linux shared object 
# =============================================================================

docker build -t vw-linux-build-docker-img  vw-linux-build-docker-img

run_docker "vw-linux-build-docker-img" "/build-jni/build.sh"
mv transient/lib/vw_wrapper/vw_jni.lib ../src/main/resources/lib/vw_jni.Linux.lib

# =============================================================================
# Build Macos shared object 
# =============================================================================

./build.sh
mv transient/lib/vw_wrapper/vw_jni.lib ../src/main/resources/lib/vw_jni.Darwin.lib
