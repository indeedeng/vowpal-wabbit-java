package com.indeed.vw.wrapper.learner;

import com.indeed.vw.wrapper.api.VowpalWabbit;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.lf5.util.StreamUtils;
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

        print(tempFile);
    }


//    @Test
//    public void testInvertHash() throws IOException {
//        final File tempFile = File.createTempFile("file with whitespace", ".txt");
//        tempFile.deleteOnExit();
////        try (final VWFloatLearner learner = VowpalWabbit.builder().invertHash(tempFile.toPath())
////                .adaptive().invariant().l2(0.0001).buildFloatLearner()) {
////            learner.learn("1 |yo yo");
////            learner.learn("2 |yo yo yay");
////            learner.close();
////        }
//        try (
//        final VWFloatLearner learner = VowpalWabbit.builder().initialRegressor(getFilePath("/VWUtilityTest/output")).invertHash(tempFile.toPath()).buildFloatLearner()){
//
//        }
//
//        print(tempFile);
//    }

    private Path getFilePath(@Nonnull final String relativePathToResources) throws IOException {
        final Path modelDirectory = Files.createTempDirectory("test");
        final Path modelPath = Paths.get(modelDirectory.toString(), "model");
        try (final OutputStream os = Files.newOutputStream(modelPath);
             final InputStream is = getClass().getResourceAsStream(relativePathToResources)
        ) {
            StreamUtils.copy(is, os);
        }

        return modelPath;
    }

    private void print(File tempFile) throws IOException {
        try (final InputStream is = Files.newInputStream(tempFile.toPath())
        ) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String currentline = null;
            while((currentline = br.readLine()) != null) {
                System.out.println(currentline);
            }
        }
    }
}