How to build vw_jni shared objects
==================================

build for all platforms
-----------------------

We have built two shared objects with jni wrapper:

 - 64 bit Linux version
 - OS X version

Both objects have linked statically dependencies: vowpal-wabbit and boost_options.

To reproduce build process, you need a machine with OS X.

On this machine run command.
```
./build_everything_on_macos.sh
```
This command

 - will install all the software needed, and
 - will run `build.sh` script in `vw-linux-build-docker-img` docker image
 - will run `build.sh` script under OS X.

 You may find result files in `${PROJECT_DIR}/src/main/resources/lib`

build for a specific platform
------------------------------

You may want to build shared object for a specific platform - e.g. 32 bit linux.

In this case you need a machine of this platform.

On this machine run:
```
./build.sh
```

This script

- will download boost and vowpal wabbit, and
- apply indeed patches on vowpal wabbit, and
- build boost and vowpal wabbit with specific flags, and
- build jni wrapper shared object with boost and vowpal wabbit linked statically.

Result file is `transient/lib/vw_wrapper/vw_jni.lib`.
