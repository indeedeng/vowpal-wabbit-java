package com.indeed.vw.wrapper.api;

import com.indeed.vw.wrapper.learner.VWFloatLearner;

import java.nio.file.Path;

/**
 * Updates options.
 *
 * These options automates and hides complexity of learning rates parameter tuning.
 * Notice that by default adaptive, invariant and normalized are turned on and to remove them
 * you need to pass sgd parameter.
 *
 * Adaptive option tunes learning rates individually per feature. This option is highly recommended - if you do not specify it,
 * then rare features will have too small weights.
 *
 * Invariant option allows consider example's weight during online learning.
 * Without this option example's weight will be ignored.
 * Also this option usually improves model quality.
 *
 * Normalize option turns on online feature scaling.
 * In a lot of cases you want to remove this option,
 * specially in case of very sparse boolean feature space.
 *
 * Always play with these options first, it usually gives the biggest gain.
 */
interface UpdatesOptions {
    /**
     * use regular stochastic gradient descent update.
     * removes adaptive, invariant and normalized options
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder sgd();

    /**
     * use adaptive, individual learning rates.
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder adaptive();

    /**
     * use safe/importance aware updates.
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder invariant();

    /**
     * use per feature normalized updates
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder normalized();

    /**
     * Set learning rate
     *
     * @param learningRate
     * @return builder
     */
    SGDVowpalWabbitBuilder learningRate(final double learningRate);
}

/**
 * Regularization options.
 *
 * Specify these values to prevent overfitting.
 * l2 regularization will tend to keep weight small
 * while l1 will tend to zero irrelevant weights
 * and can be seen as feature selection
 */
interface RegularizationOptions {
    /**
     * l_1 lambda
     *
     * @param l1
     * @return builder
     */
    SGDVowpalWabbitBuilder l1(final double l1);

    /**
     * l_2 lambda
     *
     * @param l2
     * @return builder
     */
    SGDVowpalWabbitBuilder l2(final double l2);
}

/**
 * Link and Loss options.
 *
 * loss function defines gradient using to update feature weights
 * link applied to linear composition
 * Possibility to set these to functions makes vowpal wabbit GLM framework
 *
 * Check https: github.com/JohnLangford/vowpal_wabbit/wiki/Loss-functions
 */
interface LinkAndLossOptions {
    /**
     * Specify the loss function to be used, uses squared by default. Currently available ones are
     * squared, classic, hinge, logistic, quantile and poisson. (=squared)
     *
     * @param loss
     * @return builder
     */
    SGDVowpalWabbitBuilder lossFunction(final Loss loss);

    /**
     * Parameter \tau associated with Quantile loss. Defaults to 0.5 (=0.5)
     *
     * @param tau
     * @return builder
     */
    SGDVowpalWabbitBuilder quantileTau(final double tau);

    /**
     * Specify the link function: identity, logistic, glf1 or poisson  (=identity)
     *
     * @param link
     * @return builder
     */
    SGDVowpalWabbitBuilder link(final Link link);

}

/**
 * Prediction boundary options
 *
 */
interface PredictionBoundaryOptions {
    /**
     * Smallest prediction to output
     *
     * @param min
     * @return builder
     */
    SGDVowpalWabbitBuilder minPrediction(final double min);

    /**
     * Largest prediction to output
     *
     * @param max
     * @return builder
     */
    SGDVowpalWabbitBuilder maxPrediction(final double max);

}

/**
 * Feature engineering functions.
 *
 * Feature engineering is a strong part of vowpal wabbit
 * To refer namespace you its first character
 * To refer any namespaces use ':'
 */
interface FeatureEngineeringFunctions {
    /**
     * Don't add a constant feature
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder noconstant();

    /**
     * Set initial value of constant
     *
     * @param initialValue
     * @return builder
     */
    SGDVowpalWabbitBuilder constant(final double initialValue);

    /**
     * Generate N grams. To generate N grams for a single namespace 'foo', arg should be fN.
     *
     * @param namespace
     * @param n
     * @return builder
     */
    SGDVowpalWabbitBuilder ngram(final String namespace, final int n);

    /**
     * Generate skips in N grams. This in conjunction with the ngram tag can be used to generate generalized n-skip-k-gram.
     * To generate n-skips for a single namespace 'foo', arg should be fN.
     *
     * @param namespace
     * @param n
     * @return builder
     */
    SGDVowpalWabbitBuilder skips(final String namespace, final int n);

