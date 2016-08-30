package com.indeed.vw.wrapper.integration.movielens;

import com.indeed.vw.wrapper.api.ExampleBuilder;
import com.indeed.vw.wrapper.api.SGDVowpalWabbitBuilder;
import com.indeed.vw.wrapper.api.VowpalWabbit;
import com.indeed.vw.wrapper.integration.IntegrationSuite;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is example of how vowpal-wabbit can be used for recomendation systems
 */
public class TestOnMovieLensDataset extends IntegrationSuite {
    private static final int RATING_POS = 0;
    private static final int TIMESTAMP_POS = 1;
    private static final int USER_ID_POS = 2;
    private static final int MOVIE_ID_POS = 3;
    private static final int U_GENDER_POS = 4;
    private static final int U_AGE_POS = 5;
    private static final int U_OCUPATION_POS = 6;
    private static final int U_ZIP_CODE_POS = 7;
    private static final int M_TITLE_POS = 8;
    private static final int M_GENRES_POS = 9;

    @Override
    protected VowpalWabbit.Builder configureVowpalWabbit() {
        return VowpalWabbit.advancedBuilder()
                // Bit precision increases consumption of RAM
                // and decreases chances of hash collision - so improves quality.
                .bit_precision(29)
                        // Always try to play with adaptive, invariant and normalized.
                .adaptive().invariant()
                .loss_function(VowpalWabbit.Loss.squared)
                .learning_rate(0.15)
                        // Most of the magic happens here.
                        // LRQFA - low rank quadratic feature aware interactions.
                        // This option allows to learn latent interaction
                        // between user_id and movie_id.
                        // Notice that this option will learn interaction between user_id and movie_id
                        // even if concrete pair (user_id, movie_id) didn't occur in train set.
                        // For more details search for "feature-aware factorization machines"
                .lrqfa("user_id", "movie_id", 7)
                        // LRQFA will learn interactions only for those users and movies that present in train set.
                        // Film features represent movie_id but they are much less sparse and it makes sense
                        // to generate quadratic features with them
                .quadratic("user_id", "film_features")
                .quadratic("demographics_features", "movie_id")
                        // Useful constraints.
                .min_prediction(1)
                .max_prediction(5)
                        // Regularization term should be small in high dimension feature space
                        // otherwise you will go out of loss minimum.
                .l2(0.000001);
    }

    private final Pattern YEAR_PATTERN = Pattern.compile("[^\\d]([12][90]\\d\\d)[^\\d]");

    @Override
    protected ExampleBuilder parseWvExample(final List<String> columns) {
        final double rating = Double.parseDouble(columns.get(RATING_POS));
        final String id = columns.get(USER_ID_POS) + "_" + columns.get(MOVIE_ID_POS);
        final ExampleBuilder exampleBuilder = ExampleBuilder.create()
                .exampleTag(id)
                .label(rating);
        exampleBuilder.createNamespace("user_id")
                .addCategoricalFeature(columns.get(USER_ID_POS));
        exampleBuilder.createNamespace("movie_id")
                .addCategoricalFeature(columns.get(MOVIE_ID_POS));

        // This number is tuned through progressive validation
        final double secondaryFeaturesWeight = 0.1;
        exampleBuilder.createNamespace("demographics_features")
                .namespaceWeight(secondaryFeaturesWeight)
                .addCategoricalFeature("age", columns.get(U_AGE_POS))
                .addCategoricalFeature("gender", columns.get(U_GENDER_POS))
                .addCategoricalFeature("occupation", columns.get(U_OCUPATION_POS));

        ExampleBuilder.NamespaceBuilder filmFeaturesNamespace = exampleBuilder.createNamespace("film_features")
                .namespaceWeight(secondaryFeaturesWeight)
                .addTextAsFeatures(columns.get(M_GENRES_POS))
                .addTextAsFeatures(columns.get(M_TITLE_POS));
        final Matcher yearMatcher = YEAR_PATTERN.matcher(columns.get(M_TITLE_POS));
        if (yearMatcher.find()) {
            final int year = Integer.parseInt(yearMatcher.group(1));
            final String decade = (year / 10) + "0";
            filmFeaturesNamespace
                    .addCategoricalFeature("creation_decade", decade);
        }
        return exampleBuilder;
    }

    @Override
    protected double expectedTestScore() {
        return 0.8996;
    }

    @Override
    protected String getTrainPath() {
        return "/movie-lens-dataset/train.csv.gz";
    }

    @Override
    protected String getTestPath() {
        return "/movie-lens-dataset/test.csv.gz";
    }

    @Override
    protected String getModelPath() {
        return "/movie-lens-dataset/model.8.2.0.bin";
    }
}