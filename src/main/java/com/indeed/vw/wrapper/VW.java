package com.indeed.vw.wrapper;

import com.indeed.vw.wrapper.learner.VWLearners;

import java.io.IOException;

public final class VW {
    private VW(){}

    /**
     * This main method only exists to test the library implementation.  To test it just run
     * java -cp "log4j.jar:vw-wrapper-*-SNAPSHOT.jar" com.indeed.vw.wrapper.VW
     * @param args No args needed.
     */
    public static void main(String[] args) throws IOException {
        VWLearners.create("").close();
        VWLearners.create("--quiet").close();
    }

}