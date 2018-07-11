package com.indeed.vw.wrapper.learner;

import com.indeed.vw.wrapper.api.VowpalWabbit;
import com.indeed.vw.wrapper.api.parameters.VWUtility;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;


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
        try (final VWFloatLearner learner = VowpalWabbit.builder().invertHash(tempFile.toPath())
                .adaptive().invariant().l2(0.0001).buildFloatLearner()) {
            learner.learn("1 |yo yo");
            learner.learn("2 |yo yo yay");
        }
    }

    @Test
    public void testReadfromModelFile() throws IOException {
        try(final VWFloatLearner learner = VowpalWabbit.builder()
                .initialRegressor(VWUtility.getFilePathFromRelativePath("/VWLearnerTest/model.8.5.0")
                ).buildFloatLearner()) {
            Assert.assertEquals(0.6569876670837402, learner.predict("| price:.23 sqft:.25 age:.05 2006"), 0);
        }
    }

    @Test
    public void testIntLearner() throws IOException {
        try(final VWIntLearner learner = VowpalWabbit.advancedBuilder()
                .initialRegressor(VWUtility.getFilePathFromRelativePath("/VWLearnerTest/classifier.model")
                ).buildIntLearner()) {
            Assert.assertEquals(2, learner.predict("| d e f"));
        }
    }
}