package com.indeed.vw.wrapper.progvalidation;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Metrics {
    public static Metrics regressionMetrics(final int printEveryN) {
        return new Metrics(
                printEveryN,
                new RMSEValidation(), new MAEValidation()
        );
    }

    public static Metrics zeroOneClassificationMetrics(final int printEveryN, final double decisionThreshold) {
        return new Metrics(
                printEveryN,
                new AccuracyValidation(decisionThreshold), new FOneScoreValidation(decisionThreshold)
        );
    }

    public static Metrics probabilityClassificationMetrics(final int printEveryN) {
        return new Metrics(
                printEveryN,
                new LogLossValidation()
        );
    }

    private final List<ProgressiveValidation> validationMetrics;
    private static final Logger logger = Logger.getLogger(ProgressiveValidation.class);
    protected int examplesCount = 0;
    private final int printScoreEveryNExamples;

    public Metrics(final int printScoreEveryNExamples,
                   final ProgressiveValidation... validationMetrics) {
        this.printScoreEveryNExamples = printScoreEveryNExamples;
        this.validationMetrics = Arrays.asList(validationMetrics);
    }

    public synchronized void updateScore(double prediction, double actual) {
        for (final ProgressiveValidation metric : validationMetrics) {
            metric.updateScore(prediction, actual);
        }
        examplesCount++;
        if (printScoreEveryNExamples > 0) {
            if (examplesCount % printScoreEveryNExamples == 0) {
                printScores();
            }
        }
    }


    public synchronized void printScores() {
        logger.info("#Examples=" + examplesCount +  "\t- " + Joiner.on('\t').join(validationMetrics));
    }

    public synchronized Map<String, Double> getScores() {
        final HashMap<String, Double> scores = new HashMap<>();
        for (final ProgressiveValidation metric : validationMetrics) {
            scores.put(metric.getMetric(), metric.getScore());
        }
        return scores;
    }
}
