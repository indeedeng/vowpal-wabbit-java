package com.indeed.vw.wrapper.progvalidation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MultiMetricsProgressiveValidation {
    public static MultiMetricsProgressiveValidation regressionMetrics(final int printEveryN) {
        return new MultiMetricsProgressiveValidation(
                new RMSEValidation(printEveryN), new MAEValidation(printEveryN), new MAPEValidation(printEveryN)
        );
    }

    private final List<ProgressiveValidation> validationMetrics;

    public MultiMetricsProgressiveValidation(final ProgressiveValidation ... validationMetrics) {
        this.validationMetrics = Arrays.asList(validationMetrics);
    }

    public MultiMetricsProgressiveValidation(final List<ProgressiveValidation> validationMetrics) {
        this.validationMetrics = validationMetrics;
    }

    public synchronized void updateScore(double prediction, double actual) {
        for (final ProgressiveValidation metric : validationMetrics) {
            metric.updateScore(prediction, actual);
        }
    }


    public synchronized void printScores() {
        for (final ProgressiveValidation metric : validationMetrics) {
            metric.printScore();
        }
    }

    public synchronized Map<String, Double> getScores() {
        final HashMap<String, Double> scores = new HashMap<>();
        for (final ProgressiveValidation metric : validationMetrics) {
            scores.put(metric.metric(), metric.getScore());
        }
        return scores;
    }
}
