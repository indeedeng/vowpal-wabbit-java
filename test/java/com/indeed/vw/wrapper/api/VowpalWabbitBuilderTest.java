package com.indeed.vw.wrapper.api;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class VowpalWabbitBuilderTest {

    @Test
    public void testBuildCommand() {
        final String command = VowpalWabbit.advancedBuilder()
                .adaptive().invariant()
                .link(Link.logistic)
                .lossFunction(Loss.logistic)
                .l2(0.0001).getCommand();
        final String expected = "--adaptive --invariant --link logistic --loss_function logistic --l2 1.0E-4  --quiet";
        assertEquals(expected, command);
    }
}