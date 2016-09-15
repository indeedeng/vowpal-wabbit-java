package com.indeed.vw.wrapper.progvalidation;

/**
 * correct predictions / total
 */
public class AccuracyValidation extends ProgressiveValidation {

    private final double decisionThreashold;
    private double correctPredictions = 0;
    private int count = 0;

    public AccuracyValidation(final double decisionThreashold) {
        super("ACC", true);
        this.decisionThreashold = decisionThreashold;
    }

    @Override
    public synchronized double getScore() {
        if (count == 0) {
            return 0;
        }
        return correctPredictions / count;
    }

    @Override
    public synchronized void updateScore(final double prediction, final double actual) {
        boolean actualPositive = actual > 0.0000000000001;
        boolean predictionPositive = prediction > decisionThreashold;
        if ((actualPositive && predictionPositive) || (!actualPositive && !predictionPositive)) {
            correctPredictions++;
        }
        count++;
    }
}
