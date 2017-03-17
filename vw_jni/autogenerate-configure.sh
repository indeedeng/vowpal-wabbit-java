#!/usr/bin/env bash

# =============================================================================
# This script generates configure script using configure.ac definition.
#
# =============================================================================


automake --add-missing --force-missing || true
autoreconf --install || true
