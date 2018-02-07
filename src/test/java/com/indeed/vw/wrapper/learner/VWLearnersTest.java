package com.indeed.vw.wrapper.learner;

import com.indeed.vw.wrapper.api.VowpalWabbit;
import com.indeed.vw.wrapper.api.example.ExampleBuilder;
import com.indeed.vw.wrapper.api.parameters.VWUtility;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.lf5.util.StreamUtils;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
        try(final VWIntLearner learner = VowpalWabbit.builder()
                .initialRegressor(VWUtility.getFilePathFromRelativePath("/VWLearnerTest/classifier.model")
                ).buildIntLearner()) {
            Assert.assertEquals(2, learner.predict("| d e f"));
        }
    }
}