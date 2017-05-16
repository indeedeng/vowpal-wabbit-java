Vowpal Wabbit Wrapper
====================

[Vowpal wabbit](https://github.com/JohnLangford/vowpal_wabbit/wiki) java wrapper and OOP api for it.

Motivation
----------

[Vowpal wabbit](https://github.com/JohnLangford/vowpal_wabbit/wiki) is widely used machine learning package.

One one hand it is very production friendly, on another hand it has the implementation of state of art of modern machine learning research.

It has bindings to a lot of programing languages including java. However it's java binding has following problems:

 - It has a bug because of which saved model may have incorrect weights; and
 - it requires boost library to be installed one every machine where this wrapper is used, which is not always feasible; and
 - Its API is not very clean.

In this project we base on existing java wrapper, but address described issues.

Build
-----

To build this library run command:

```
mvn clean install
```

Build c++ binary
----------------

Notice that we distribute already built c++ binaries together with the code.
Please, refer [build-jni/README.md](build-jni/README.md) for instructions if you want to rebuild them.

Dependencies
------------
 - guava
 - log4j

Use it
------

For general advices on how to train vowpal wabbit model, please refer to official [vowpal wabbit wiki] (https://github.com/JohnLangford/vowpal_wabbit/wiki).

To learn how to use this wrapper and its java API, you may check [API javadocs](http://opensource.indeedeng.io/vowpal-wabbit-java).
Also, integration tests may work as a reference of how to use this api.

Check them:

 - [Twitter Sentiment analysis](src/test/java/com/indeed/vw/wrapper/integration/tests/TestOnTwitterSentimentDataset.java)
 - [Movie lens 1M](src/test/java/com/indeed/vw/wrapper/integration/tests/TestOnMovieLensDataset.java)

Tested platform
---------------

We have tested this wrapper on following platforms:

 - OS X Yosemite
 - Ubuntu 14
 - Enterprise Linux 5
 - Enterprise Linux 6
 - CentOS 5
 - CentOS 6
 - CentOS 7

License
-------
- This library is distributed under [The Apache Software License, Version 2.0](LICENSE).
- VW binaries are distributed under [BSD (revised) license](VW_LICENSE)
