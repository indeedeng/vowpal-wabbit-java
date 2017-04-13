package com.indeed.vw.wrapper.progvalidation;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;

/**
 * Base class for all metrics
 */
public abstract class ProgressiveValidation {

    private final String metric;
    private final boolean biggerIsBetter;

    protected ProgressiveValidation(final String metric, final boolean biggerIsBetter) {
        this.metric = metric;
        this.biggerIsBetter = biggerIsBetter;
    }

    /**
     * update score <p>
     * @param prediction
     * @param actual
     */
    public abstract void updateScore(double prediction, double actual);

    public abstract double getScore();

    public String getMetric() {
        return metric;
    }

    public boolean biggerIsBetter() {
        return biggerIsBetter;
    }

    @Override
    public synchronized String toString() {
        return getMetric() + "=" + new DecimalFormat("0.000000").format(getScore());
    }
}
