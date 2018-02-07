package com.indeed.vw.wrapper.api.parameters;

import com.indeed.vw.wrapper.learner.VWFloatLearner;
import com.indeed.vw.wrapper.learner.VWIntLearner;

import java.nio.file.Path;

/**
 * Updates options. <p>
 *
 * These options automates and hides complexity of learning rates parameter tuning. <p>
 * Notice that by default adaptive, invariant and normalized are turned on and to remove them
 * you need to pass sgd parameter. <p>
 *
 * Adaptive option tunes learning rates individually per feature. This option is highly recommended - if you do not specify it,
 * then rare features will have too small weights. <p>
 *
 * Invariant option allows consider example's weight during online learning.
 * Without this option example's weight will be ignored.
 * Also this option usually improves model quality. <p>
 *
 * Normalize option turns on online feature scaling.
 * In a lot of cases you want to remove this option,
 * specially in case of very sparse boolean feature space. <p>
 *
 * Always play with these options first, it usually gives the biggest gain. <p>
 */
interface UpdatesOptions {
    /**
     * use regular stochastic gradient descent update. <p>
     * removes adaptive, invariant and normalized options <p>
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder sgd();

    /**
     * use adaptive, individual learning rates. <p>
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder adaptive();

    /**
     * use safe/importance aware updates. <p>
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder invariant();

    /**
     * use per feature normalized updates <p>
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder normalized();

    /**
     * Set learning rate <p>
     *
     * @param learningRate learning rate. Must be positive
     * @return builder
     */
    SGDVowpalWabbitBuilder learningRate(final double learningRate);
}

/**
 * Regularization options. <p>
 *
 * Specify these values to prevent overfitting. <p></p>
 * l2 regularization will tend to keep weight small
 * while l1 will tend to zero irrelevant weights
 * and can be seen as feature selection <p>
 */
interface RegularizationOptions {
    /**
     * l_1 lambda <p>
     *
     * @param l1 l1 regularization. Must be not negative. Default 0
     * @return builder
     */
    SGDVowpalWabbitBuilder l1(final double l1);

    /**
     * l_2 lambda <p>
     *
     * @param l2  l2 regularization. Must be not negative. Default 0
     * @return builder
     */
    SGDVowpalWabbitBuilder l2(final double l2);
}

/**
 * Link and Loss options. <p>
 *
 * loss function defines gradient using to update feature weights;
 * link function is applied to linear composition.<p>
 * Possibility to set these to functions makes vowpal wabbit GLM framework. <p>
 *
 * Check https: github.com/JohnLangford/vowpal_wabbit/wiki/Loss-functions <p>
 */
interface LinkAndLossOptions {
    /**
     * Specify the loss function to be used, uses squared by default. Currently available ones are <p>
     * squared, classic, hinge, logistic, quantile and poisson. (=squared) <p>
     *
     * @param loss loss function
     * @return builder
     */
    SGDVowpalWabbitBuilder lossFunction(final Loss loss);

    /**
     * Parameter \tau associated with Quantile loss. Defaults to 0.5 (=0.5) <p>
     *
     * @param tau tau quantile. Must be in range [0, 1].
     * @return builder
     */
    SGDVowpalWabbitBuilder quantileTau(final double tau);

    /**
     * Specify the link function: identity, logistic, glf1 or poisson  (=identity) <p>
     *
     * @param link link function
     * @return builder
     */
    SGDVowpalWabbitBuilder link(final Link link);

}

/**
 * Prediction boundary options <p>
 *
 */
interface PredictionBoundaryOptions {
    /**
     * Smallest prediction to output <p>
     *
     * @param min minimum prediction. Inclusive
     * @return builder
     */
    SGDVowpalWabbitBuilder minPrediction(final double min);

    /**
     * Largest prediction to output <p>
     *
     * @param max maximum prediction. Inclusive
     * @return builder
     */
    SGDVowpalWabbitBuilder maxPrediction(final double max);

}

/**
 * Feature engineering functions. <p>
 *
 * Feature engineering is a strong part of vowpal wabbit. <p>
 * To refer any namespaces use VowpalWabbit.ANY_NAMESPACE <p>
 */
interface FeatureEngineeringFunctions {
    /**
     * Don't add a constant feature <p>
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder noconstant();

    /**
     * Set initial value of constant <p>
     *
     * @param initialValue bias initial value
     * @return builder
     */
    SGDVowpalWabbitBuilder constant(final double initialValue);

    /**
     * Generate N grams. To generate N grams for a single namespace 'foo', arg should be fN. <p>
     *
     * @param namespace namespace name, or ':' for any namespaces
     * @param n size of n-gram.
     * @return builder
     */
    SGDVowpalWabbitBuilder ngram(final String namespace, final int n);

    /**
     * Generate skips in N grams. This in conjunction with the ngram tag can be used to generate generalized n-skip-k-gram. <p>
     * To generate n-skips for a single namespace 'foo', arg should be fN. <p>
     *
     * @param namespace namespace name, or ':' for any namespaces
     * @param n size of skips n-gram.
     * @return builder
     */
    SGDVowpalWabbitBuilder skips(final String namespace, final int n);

