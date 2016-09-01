package com.indeed.vw.wrapper.progvalidation;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;

/**
 *
 */
public abstract class ProgressiveValidation {

    private final String metric;
    private final boolean biggerIsBetter;

    protected ProgressiveValidation(final String metric, final boolean biggerIsBetter) {
        this.metric = metric;
        this.biggerIsBetter = biggerIsBetter;
    }

    /**
     * update score
     * @param prediction
     * @param actual
     */
    public abstract void updateScore(double prediction, double actual);

    public abstract double getScore();

    public String getMetric() {
        return metric;
    }

    public boolean isBiggerIsBetter() {
        return biggerIsBetter;
    }

    @Override
    public synchronized String toString() {
        return getMetric() + "=" + new DecimalFormat("0.000000").format(getScore());
    }
}
