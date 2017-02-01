package com.indeed.vw.wrapper.api.parameters;

import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 *
 */
public class VWUtilityTest {

    @Test
    public void testCountNumberOfNonZeroWeights() throws Exception {
        assertEquals(136, VWUtility.countNumberOfNonZeroWeights(Paths.get("test/resources/VWUtilityTest/model.bin")));
    }
}