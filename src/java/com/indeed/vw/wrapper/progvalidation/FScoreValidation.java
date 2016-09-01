package com.indeed.vw.wrapper.progvalidation;

/**
 * See https://www.kaggle.com/wiki/MeanFScore
 */
public class FScoreValidation extends ProgressiveValidation {

    private final PrecisionValidation precision;
    private final RecallValidation recall;

    public FScoreValidation(final double decisionThreashold) {
        super("F-SCORE", true);
        precision = new PrecisionValidation(decisionThreashold);
        recall = new RecallValidation(decisionThreashold);
    }

    @Override
    public synchronized double getScore() {
        return 2 * precision.getScore() * recall.getScore() / (precision.getScore() + recall.getScore());
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        precision.updateScore(prediction, actual);
        recall.updateScore(prediction, actual);
    }
}
