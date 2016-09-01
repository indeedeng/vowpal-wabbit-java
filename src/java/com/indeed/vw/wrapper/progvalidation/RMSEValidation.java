package com.indeed.vw.wrapper.progvalidation;

/**
 * See https://www.kaggle.com/wiki/RootMeanSquaredError
 */
public class RMSEValidation extends ProgressiveValidation {

    private double sumOfSquares = 0;
    private int count = 0;

    public RMSEValidation() {
        super("RMSE", false);
    }

    @Override
    public synchronized double getScore() {
        return Math.sqrt(sumOfSquares / count);
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        sumOfSquares += (prediction - actual) * (prediction - actual);
        count++;
    }
}
