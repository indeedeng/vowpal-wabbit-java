#!/usr/bin/env bash
# =============================================================================
#  This script was mainly written by Jon Morra.
#  It contains helper function to configure MacOS build enviroment.
#
#  Check original version in https://github.com/JohnLangford/vowpal_wabbit/blob/8.2.1/java/src/main/bin/build.sh
# =============================================================================

# =============================================================================
#  Constants:
# =============================================================================
__not_darwin=1
__brew_not_installed=2



# =============================================================================
#  Function Definitions:
# =============================================================================

# -----------------------------------------------------------------------------
#  Print red text to stderr.
# -----------------------------------------------------------------------------
red() {
  # https://linuxtidbits.wordpress.com/2008/08/11/output-color-on-bash-scripts/
  echo >&2 "$(tput setaf 1)${1}$(tput sgr0)"
}

# -----------------------------------------------------------------------------
#  Print yellow text to stderr.
# -----------------------------------------------------------------------------
yellow() {
  echo >&2 "$(tput setaf 3)${1}$(tput sgr0)"
}

die() { red $2; exit $1; }

# -----------------------------------------------------------------------------
#  Check that the OS is OS X.  If not, die.  If so, check that brew is
#  installed.  If brew is not installed, ask the user if they want to install.
#  If so, attempt to install.  After attempting install, check for existence.
#  If it still doesn't exist, fail.
# -----------------------------------------------------------------------------
check_brew_installed() {
  local os=$(uname)
  if [[ "$os" != "Darwin" ]]; then
    die $__not_darwin "Build script only supported on OS X.  OS=${os}.  Aborting ..."
  else
    if ! brew help 1>/dev/null 2>/dev/null; then
      red "brew not installed.  To install: Y or N?"
      read should_install
      if [[ "Y" == "${should_install^^}" ]]; then
        ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
      fi
      if ! brew help 1>/dev/null 2>/dev/null; then
        die $__brew_not_installed "brew not installed.  Aborting ..."
      fi
    fi
  fi
}

install_brew_cask() {
  if ! brew cask 1>/dev/null 2>/dev/null; then
    yellow "Installing brew-cask..."
    brew install caskroom/cask/brew-cask
  fi
}

install_brew_app() {
  local app=$1
  if ! brew list | grep $app 1>/dev/null; then
    yellow "installing brew app: $app"
    brew install $app
  fi
}

install_cask_app() {
  local app=$1
  if ! brew cask list | grep $app 1>/dev/null; then
    yellow "installing brew cask app: $app"
    brew cask install $app
  fi
}


install_on_mac() {
  sh -c "$make_boost"
  CPPFLAGS=\" -fPIC -fpermissive\" ./autogen.sh
  ./configure  --with-boost-libdir=/usr/local/lib;
  make clean
  make
  cd java
  make clean
  BOOST_LIBRARY=/usr/local/lib/libboost_program_options.a UNAME=Darwin make target/vw_jni.lib
  cd ..
  mv java/target/vw_jni.lib java/target/vw_jni.Darwin.lib
}
# =============================================================================
#  Configure and docker functions
# =============================================================================

configure_macos() {
  check_brew_installed
  install_brew_cask
  install_brew_app "wget"
  install_brew_app "bash"
  install_brew_app "libtool"
  install_brew_app "autoconf"
  install_brew_app "automake"
  install_cask_app "virtualbox"
  install_brew_app "docker-machine"
  install_brew_app "docker"
}


