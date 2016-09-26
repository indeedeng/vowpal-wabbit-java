package com.indeed.vw.wrapper.integration.tests;

import com.indeed.vw.wrapper.api.example.ExampleBuilder;
import com.indeed.vw.wrapper.api.parameters.SGDVowpalWabbitBuilder;
import com.indeed.vw.wrapper.api.VowpalWabbit;
import com.indeed.vw.wrapper.api.parameters.Loss;
import com.indeed.vw.wrapper.integration.IntegrationSuite;
import com.indeed.vw.wrapper.learner.VWFloatLearner;
import com.indeed.vw.wrapper.progvalidation.Metrics;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This is example of how vowpal-wabbit can be used for NLP tasks.
 */
public class TestOnTwitterSentimentDataset extends IntegrationSuite {
    private static final int TARGET_POS = 0;
    private static final int TEXT_POS = 1;

    // Feature selection is done with a preliminary pass through the train set and learns a linear model
    // with many zero weights (L1 regularization)
    private Path trainFeatureMask() throws IOException {
        final Path featureMask = Paths.get(tmpDir.toString(), "feature-mask-model.bin");
        final SGDVowpalWabbitBuilder vowpalWabbitBuilder = VowpalWabbit.builder()
                // FTRL-Proximal is an optimization method to train sparse models
                .ftrl()
                // l1 regularization
                .l1(0.5)
                // Bit precision increases consumption of RAM and improves quality
                // Notice that feature mask must have same bit precision as a final model
                // You can increase this parameter even more
                .bitPrecision(22)
                // When you have text data it's always good thing to try ngrams and skip-ngrams.
                // Nice thing about vowpal wabbit is that it is very easy to try these features
                .ngram("clean", 2)
                .skips("clean", 1)
                .ngram("raw", 2)
                // Loss function defines whether this task is classification or regression
                .lossFunction(Loss.logistic)
                .finalRegressor(featureMask);

        try (final VWFloatLearner learner = vowpalWabbitBuilder.buildFloatLearner()) {
            for (final List<String> columns : readColumnsFromCsv(getTrainPath())) {
                final ExampleBuilder wvExample = parseWvExample(columns);
                learner.learn(wvExample.toString());
            }
        }
        return featureMask;
    }

    @Override
    protected SGDVowpalWabbitBuilder configureVowpalWabbit() throws IOException {
        final Path featureMaskModel = trainFeatureMask();
        return VowpalWabbit.builder()
                // Notice that once we trained feature mask we don't need to use l1 regularization
                // Though you still can try to use l2 regularization if your model is not stable enough
                // Also notice that we use regular sgd  (i.e. adaptive, invariant, normalized) to train final model
                .featureMask(featureMaskModel)
                .bitPrecision(22)
                // We need to specify same feature engineering
                .ngram("clean", 2)
                .skips("clean", 1)
                .ngram("raw", 2)
                .lossFunction(Loss.logistic);
    }

    private final Pattern notWordPattern = Pattern.compile("[^a-zA-Z]+");
    private final Pattern splitWordsFromPunctuationPattern = Pattern.compile("([a-zA-Z])([^a-zA-Z\\s])");

    @Override
    protected ExampleBuilder parseWvExample(final List<String> columns) {
        boolean positiveSentiment = columns.get(TARGET_POS).equals("1");
        final ExampleBuilder exampleBuilder = ExampleBuilder.create()
                // Notice that we use special method to set binary target
                .binaryLabel(positiveSentiment);
        final String rawText = columns.get(TEXT_POS);
        exampleBuilder.createNamespace("clean")
                // cheap way to normalize words.
                // This model improved by using stemming and lemmatization.
                .addTextAsFeatures(notWordPattern.matcher(rawText).replaceAll(" ").toLowerCase());
        exampleBuilder.createNamespace("raw")
                // Add raw text to add signals such as smiles or punctuation
                 .addTextAsFeatures(splitWordsFromPunctuationPattern.matcher(rawText).replaceAll("$1 $2"));
        return exampleBuilder;
    }

    @Override
    protected String getMetricToVerify() {
        return "ACC";
    }

    @Override
    protected Metrics createProgressiveValidation(final int printEveryN) {
        return Metrics.zeroOneClassificationMetrics(printEveryN, 0);
    }

    @Override
    protected char getInputCsvSeparator() {
        return '\t';
    }

    @Override
    protected double expectedTestScore() {
        return 0.819;
    }

    @Override
    protected String getTrainPath() {
        return "/sentiment-analysis/train.tsv.gz";
    }

    @Override
    protected String getTestPath() {
        return "/sentiment-analysis/test.tsv.gz";
    }

    @Override
    protected String getModelPath() {
        return "/sentiment-analysis/model.8.2.0.bin";
    }
}
