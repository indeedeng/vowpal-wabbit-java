#!/usr/bin/env bash

# =============================================================================
#  Helper functions to download dependencies
# =============================================================================


# Notice that we patched vowpal wabbit with a bug fix. 
# Because of this bug weight in final regressor would be incorrect
download_vowpal_wabbit_if_needed() {
  if [ ! -d "$VOWPAL_WABBIT_SOURCE_DIR" ]; then
    git clone https://github.com/JohnLangford/vowpal_wabbit.git "$VOWPAL_WABBIT_SOURCE_DIR"
    cd $VOWPAL_WABBIT_SOURCE_DIR
    git checkout 10bd09ab06f59291e04ad7805e88fd3e693b7159 -b indeed-vw-patch
    git apply "$CURRENT_DIR/indeed-vw-patch.diff"
  fi
}

download_boost_if_needed() {
  if [ ! -d "$BOOST_SOURCE_DIR" ]; then
    wget --no-check-certificate -q -O "$TRANSIENT_DIR/boost_1_61_0.tar.gz" http://sourceforge.net/projects/boost/files/boost/1.61.0/boost_1_61_0.tar.gz/download
    tar -xzvf "$TRANSIENT_DIR/boost_1_61_0.tar.gz" 
    mv boost_1_61_0 "$BOOST_SOURCE_DIR"  
  fi
}
