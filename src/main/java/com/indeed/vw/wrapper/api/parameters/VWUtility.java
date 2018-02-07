package com.indeed.vw.wrapper.api.parameters;

import com.indeed.vw.wrapper.api.VowpalWabbit;
import org.apache.log4j.lf5.util.StreamUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    /**
     *  Read vowpal wabbit model and count number of non-zero weights in it <p>
     *
     * @param relativePathToResources relative path to vowpal wabbit model
     * @return the modelPath consumed by initialRegressor
     * @throws IOException
     */
    public static Path getFilePathFromRelativePath(@Nonnull final String relativePathToResources) throws IOException {
        final Path modelDirectory = Files.createTempDirectory("temp");
        final Path modelPath = Paths.get(modelDirectory.toString(), "model");
        try (final OutputStream os = Files.newOutputStream(modelPath);
             final InputStream is = VWUtility.class.getResourceAsStream(relativePathToResources)
        ) {
            StreamUtils.copy(is, os);
        }

        return modelPath;
    }
}