    /**
     * Create and use quadratic features
     *
     * @param firstNameSpace - namespace or ":" for any
     * @param secondNamespace - namespace or ":" for any
     * @return builder
     */
    SGDVowpalWabbitBuilder quadratic(final String firstNameSpace, final String secondNamespace);

    /**
     * Create and use cubic features
     *
     * @param firstNameSpace - namespace or ":" for any
     * @param secondNamespace - namespace or ":" for any
     * @param thirdNamespace - namespace or ":" for any
     * @return builder
     */
    SGDVowpalWabbitBuilder cubic(final String firstNameSpace, final String secondNamespace, final String thirdNamespace);

    /**
     * use low rank quadratic feature-aware weights
     *
     * @param firstNamespace  - namespace or ":" for any
     * @param secondNamespace - namespace or ":" for any
     * @param k
     * @return builder
     */
    SGDVowpalWabbitBuilder lrqfa(final String firstNamespace, final String secondNamespace, final int k);
}

/**
 * Options to save and load model.
 *
 */
interface OptionsToSaveAndLoadModel {
    /**
     * Initial regressor(s)
     *
     * @param initialRegressor
     * @return builder
     */
    SGDVowpalWabbitBuilder initialRegressor(final Path initialRegressor);

    /**
     * Final regressor
     *
     * @param regressor
     * @return builder
     */
    SGDVowpalWabbitBuilder finalRegressor(final Path regressor);
}

/**
 * Options for debugging model.
 *
 */
interface DebuggingOptions {
    /**
     * Output human-readable final regressor with numeric features
     *
     * @param model
     * @return builder
     */
    SGDVowpalWabbitBuilder readableModel(final Path model);

    /**
     * Make vowpal wabbit writing debug and performance information to stderr
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder verbose();
}

/**
 * Feature selection options.
 *
 * Usual way to do feature selection in GLM framework is to set l1 regularization.
 * This type of regularization will tend to zero weights of unrellevant variables.
 * Although this a good way to go, setting l1 usually decreases model quality.
 *
 * Vowpal wabbit has two workarounds. One of them is ftrl-proximal optimization algorithm.
 * This algorithm accumulates updates in a buffer vector and changes actual model
 * weights only if updates are big enough. As result, the produced model tends to be very sparse.
 * I order to get benefit from this option you still need to set l1. Also this is the algorithm described
 * in famous google paper "ctr-modeling, a view from trenches".
 *
 * Other option is feature mask. The idea of feature mask is that you train a model with very big l1 regularization
 * to do feature selection in a first pass. Then in a second pass you use previous model to zero unrellevant features
 * and you can train without l1 regularization at all.
 */
interface FeatureSelectionOptions {
    /**
     * FTRL: Follow the Proximal Regularized Leader
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder ftrl();

    /**
     * Use existing regressor to determine which parameters may be updated.
     * If no initialRegressor given, also used for initial weights.
     *
     * @param featureMask
     * @return builder
     */
    SGDVowpalWabbitBuilder featureMask(final Path featureMask);
}

/**
 * Option to exchange RAM for some quality.
 *
 */
interface OptionToExchangeRAMForQuality {
    /**
     * number of bits in the feature table
     *
     * @param bitsNum
     * @return builder
     */
    SGDVowpalWabbitBuilder bitPrecision(int bitsNum);
}

/**
 * Misc options
 *
 */
interface MiscOptions {
    /**
     * Ignore label information and just test
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder testonly();


    /**
     * Seed random number generator
     *
     * @param seed
     * @return builder
     */
    SGDVowpalWabbitBuilder randomSeed(final int seed);
}
/**
 * This interface specify small subset of available options with some additional.
 *
 * In 90% [1] of cases you will need only these options.
 *
 * Though I highly recommend to follow https:*github.com/JohnLangford/vowpal_wabbit/wiki/Command-line-arguments
 * and read some tutorials.
 */
public interface SGDVowpalWabbitBuilder extends UpdatesOptions, RegularizationOptions, PredictionBoundaryOptions,
        LinkAndLossOptions, FeatureEngineeringFunctions, OptionsToSaveAndLoadModel, DebuggingOptions,
        FeatureSelectionOptions, OptionToExchangeRAMForQuality, MiscOptions {
    /**
     * Build learner
     *
     * @return VWFloatLearner instance
     */
    VWFloatLearner buildFloatLearner();
}

// [1] - this number is calculated using Data Science.
