package com.indeed.vw.wrapper.progvalidation;

import org.apache.log4j.Logger;

/**
 *
 */
public abstract class ProgressiveValidation {
    private static final Logger logger = Logger.getLogger(ProgressiveValidation.class);
    protected int examplesCount = 0;
    private final int printScoreEveryNExamples;

    protected ProgressiveValidation(final int printScoreEveryNExamples) {
        this.printScoreEveryNExamples = printScoreEveryNExamples;
    }

    /**
     * update score
     * @param prediction
     * @param actual
     */
    public synchronized void updateScore(double prediction, double actual) {
        doUpdateScore(prediction, actual);
        examplesCount++;
        if (printScoreEveryNExamples > 0) {
            if (examplesCount % printScoreEveryNExamples == 0) {
                printScore();
            }
        }
    }


    public synchronized void printScore() {
        logger.info("Rows=" + examplesCount + " " + metric() + "=" + getScore());
    }

    public abstract double getScore();


    protected abstract String metric();

    protected abstract void doUpdateScore(final double prediction, final double actual);
}
