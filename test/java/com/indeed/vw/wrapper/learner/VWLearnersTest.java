package com.indeed.vw.wrapper.learner;

import com.indeed.vw.wrapper.api.VowpalWabbit;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class VWLearnersTest {

    @Test
    public void testCreateTwoLearners() throws Exception {
        try(final VWFloatLearner learnerOuter = VowpalWabbit.builder()
                .adaptive().invariant().l2(0.0001).buildFloatLearner()) {
            try(final VWFloatLearner learnerInner = VowpalWabbit.builder()
                    .l1(0.0001).buildFloatLearner()) {
                learnerOuter.learn("1 |yo yo");
                learnerInner.learn("2 |yo yo yay");
            }
        }
    }
}