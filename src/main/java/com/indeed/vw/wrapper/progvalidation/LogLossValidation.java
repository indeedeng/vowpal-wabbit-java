package com.indeed.vw.wrapper.progvalidation;

/**
 * See <a href="https://www.kaggle.com/wiki/LogarithmicLoss">https://www.kaggle.com/wiki/LogarithmicLoss</a> <p>
 */
public class LogLossValidation extends ProgressiveValidation {

    private double logLikelihood = 0.;
    private int count = 0;

    public LogLossValidation() {
        super("LOG-LOSS", false);
    }

    @Override
    public synchronized double getScore() {
        if (count == 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return -logLikelihood / count;
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        final double constrainedPrediction = Math.max(0.0000001, Math.min(0.9999999, prediction));
        if (actual > 0) { // positive
            logLikelihood += Math.log(constrainedPrediction);
        } else {
            logLikelihood += Math.log(1 - constrainedPrediction);
        }
        count++;
    }
}
