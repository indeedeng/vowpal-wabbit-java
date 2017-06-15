package com.indeed.vw.wrapper.api;

import com.google.common.collect.Lists;
import com.indeed.vw.wrapper.api.parameters.Link;
import com.indeed.vw.wrapper.api.parameters.Loss;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class VowpalWabbitBuilderTest {

    @Test
    public void testBuildCommand() {
        final String command = String.join(" ", VowpalWabbit.advancedBuilder()
                .adaptive().invariant()
                .link(Link.logistic)
                .lossFunction(Loss.logistic)
                .l2(0.0001).getCommand());
        final String expected = "vw --adaptive --invariant --link logistic --loss_function logistic --l2 1.0E-4 --quiet";
        assertEquals(expected, command);
    }
}