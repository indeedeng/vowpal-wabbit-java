package com.indeed.vw.wrapper.api.parameters;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 *
 */
public class VWUtilityTest {

    @Test
    public void testCountNumberOfNonZeroWeights() throws Exception {
        final Path modelPath = Paths.get(getClass().getResource("/VWUtilityTest/model.bin").getPath());
        assertEquals(136, VWUtility.countNumberOfNonZeroWeights(modelPath));
    }
}