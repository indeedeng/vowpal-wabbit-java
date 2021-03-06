package com.indeed.vw.wrapper.learner;

/**
 * @author deak
 */
final public class VWScalarsLearner extends VWLearnerBase<float[]> {
    VWScalarsLearner(final long nativePointer) {
        super(nativePointer);
    }

    @Override
    protected native float[] predict(String example, boolean learn, long nativePointer);

    @Override
    protected native float[] predictMultiline(String[] example, boolean learn, long nativePointer);
}
