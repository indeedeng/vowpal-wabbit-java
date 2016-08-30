package com.indeed.vw.wrapper.api;

import com.indeed.vw.wrapper.learner.VWFloatLearner;

import java.nio.file.Path;

/**
 * This interface specify small subset of available options with some additional.
 *
 * In 90% of cases you will need only these options
 * Though I highly recommend to follow https://github.com/JohnLangford/vowpal_wabbit/wiki/Command-line-arguments
 * and read some tutorials.
 */
public interface SGDVowpalWabbitBuilder {
    // Updates options
    // ===============
    //
    // These options automates and hides complexity of learning rates parameter tunning
    // Notice that by default adaptive, invariant and normalized are turned on and to remove them
    // you need to pass sgd parameter
    //
    // adaptive will tune learning rates individually per feature, highly recommended option, if you not specify it
    // rare features will have too small weights
    //
    // invariant option allows consider example's weight during online learning;
    // without this option example's weight will be ignored
    // Also this option usually improves model quality
    //
    // normalize option turns on online feature scaling
    // in a lot of cases you want to remove this option
    // specially in case of very sparse boolean feature space
    //
    // always play with these options first it usually gives the biggest gain

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
    public SGDVowpalWabbitBuilder learning_rate(final double learningRate);

    // Regularization options
    // ======================
    //
    // specify these values to pervent overfitting
    // l2 regularization will tend to keep weight small
    // while l1 will tend to zero irrelevant weights
    // and can be seen as feature selection

    /**
     * l_1 lambda
     *
     * @param l1
     * @return builder
     */
    SGDVowpalWabbitBuilder l1(double l1);

    /**
     * l_2 lambda
     *
     * @param l2
     * @return builder
     */
    SGDVowpalWabbitBuilder l2(double l2);


    // Link and loss functions
    // =======================
    //
    // loss function defines gradient using to update feature weights
    // link applied to linear composition
    // Possibility to set these to functions makes vowpal wabbit GLM framework
    //
    // Check https://github.com/JohnLangford/vowpal_wabbit/wiki/Loss-functions

    /**
     * Specify the loss function to be used, uses squared by default. Currently available ones are
     * squared, classic, hinge, logistic, quantile and poisson. (=squared)
     *
     * @param loss
     * @return builder
     */
    SGDVowpalWabbitBuilder loss_function(VowpalWabbit.Loss loss);

    /**
     * Parameter \tau associated with Quantile loss. Defaults to 0.5 (=0.5)
     *
     * @param tau
     * @return builder
     */
    SGDVowpalWabbitBuilder quantile_tau(double tau);

    /**
     * Specify the link function: identity, logistic, glf1 or poisson  (=identity)
     *
     * @param link
     * @return builder
     */
    SGDVowpalWabbitBuilder link(VowpalWabbit.Link link);


    // Prediction boundary options
    // ===========================

    /**
     * Smallest prediction to output
     *
     * @param min
     * @return builder
     */
    SGDVowpalWabbitBuilder min_prediction(double min);

    /**
     * Largest prediction to output
     *
     * @param max
     * @return builder
     */
    SGDVowpalWabbitBuilder max_prediction(double max);

    // Feature engineering functions
    // =============================
    //
    // Feature engineering is a strong part of vowpal wabbit
    // To refer namespace you its first character
    // To refer any namespaces use ':'

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
    SGDVowpalWabbitBuilder constant(double initialValue);

    /**
     * Generate N grams. To generate N grams for a single namespace 'foo', arg should be fN.
     *
     * @param namespace
     * @param n
     * @return builder
     */
    SGDVowpalWabbitBuilder ngram(String namespace, int n);

    /**
     * Generate skips in N grams. This in conjunction with the ngram tag can be used to generate generalized n-skip-k-gram.
     * To generate n-skips for a single namespace 'foo', arg should be fN.
     *
     * @param namespace
     * @param n
     * @return builder
     */
    SGDVowpalWabbitBuilder skips(String namespace, int n);

    /**
     * Create and use quadratic features
     *
     * @param firstNameSpace - namespace or ":" for any
     * @param secondNamespace - namespace or ":" for any
     * @return builder
     */
    SGDVowpalWabbitBuilder quadratic(String firstNameSpace, String secondNamespace);

    /**
     * Create and use cubic features
     *
     * @param firstNameSpace - namespace or ":" for any
     * @param secondNamespace - namespace or ":" for any
     * @param thirdNamespace - namespace or ":" for any
     * @return builder
     */
    SGDVowpalWabbitBuilder cubic(String firstNameSpace, String secondNamespace, String thirdNamespace);

    /**
     * use low rank quadratic feature-aware weights
     *
     * @param firstNamespace  - namespace or ":" for any
     * @param secondNamespace - namespace or ":" for any
     * @param k
     * @return builder
     */
    SGDVowpalWabbitBuilder lrqfa(String firstNamespace, String secondNamespace, int k);

    // Options to save and load model
    // ==============================

    /**
     * Initial regressor(s)
     *
     * @param initialRegressor
     * @return builder
     */
    SGDVowpalWabbitBuilder initial_regressor(Path initialRegressor);

    /**
     * Final regressor
     *
     * @param regressor
     * @return builder
     */
    SGDVowpalWabbitBuilder final_regressor(Path regressor);

    // Option for debugging model
    // =========================
    /**
     * Output human-readable final regressor with numeric features
     *
     * @param model
     * @return builder
     */
    SGDVowpalWabbitBuilder readable_model(final Path model);

    /**
     * Make vowpal wabbit writing debug and performance information to stderr
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder verbose();

    // Multi-pass options
    // ==================
    //
    // If you have small train dataset then you may try to pass through it several times
    // in this case you need to specify cache file - optimized input representation - path
    /**
     * Number of Training Passes
     *
     * @param passes
     * @return builder
     */
    SGDVowpalWabbitBuilder passes(int passes);

    // Option to exchange RAM for some quality
    // =======================================

    /**
     * number of bits in the feature table
     *
     * @param bitsNum
     * @return builder
     */
    SGDVowpalWabbitBuilder bit_precision(int bitsNum);

    // Misc
    // ====

    /**
     * Ignore label information and just test
     *
     * @return builder
     */
    SGDVowpalWabbitBuilder testonly();


    /**
     * seed random number generator
     *
     * @param seed
     * @return builder
     */
    SGDVowpalWabbitBuilder random_seed(int seed);

    /**
     *
     * @return VWFloatLearner inctance
     */
    VWFloatLearner buildFloatLearner();
}
