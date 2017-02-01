package com.indeed.vw.wrapper.api.parameters;

import com.indeed.vw.wrapper.api.VowpalWabbit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Set of useful methods
 */
public class VWUtility {
    private VWUtility() {}

    /**
     *  Read vowpal wabbit model and count number of non-zero weights in it <p>
     *
     * @param modelPath path to vowpal wabbit model
     * @return number of non-zero weights in a model
     * @throws IOException
     */
    public static long countNumberOfNonZeroWeights(final Path modelPath) throws IOException {
        final Path readableModelPath = Files.createTempFile("readable-model", ".txt");
        try {
            VowpalWabbit.advancedBuilder()
                    .initialRegressor(modelPath)
                    .readableModel(readableModelPath)
                    .buildFloatLearner()
                    .close();
            return Files.readAllLines(readableModelPath, Charset.defaultCharset()).size();
        } finally {
            Files.delete(readableModelPath);
        }
    }
}
