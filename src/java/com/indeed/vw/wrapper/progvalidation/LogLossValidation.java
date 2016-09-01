package com.indeed.vw.wrapper.progvalidation;

import com.google.common.base.Preconditions;

/**
 * See https://www.kaggle.com/wiki/LogarithmicLoss
 */
public class LogLossValidation extends ProgressiveValidation {

    private double logLikelyhood = 0.;
    private int count = 0;

    public LogLossValidation() {
        super("LOG-LOSS", false);
    }

    @Override
    public synchronized double getScore() {
        return - logLikelyhood / count;
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        final double constrainedPrediction = Math.max(0.0000001, Math.min(0.9999999, prediction));
        if (actual > 0) { // positive
            logLikelyhood += Math.log(constrainedPrediction);
        } else {
            logLikelyhood += Math.log(1 - constrainedPrediction);
        }
        count++;
    }
}
