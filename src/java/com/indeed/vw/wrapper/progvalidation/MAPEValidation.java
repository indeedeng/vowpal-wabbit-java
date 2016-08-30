package com.indeed.vw.wrapper.progvalidation;

/**
 *
 */
public class MAPEValidation extends ProgressiveValidation {

    private double absolutePercentageError = 0;

    protected MAPEValidation(final int printScoreEveryNExamples) {
        super(printScoreEveryNExamples);
    }

    @Override
    public double getScore() {
        return 100 * absolutePercentageError / examplesCount;
    }

    @Override
    protected String metric() {
        return "MAPE";
    }

    @Override
    protected void doUpdateScore(final double prediction, final double actual) {
        absolutePercentageError += Math.abs(prediction - actual) / actual;
    }
}
