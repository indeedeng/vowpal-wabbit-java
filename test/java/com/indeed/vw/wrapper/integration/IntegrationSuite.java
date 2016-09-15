package com.indeed.vw.wrapper.integration;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Doubles;
import com.indeed.vw.wrapper.api.ExampleBuilder;
import com.indeed.vw.wrapper.api.SGDVowpalWabbitBuilder;
import com.indeed.vw.wrapper.api.VowpalWabbit;
import com.indeed.vw.wrapper.learner.VWFloatLearner;
import com.indeed.vw.wrapper.progvalidation.Metrics;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public abstract class IntegrationSuite {
    private final Logger logger = Logger.getLogger(IntegrationSuite.class);
    protected Path tmpDir;

    @Before
    public void setup() throws IOException {
        tmpDir = Files.createTempDirectory(getClass().getSimpleName());
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(tmpDir.toFile());
    }

    /**
     * You can use this method to generate input for vowpal-wabbit command line program
     *
     * @throws IOException
     */
    @Test
    @Ignore
    public void testGenerateVwDatasets() throws IOException {
        final Path trainVwPath = Paths.get(getClass().getResource(getTrainPath()).getPath() + ".vw");
        final List<String> trainVwExamples = new ArrayList<>();
        for (final List<String> columns : readColumnsFromCsv(getTrainPath())) {
            trainVwExamples.add(parseWvExample(columns).toString());
        }
        Files.write(trainVwPath, trainVwExamples, Charsets.UTF_8);
        logger.info("Train vw path: " + trainVwPath);

        final Path testVwPath = Paths.get(getClass().getResource(getTestPath()).getPath() + ".vw");
        final List<String> testVwExamples = new ArrayList<>();
        for (final List<String> columns : readColumnsFromCsv(getTestPath())) {
            testVwExamples.add(parseWvExample(columns).toString());
        }
        Files.write(testVwPath, testVwExamples, Charsets.UTF_8);
        logger.info("Test vw path: " + testVwPath);
    }

    /**
     * Test that we can load model trained using command line program.
     *
     * @throws IOException
     */
    @Test
    public void testReadModelTrainedInCommandLine() throws IOException {
        final Path modelPath = Paths.get(getClass().getResource(getModelPath()).getPath());
        try (final VWFloatLearner learner = VowpalWabbit.builder()
                .verbose()
                .testonly()
                .initialRegressor(modelPath)
                .buildFloatLearner()) {
            final Metrics testValidation = createProgressiveValidation(-1);
            for (final List<String> columns : readColumnsFromCsv(getTestPath())) {
                final ExampleBuilder wvExample = parseWvExample(columns);
                final double label = wvExample.getLabelAsDouble();
                float prediction = learner.predict(wvExample.toString());
                testValidation.updateScore(prediction, label);
            }
            // In this test-case we load small version of model which is less accurate
            // because we cannot put in git very big files
            assertEquals(expectedTestScore(), testValidation.getScores().get(getMetricToVerify()), 0.02);
        }
    }

    /**
     * Test the case when we learn and predict model within same process without saving model in file.
     *
     * @throws IOException
     */
    @Test
    public void testInteractingTroughJava() throws IOException {
        try (final VWFloatLearner learner = configureVowpalWabbit().verbose().buildFloatLearner()) {
            final Metrics trainValidation = createProgressiveValidation(100000);
            for (final List<String> columns : readColumnsFromCsv(getTrainPath())) {
                final ExampleBuilder wvExample = parseWvExample(columns);
                float prediction = learner.learn(wvExample.toString());
                trainValidation.updateScore(prediction, wvExample.getLabelAsDouble());
            }
            trainValidation.printScores();
            assertTrue(trainValidation.getScores().get(getMetricToVerify()) > 0);
            assertTrue(Doubles.isFinite(trainValidation.getScores().get(getMetricToVerify())));
            final Metrics testValidation = createProgressiveValidation(-1);
            for (final List<String> columns : readColumnsFromCsv(getTestPath())) {
                final ExampleBuilder wvExample = parseWvExample(columns);
                final double label = wvExample.getLabelAsDouble();
                float prediction = learner.predict(wvExample.toString());
                testValidation.updateScore(prediction, label);
            }
            assertEquals(expectedTestScore(), testValidation.getScores().get(getMetricToVerify()), 0.002);
        }
    }

    /**
     * Test the case when you train model in one process and save it in file
     * then in different process we read the model and compute predictions.
     *
     * @throws IOException
     */
    @Test
    public void testSaveAndReadModelFile() throws IOException {
        final Path modelPath = Paths.get(tmpDir.toString(), "model.bin");
        logger.info("Tmp directory: " + tmpDir);

        final Metrics trainValidation = createProgressiveValidation(100000);
        try (final VWFloatLearner learner = configureVowpalWabbit()
                .verbose()
                .finalRegressor(modelPath)
                .buildFloatLearner()) {
            for (final List<String> columns : readColumnsFromCsv(getTrainPath())) {
                final ExampleBuilder wvExample = parseWvExample(columns);
                float prediction = learner.learn(wvExample.toString());
                trainValidation.updateScore(prediction, wvExample.getLabelAsDouble());
            }
            trainValidation.printScores();
            assertTrue(trainValidation.getScores().get(getMetricToVerify()) > 0);
            assertTrue(Doubles.isFinite(trainValidation.getScores().get(getMetricToVerify())));
        }

        try (final VWFloatLearner testLearner = VowpalWabbit.builder()
                .initialRegressor(modelPath)
                .verbose()
                .testonly()
                .buildFloatLearner()) {
            final Metrics testValidation = createProgressiveValidation(-1);
            for (final List<String> columns : readColumnsFromCsv(getTestPath())) {
                final ExampleBuilder wvExample = parseWvExample(columns);
                final double label = wvExample.getLabelAsDouble();
                final float prediction = testLearner.predict(wvExample.toString());
                testValidation.updateScore(prediction, label);
            }
            assertEquals(expectedTestScore(), testValidation.getScores().get(getMetricToVerify()), 0.002);
        }
    }

    protected Collection<List<String>> readColumnsFromCsv(final String path) throws IOException {
        final List<List<String>> table = new ArrayList<>();
        try (final GZIPInputStream trainInputStream = new GZIPInputStream(
                getClass().getResourceAsStream(path))) {
            for (final String line : Iterables.skip((List<String>) IOUtils.readLines(trainInputStream), 1)) {
                final List<String> columns = Splitter.on(getInputCsvSeparator()).splitToList(line);
                table.add(columns);
            }
        }
        return table;
    }

    protected abstract String getMetricToVerify();

    protected abstract Metrics createProgressiveValidation(final int printEveryN);

    protected abstract char getInputCsvSeparator();

    protected abstract double expectedTestScore();

    protected abstract String getTrainPath();

    protected abstract String getTestPath();

    protected abstract String getModelPath();

    protected abstract SGDVowpalWabbitBuilder configureVowpalWabbit() throws IOException;

    protected abstract ExampleBuilder parseWvExample(List<String> columns);
}
