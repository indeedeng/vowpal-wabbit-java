package com.indeed.vw.wrapper.progvalidation;

/**
 * https://www.kaggle.com/wiki/MeanAbsoluteError
 */
public class MAEValidation extends ProgressiveValidation {

    private double absoluteError = 0;
    private int count = 0;

    public MAEValidation() {
        super("MAE", false);
    }

    @Override
    public synchronized double getScore() {
        return absoluteError / count;
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        absoluteError += Math.abs(prediction - actual);
        count++;
    }
}
