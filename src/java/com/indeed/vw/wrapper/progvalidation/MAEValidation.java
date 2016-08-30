package com.indeed.vw.wrapper.progvalidation;

/**
 *
 */
public class MAEValidation extends ProgressiveValidation {

    private double absoluteError = 0;

    protected MAEValidation(final int printScoreEveryNExamples) {
        super(printScoreEveryNExamples);
    }

    @Override
    public double getScore() {
        return absoluteError / examplesCount;
    }

    @Override
    protected String metric() {
        return "MAE";
    }

    @Override
    protected void doUpdateScore(final double prediction, final double actual) {
        absoluteError += Math.abs(prediction - actual);
    }
}
