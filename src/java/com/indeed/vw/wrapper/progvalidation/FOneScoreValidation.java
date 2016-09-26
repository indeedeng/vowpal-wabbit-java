package com.indeed.vw.wrapper.progvalidation;

/**
 * See <a href="https://www.kaggle.com/wiki/MeanFScore">https://www.kaggle.com/wiki/MeanFScore</a> <p>
 */
public class FOneScoreValidation extends ProgressiveValidation {

    private final PrecisionValidation precision;
    private final RecallValidation recall;

    public FOneScoreValidation(final double decisionThreashold) {
        super("F1-SCORE", true);
        precision = new PrecisionValidation(decisionThreashold);
        recall = new RecallValidation(decisionThreashold);
    }

    @Override
    public synchronized double getScore() {
        if (precision.getScore() == 0 || recall.getScore() == 0) {
            return 0;
        }
        return 2 * precision.getScore() * recall.getScore() / (precision.getScore() + recall.getScore());
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        precision.updateScore(prediction, actual);
        recall.updateScore(prediction, actual);
    }
}
