package com.indeed.vw.wrapper.learner;

import com.indeed.vw.wrapper.api.VowpalWabbit;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


/**
 *
 */
public class VWLearnersTest {

    @Test
    public void testCreateTwoLearners() throws Exception {
        try(final VWFloatLearner learnerOuter = VowpalWabbit.builder()
                .adaptive().invariant().l2(0.0001).buildFloatLearner()) {
            try(final VWFloatLearner learnerInner = VowpalWabbit.builder()
                    .l1(0.0001).buildFloatLearner()) {
                learnerOuter.learn("1 |yo yo");
                learnerInner.learn("2 |yo yo yay");
            }
        }
    }

    @Test
    public void testCreateModelFileWithWhiteSpace() throws IOException {
        final File tempFile = File.createTempFile("file with whitespace", ".txt");
        tempFile.deleteOnExit();
        try (final VWFloatLearner learner = VowpalWabbit.builder().readableModel(tempFile.toPath())
                .adaptive().invariant().l2(0.0001).buildFloatLearner()) {
            learner.learn("1 |yo yo");
            learner.learn("2 |yo yo yay");
        }
    }
}