    /**
     * Create and use quadratic features <p>
     *
     * @param firstNameSpace namespace name, or ':' for any namespaces
     * @param secondNamespace namespace name, or ':' for any namespaces
     * @return builder
     */
    SGDVowpalWabbitBuilder quadratic(final String firstNameSpace, final String secondNamespace);

    /**
     * Create and use cubic features <p>
     *
     * @param firstNameSpace namespace name, or ':' for any namespaces
     * @param secondNamespace namespace name, or ':' for any namespaces
     * @param thirdNamespace namespace name, or ':' for any namespaces
     * @return builder
     */
    SGDVowpalWabbitBuilder cubic(final String firstNameSpace, final String secondNamespace, final String thirdNamespace);

    /**
     * use low rank quadratic feature-aware weights <p>
     *
     * @param firstNamespace  namespace name, or ':' for any namespaces
     * @param secondNamespace namespace name, or ':' for any namespaces
     * @param k  k factorized matrices width
     * @return builder
     */
    SGDVowpalWabbitBuilder lrqfa(final String firstNamespace, final String secondNamespace, final int k);
}

/**
 * Options to save and load model. <p>
 *
 */
interface OptionsToSaveAndLoadModel {
    /**
     * Initial regressor(s) <p>
     *
     * @param initialRegressor path where to read initial regressor
     * @return builder
     */
    SGDVowpalWabbitBuilder initialRegressor(final Path initialRegressor);

    /**
     * Final regressor <p>
     *
     * @param regressor path where to write final regressor
     * @return builder
     */
    SGDVowpalWabbitBuilder finalRegressor(final Path regressor);
}

/**
 * Options for debugging model. <p>
 *
 */
interface DebuggingOptions {
    /**
     * Output human-readable final regressor with numeric features <p>
     *
     * @param model path where to write readable model
     * @return builder
     */
    SGDVowpalWabbitBuilder readableModel(final Path model);

    /**
     * Output human-readable final regressor with feature names <p>
     *
     * @param model path where to write readable model
     * @return builder
     */
    SGDVowpalWabbitBuilder invertHash(final Path model);

    /**
     * Make vowpal wabbit writing debug and performance information to stderr <p>
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder verbose();
}

/**
 * Feature selection options. <p>
 *
 * Usual way to do feature selection in GLM framework is to set l1 regularization.
 * This type of regularization will tend to zero weights of unrellevant variables.
 * Although this a good way to go, setting l1 usually decreases model quality. <p>
 *
 * Vowpal wabbit has two workarounds. One of them is ftrl-proximal optimization algorithm.
 * This algorithm accumulates updates in a buffer vector and changes actual model
 * weights only if updates are big enough. As result, the produced model tends to be very sparse.
 * I order to get benefit from this option you still need to set l1. Also this is the algorithm described
 * in famous google paper "ctr-modeling, a view from trenches". <p>
 *
 * Other option is feature mask. The idea of feature mask is that you train a model with very big l1 regularization
 * to do feature selection in a first pass. Then in a second pass you use previous model to zero unrellevant features
 * and you can train without l1 regularization at all. <p>
 */
interface FeatureSelectionOptions {
    /**
     * FTRL: Follow the Proximal Regularized Leader <p>
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder ftrl();

    /**
     * Use existing regressor to determine which parameters may be updated. <p>
     * If no initialRegressor given, also used for initial weights. <p>
     *
     * @param featureMask path where to read feature mask
     * @return builder
     */
    SGDVowpalWabbitBuilder featureMask(final Path featureMask);
}

/**
 * Option to exchange RAM for some quality. <p>
 *
 */
interface OptionToExchangeRAMForQuality {
    /**
     * number of bits in the feature table. <p>
     *
     * It mean feature table will have 2^bitsNum size <p>
     *
     * @param bitsNum number of bits in hash feature table.
     * @return builder
     */
    SGDVowpalWabbitBuilder bitPrecision(int bitsNum);
}

/**
 * Misc options <p>
 *
 */
interface MiscOptions {
    /**
     * Ignore label information and just test <p>
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder testonly();


    /**
     * Seed random number generator <p>
     *
     * @param seed random generator seed
     * @return builder
     */
    SGDVowpalWabbitBuilder randomSeed(final int seed);
}
/**
 * This interface specify small subset of available options with some additional documentation. <p>
 *
 * In 90% [1] of cases you will need only these options. <p>
 *
 * Though I highly recommend to follow <a href="https://github.com/JohnLangford/vowpal_wabbit/wiki/Command-line-arguments">https://github.com/JohnLangford/vowpal_wabbit/wiki/Command-line-arguments</a>
 * and read some tutorials. <p>
 */
public interface SGDVowpalWabbitBuilder extends UpdatesOptions, RegularizationOptions, PredictionBoundaryOptions,
        LinkAndLossOptions, FeatureEngineeringFunctions, OptionsToSaveAndLoadModel, DebuggingOptions,
        FeatureSelectionOptions, OptionToExchangeRAMForQuality, MiscOptions {
    /**
     * Build learner <p>
     *
     * @return VWFloatLearner instance
     */
    VWFloatLearner buildFloatLearner();

    VWIntLearner buildIntLearner();
}

// [1] - this number is calculated using Data Science.
