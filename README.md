Vowpal Wabbit Wrapper
====================
 
A java wrapper and friendly API for the [Vowpal wabbit](https://github.com/JohnLangford/vowpal_wabbit/wiki) machine learning package.
 
Background
----------
 
 
The Vowpal Wabbit (VP) package is very production friendly and it implements the state of the art in modern machine learning research.
 
The existing java binding for VP has drawbacks:
 
 - Because of a bug, saved models may have incorrect weights
 - It requires the boost library to be installed on every machine where the wrapper is used, which is not always feasible
 - its API is  low-level, requiring you to operate with strings instead of providing a more convenient domain abstraction
 
This project addresses these drawbacks.
 
Building the library
-----
 
To build this library run the following command:
 
```
mvn clean install
```
 
Dependencies
------------
 - guava
 - log4j
 
Rebuilding C++ binaries
----------------
 
This distribution includes pre-built C++ binaries along with the code.
You can rebuild the binaries from source if necessary. Refer to [build-jni/README.md](build-jni/README.md) for instructions.
 
 
Using the library
------
 
Refer to the official [vowpal wabbit wiki](https://github.com/JohnLangford/vowpal_wabbit/wiki) for general instructions and advice on training the Vowpal Wabbit model.
 
Refer to [API javadocs](http://opensource.indeedeng.io/vowpal-wabbit-java) for instructions specific to this wrapper and java API.
 
The following integration tests provide references for using the API.
 
 - [Twitter Sentiment analysis](src/test/java/com/indeed/vw/wrapper/integration/tests/TestOnTwitterSentimentDataset.java)
 - [Movie lens 1M](src/test/java/com/indeed/vw/wrapper/integration/tests/TestOnMovieLensDataset.java)
 
Reporting issues
--------
 
Create an [issue](https://github.com/indeedeng/vowpal-wabbit-java/issues) in this project if you encounter issues or need help.
 
Tested platform
---------------
 
We have tested this wrapper on the following platforms:
 
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
