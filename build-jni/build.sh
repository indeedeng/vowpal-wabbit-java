#!/usr/bin/env bash

# =============================================================================
#  This script will build vw-wrapper and its dependencies: boost and vowpal_wabbit
#  with needed flags and configurations
#
#  it will download boost and vowpal_wabbit from iternet to transient directry, 
#  if they are not downloaded.
#
#  If you want to build vw-wrapper with different version of vowpall-wabbit
#  you may set  BOOST_SOURCE_DIR or VOWPAL_WABBIT_SOURCE_DIR enviroment variables.
#
#  Result file will be stored in transient/lib/vw_wrapper direcory.
# =============================================================================

set -e
set -x

CURRENT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

TRANSIENT_DIR="$CURRENT_DIR/transient"
BASE_LIB_DIRECTORY="$TRANSIENT_DIR/lib"
BASE_SRC_DIRECTORY="$TRANSIENT_DIR/src"
mkdir -p $BASE_SRC_DIRECTORY

BOOST_LIB_DIR="$BASE_LIB_DIRECTORY/boost"
VW_WRAPPER_LIB_DIR="$BASE_LIB_DIRECTORY/vw_wrapper"

source $CURRENT_DIR/download_dependencies_sources_functions.sh

VW_WRAPPER_SOURCE_DIR="$CURRENT_DIR/../vw_jni/"

if [ -z "$BOOST_SOURCE_DIR" ]; then
  BOOST_SOURCE_DIR="$BASE_SRC_DIRECTORY/boost"
  download_boost_if_needed
fi

if [ -z "$VOWPAL_WABBIT_SOURCE_DIR" ]; then
  VOWPAL_WABBIT_SOURCE_DIR="$BASE_SRC_DIRECTORY/vowpal_wabbit"
  download_vowpal_wabbit_if_needed
fi


clean() {
  rm -rf "$BASE_LIB_DIRECTORY" "$VOWPAL_WABBIT_SOURCE_DIR/.libs"
  mkdir -p $BOOST_LIB_DIR $VOWPAL_WABBIT_LIB_DIR $VW_WRAPPER_LIB_DIR
}

build_boost() {
  cd "$BOOST_SOURCE_DIR"
  ./bootstrap.sh --prefix="$BOOST_LIB_DIR" --with-libraries=program_options
  ./b2 cxxflags=-fPIC link=static install
}

build_vowpal_wabbit() {
  cd "$VOWPAL_WABBIT_SOURCE_DIR"
  ./autogen.sh || true # autogen.sh distributed with VW does not support cust boost install
  CXXFLAGS=" -fPIC -fpermissive" ./configure --with-boost="$BOOST_LIB_DIR"
  make clean
  make
  make test
}

build_vw_wrapper() {
  cd $VW_WRAPPER_SOURCE_DIR
  ./download-m4-plugins.sh
  ./autogenerate-configure.sh
  ./configure --with-boostlib="$BOOST_LIB_DIR" --with-vwlib="$VOWPAL_WABBIT_SOURCE_DIR/vowpalwabbit"
  make clean
  make vw_jni.lib
  mv vw_jni.lib $VW_WRAPPER_LIB_DIR
}


# =============================================================================
#  Main
# =============================================================================

clean

build_boost

build_vowpal_wabbit

build_vw_wrapper
