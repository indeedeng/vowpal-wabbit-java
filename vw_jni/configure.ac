AC_INIT([vwwrapper], [0.1])
AC_CONFIG_MACRO_DIR([m4])

AM_INIT_AUTOMAKE([foreign])
AC_CONFIG_SRCDIR([configure.ac])
AC_CONFIG_FILES([Makefile])
AC_CANONICAL_HOST

AX_PROG_JAVAC
AX_JNI_INCLUDE_DIR


AC_ARG_WITH(boostlib, 
            AS_HELP_STRING([--with-boostlib], [path to libboost_program_options.a]),
            [ AC_SUBST(BOOST_LIB, "$with_boostlib") ], 
            [ AC_MSG_FAILURE([you need to specify --with-boostlib]) ]
)

AC_ARG_WITH(vwlib, 
            AS_HELP_STRING([--with-vwlib], [path to directory with vowpal wabbit objects]),
            [ AC_SUBST(VW_LIB, "$with_vwlib") ], 
            [ AC_MSG_FAILURE([you need to specify --with-vwlib]) ]
)

for JNI_INCLUDE_DIR in $JNI_INCLUDE_DIRS
do
    INCLUDES="$INCLUDES -I $JNI_INCLUDE_DIR"
done

INCLUDES="$INCLUDES -I $with_boostlib/include -I $with_vwlib"
AC_SUBST(INCLUDES, $INCLUDES)

AC_PROG_CXX
AX_CXX_COMPILE_STDCXX_11([noext])

# Check if it's GNU ld
AX_CHECK_LINK_FLAG([-Wl,-Bstatic -static-libstdc++ -static-libgcc],
                   [VWW_LDFLAGS="-Wl,-Bstatic -static-libstdc++ -static-libgcc"], 
                   [VWW_LDFLAGS="-lz"]
)

case $host_os in
    darwin* )
        AC_SUBST(SHARED_OBJ_FLAGS, "")
        ;;
    *)
        AC_SUBST(SHARED_OBJ_FLAGS, "-fPIC -shared")
        ;;
esac

AC_SUBST(VWW_LDFLAGS, $VWW_LDFLAGS)

AC_OUTPUT
