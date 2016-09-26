package com.indeed.vw.wrapper.progvalidation;

/**
 * true positives / (true positives + false positives) <p>
 */
public class PrecisionValidation extends ProgressiveValidation {
    private final double decisionThreashold;
    private double truePositives = 0;
    private double falsePositives = 0;

    public PrecisionValidation(final double decisionThreshold) {
        super("Pr", true);
        this.decisionThreashold = decisionThreshold;
    }

    @Override
    public synchronized double getScore() {
        if (truePositives + falsePositives == 0) {
            return 0;
        }
        return truePositives / (truePositives + falsePositives);
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        boolean actualPositive = actual  > 0.0000000000001;
        boolean predictionPositive = prediction > decisionThreashold;
        if (actualPositive && predictionPositive) {
            truePositives++;
        }
        if (!actualPositive && predictionPositive) {
            falsePositives++;
        }
    }
}
