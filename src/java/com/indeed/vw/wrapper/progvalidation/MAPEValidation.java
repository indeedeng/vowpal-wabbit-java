package com.indeed.vw.wrapper.progvalidation;

/**
 * See <a href="https://en.wikipedia.org/wiki/Mean_absolute_percentage_error">https://en.wikipedia.org/wiki/Mean_absolute_percentage_error</a> <p>
 */
public class MAPEValidation extends ProgressiveValidation {

    private double absolutePercentageError = 0;
    private int count = 0;

    public MAPEValidation() {
        super("MAPE", false);
    }

    @Override
    public synchronized double getScore() {
        if (count == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return 100 * absolutePercentageError / count;
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        absolutePercentageError += Math.abs(prediction - actual) / actual;
        count++;
    }
}
