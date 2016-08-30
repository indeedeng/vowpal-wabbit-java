package com.indeed.vw.wrapper.progvalidation;

/**
 *
 */
public class RMSEValidation extends ProgressiveValidation {

    private double sumOfSquares = 0;

    public RMSEValidation(final int printScoreEveryNExamples) {
        super(printScoreEveryNExamples);
    }

    @Override
    public double getScore() {
        return Math.sqrt(sumOfSquares / examplesCount);
    }

    @Override
    protected String metric() {
        return "RMSE";
    }

    @Override
    protected void doUpdateScore(final double prediction, final double actual) {
        sumOfSquares += (prediction - actual) * (prediction - actual);
    }
}
