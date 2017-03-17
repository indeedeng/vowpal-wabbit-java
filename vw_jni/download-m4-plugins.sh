#!/usr/bin/env bash

# =============================================================================
# This script will download all autoconf plugins used by configure.ac from autoconf-archive 
#
# =============================================================================

set -e
set -x

if [ ! -d 'm4' ]; then
    mkdir 'm4'

    wget -O'm4/ax_jni_include_dir.m4' \
           'http://git.savannah.gnu.org/gitweb/?p=autoconf-archive.git;a=blob_plain;f=m4/ax_jni_include_dir.m4;hb=v2016.09.16'

    wget -O'm4/ax_prog_javac.m4' \
           'http://git.savannah.gnu.org/gitweb/?p=autoconf-archive.git;a=blob_plain;f=m4/ax_prog_javac.m4;hb=v2016.09.16'

    wget -O'm4/ax_prog_javac_works.m4' \
           'http://git.savannah.gnu.org/gitweb/?p=autoconf-archive.git;a=blob_plain;f=m4/ax_prog_javac_works.m4;hb=v2016.09.16'

    wget -O'm4/ax_check_link_flag.m4' \
           'http://git.savannah.gnu.org/gitweb/?p=autoconf-archive.git;a=blob_plain;f=m4/ax_check_link_flag.m4;hb=v2016.09.16'

    wget -O'm4/ax_cxx_compile_stdcxx.m4' \
           'http://git.savannah.gnu.org/gitweb/?p=autoconf-archive.git;a=blob_plain;f=m4/ax_cxx_compile_stdcxx.m4;hb=v2016.09.16'

   wget -O'm4/ax_cxx_compile_stdcxx_11.m4' \
          'http://git.savannah.gnu.org/gitweb/?p=autoconf-archive.git;a=blob_plain;f=m4/ax_cxx_compile_stdcxx_11.m4;hb=v2016.09.16'
fi