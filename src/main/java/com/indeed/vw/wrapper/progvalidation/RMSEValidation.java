package com.indeed.vw.wrapper.progvalidation;

/**
 * See <a href="https://www.kaggle.com/wiki/RootMeanSquaredError">https://www.kaggle.com/wiki/RootMeanSquaredError</a> <p>
 */
public class RMSEValidation extends ProgressiveValidation {

    private double sumOfSquares = 0;
    private int count = 0;

    public RMSEValidation() {
        super("RMSE", false);
    }

    @Override
    public synchronized double getScore() {
        if (count == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return Math.sqrt(sumOfSquares / count);
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        sumOfSquares += (prediction - actual) * (prediction - actual);
        count++;
    }
}
