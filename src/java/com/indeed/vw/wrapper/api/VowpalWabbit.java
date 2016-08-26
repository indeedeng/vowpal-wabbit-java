package com.indeed.vw.wrapper.api;

import com.google.common.base.Joiner;
import com.indeed.vw.wrapper.learner.VWFloatArrayLearner;
import com.indeed.vw.wrapper.learner.VWFloatLearner;
import com.indeed.vw.wrapper.learner.VWIntArrayLearner;
import com.indeed.vw.wrapper.learner.VWIntLearner;
import com.indeed.vw.wrapper.learner.VWLearners;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * This class a little bit simplifies passing vowpal wabbit parameters.
 *
 * For better parameters documentation read: https://github.com/JohnLangford/vowpal_wabbit/wiki/Command-line-arguments
 */
public class VowpalWabbit {
    private VowpalWabbit() {
    }

    enum Hash {
        strings, all
    }

    enum Loss {
        squared, classic, hinge, logistic, quantile, poisson
    }

    enum LDF {
        singleline, multiline
    }

    enum Link {
        identity, logistic, glf1, poisson
    }

    public static SGDVowpalWabbitBuilder builder() {
        return new Builder();
    }

    public static Builder advancedBuilder() {
        return new Builder();
    }

    public static class Builder implements SGDVowpalWabbitBuilder {
        private Builder() {
        }

        private final StringBuilder argumentsStringBuilder = new StringBuilder();
        private boolean verbose = false;
        /**
         * seed random number generator
         *
         * @param seed
         * @return builder
         */
        @Override
        public Builder random_seed(final int seed) {
            argumentsStringBuilder.append("--random_seed " + seed + " ");
            return this;
        }

        /**
         * size of example ring
         *
         * @param ringSize
         * @return builder
         */
        public Builder ring_size(final int ringSize) {
            argumentsStringBuilder.append("--ring_size " + ringSize + " ");
            return this;
        }

        /**
         * Set learning rate
         *
         * @param learningRate
         * @return builder
         */
        public Builder learning_rate(final double learningRate) {
            argumentsStringBuilder.append("--learning_rate " + learningRate + " ");
            return this;
        }

        /**
         * t power value
         *
         * @param powerT
         * @return builder
         */
        public Builder power_t(final double powerT) {
            argumentsStringBuilder.append("--power_t " + powerT + " ");
            return this;
        }

        /**
         * Set Decay factor for learning_rate between passes
         *
         * @param decay
         * @return builder
         */
        public Builder decay_learning_rate(final double decay) {
            argumentsStringBuilder.append("--decay_learning_rate " + decay + " ");
            return this;
        }

        /**
         * initial t value
         *
         * @param initialT
         * @return builder
         */
        public Builder initial_t(final double initialT) {
            argumentsStringBuilder.append("--initial_t " + initialT + " ");
            return this;
        }

        /**
         * Use existing regressor to determine which parameters may be updated.
         * If no initial_regressor given, also used for initial weights.
         *
         * @param featureMask
         * @return builder
         */
        public Builder feature_mask(final Path featureMask) {
            argumentsStringBuilder.append("--feature_mask " + featureMask + " ");
            return this;
        }

        /**
         * Initial regressor(s)
         *
         * @param initialRegressor
         * @return builder
         */
        @Override
        public Builder initial_regressor(final Path initialRegressor) {
            argumentsStringBuilder.append("--initial_regressor " + initialRegressor + " ");
            return this;
        }

        /**
         * Set all weights to an initial value of arg.
         *
         * @param weight
         * @return builder
         */
        public Builder initial_weight(final double weight) {
            argumentsStringBuilder.append("--initial_weight " + weight + " ");
            return this;
        }

        /**
         * make initial weights random
         *
         * @param arg
         * @return builder
         */
        public Builder random_weights(final double arg) {
            argumentsStringBuilder.append("--random_weights " + arg + " ");
            return this;
        }

        /**
         * Per feature regularization input file
         *
         * @param regularizationPath
         * @return builder
         */
        public Builder input_feature_regularizer(final Path regularizationPath) {
            argumentsStringBuilder.append("--input_feature_regularizer " + regularizationPath + " ");
            return this;
        }

        /**
         * how to hash the features. Available options: strings, all
         *
         * @param hash
         * @return builder
         */
        public Builder hash(final Hash hash) {
            argumentsStringBuilder.append("--hash " + hash + " ");
            return this;
        }

        /**
         * ignore namespaces beginning with character <arg>
         *
         * @param namespaceChar
         * @return builder
         */
        public Builder ignore(final char namespaceChar) {
            argumentsStringBuilder.append("--ignore " + namespaceChar + " ");
            return this;
        }

        /**
         * keep namespaces beginning with character <arg>
         *
         * @param namespaceChar
         * @return builder
         */
        public Builder keep(final char namespaceChar) {
            argumentsStringBuilder.append("--keep " + namespaceChar + " ");
            return this;
        }

        public static final char ANY_NAMESPACE = ':';

        /**
         * redefine namespaces beginning with characters of string S as namespace N.
         * <arg> shall be in form 'N:=S' where := is operator. Empty N or S are treated as default namespace.
         * Use ':' as a wildcard in S.
         *
         * @param newNamespace
         * @param namespaces
         * @return builder
         */
        public Builder redefine(final String newNamespace, final char... namespaces) {
            argumentsStringBuilder.append("--redefine " +
                    newNamespace + ":=" + Joiner.on("").join(Arrays.asList(namespaces)) +
                    " ");
            return this;
        }

        /**
         * number of bits in the feature table
         *
         * @param bitsNum
         * @return builder
         */
        @Override
        public Builder bit_precision(final int bitsNum) {
            argumentsStringBuilder.append("--bit_precision " + bitsNum + " ");
            return this;
        }

        /**
         * Don't add a constant feature
         *
         * @return builder
         */
        @Override
        public Builder noconstant() {
            argumentsStringBuilder.append("--noconstant ");
            return this;
        }

        /**
         * Set initial value of constant
         *
         * @param initialValue
         * @return builder
         */
        @Override
        public Builder constant(final double initialValue) {
            argumentsStringBuilder.append("--constant " + initialValue + " ");
            return this;
        }

        /**
         * Generate N grams. To generate N grams for a single namespace 'foo', arg should be fN.
         *
         * @param namespace
         * @param n
         * @return builder
         */
        @Override
        public Builder ngram(final char namespace, final int n) {
            if (namespace == ANY_NAMESPACE) {
                argumentsStringBuilder.append("--ngram " + n + " ");
                return this;
            }
            argumentsStringBuilder.append("--ngram " + namespace + "" + n + " ");
            return this;
        }

        /**
         * Generate skips in N grams. This in conjunction with the ngram tag can be used to generate generalized n-skip-k-gram.
         * To generate n-skips for a single namespace 'foo', arg should be fN.
         *
         * @param namespace
         * @param n
         * @return builder
         */
        @Override
        public Builder skips(final char namespace, final int n) {
            if (namespace == ANY_NAMESPACE) {
                argumentsStringBuilder.append("--skips " + n + " ");
                return this;
            }
            argumentsStringBuilder.append("--skips " + namespace + "" + n + " ");
            return this;
        }

        /**
         * limit to N features. To apply to a single namespace 'foo', arg should be fN
         *
         * @param n
         * @return builder
         */
        public Builder feature_limit(final int n) {
            argumentsStringBuilder.append("--feature_limit " + n + " ");
            return this;
        }

        /**
         * generate prefixes/suffixes of features; argument '+2a,-3b,+1'
         * means generate 2-char prefixes for namespace a, 3-char suffixes for b and 1 char
         * prefixes for default namespace
         *
         * @param arg
         * @return builder
         */
        public Builder affix(final String arg) {
            argumentsStringBuilder.append("--affix " + arg + " ");
            return this;
        }

        /**
         * compute spelling features for a give namespace (use '_' for default namespace)
         *
         * @param namespace
         * @return builder
         */
        public Builder spelling(final char namespace) {
            argumentsStringBuilder.append("--spelling " + namespace + " ");
            return this;
        }

        /**
         * read a dictionary for additional features (arg either 'x:file' or just 'file')
         *
         * @param file
         * @return builder
         */
        public Builder dictionary(final Path file) {
            argumentsStringBuilder.append("--dictionary " + file + " ");
            return this;
        }

        /**
         * look in this directory for dictionaries; defaults to current directory or env{PATH}
         *
         * @param dir
         * @return builder
         */
        public Builder dictionary_path(final Path dir) {
            argumentsStringBuilder.append("--dictionary_path " + dir + " ");
            return this;
        }

        /**
         * Create feature interactions of any level between namespaces.
         *
         * @param namespaces
         * @return builder
         */
        public Builder interactions(final char... namespaces) {
            argumentsStringBuilder.append("--interactions " +
                    Joiner.on("").join(Arrays.asList(namespaces)) + " ");
            return this;
        }

        /**
         * Use permutations instead of combinations for feature interactions of same namespace.
         *
         * @return builder
         */
        public Builder permutations() {
            argumentsStringBuilder.append("--permutations ");
            return this;
        }

        /**
         * Don't remove interactions with duplicate combinations of namespaces.
         * For ex. this is a duplicate: '-q ab -q ba' and a lot more in '-q ::'.
         *
         * @return builder
         */
        public Builder leave_duplicate_interactions() {
            argumentsStringBuilder.append("--leave_duplicate_interactions ");
            return this;
        }

        /**
         * Create and use quadratic features
         *
         * @param firstNameSpace  - first char of namespace of ":" for any
         * @param secondNamespace - second char of namespace of ":" for any
         * @return builder
         */
        @Override
        public Builder quadratic(final char firstNameSpace, final char secondNamespace) {
            argumentsStringBuilder.append("--quadratic " + firstNameSpace + secondNamespace + " ");
            return this;
        }

        /**
         * Create and use cubic features
         *
         * @param firstNameSpace  - first char of namespace of ":" for any
         * @param secondNamespace - second char of namespace of ":" for any
         * @param thirdNamespace  - third char of namespace of ":" for any
         * @return builder
         */
        @Override
        public Builder cubic(final char firstNameSpace, final char secondNamespace, final char thirdNamespace) {
            argumentsStringBuilder.append("--cubic " + firstNameSpace + secondNamespace + thirdNamespace + " ");
            return this;
        }

        /**
         * Ignore label information and just test
         *
         * @return builder
         */
        @Override
        public Builder testonly() {
            argumentsStringBuilder.append("--testonly ");
            return this;
        }

        /**
         * holdout period for test only, default 10
         *
         * @param holdout
         * @return builder
         */
        public Builder holdout_period(final int holdout) {
            argumentsStringBuilder.append("--holdout_period " + holdout + " ");
            return this;
        }

        /**
         * holdout after n training examples, default off (disables holdout_period)
         *
         * @param n
         * @return builder
         */
        public Builder holdout_after(final int n) {
            argumentsStringBuilder.append("--holdout_after " + n + " ");
            return this;
        }

        /**
         * Specify the number of passes tolerated when holdout loss doesn't decrease before early termination, default is 3
         *
         * @param passes
         * @return builder
         */
        public Builder early_terminate(final int passes) {
            argumentsStringBuilder.append("--early_terminate " + passes + " ");
            return this;
        }

        /**
         * Number of Training Passes
         *
         * @param passes
         * @return builder
         */
        @Override
        public Builder passes(final int passes) {
            argumentsStringBuilder.append("--passes " + passes + " ");
            return this;
        }

        /**
         * initial number of examples per pass
         *
         * @param examples
         * @return builder
         */
        public Builder initial_pass_length(final int examples) {
            argumentsStringBuilder.append("--initial_pass_length " + examples + " ");
            return this;
        }

        /**
         * number of examples to parse
         *
         * @param examples
         * @return builder
         */
        public Builder examples(final int examples) {
            argumentsStringBuilder.append("--examples " + examples + " ");
            return this;
        }

        /**
         * Smallest prediction to output
         *
         * @param min
         * @return builder
         */
        @Override
        public Builder min_prediction(final double min) {
            argumentsStringBuilder.append("--min_prediction " + min + " ");
            return this;
        }

        /**
         * Largest prediction to output
         *
         * @param max
         * @return builder
         */
        @Override
        public Builder max_prediction(final double max) {
            argumentsStringBuilder.append("--max_prediction " + max + " ");
            return this;
        }

        /**
         * turn this on to disregard order in which features have been defined. This will lead to smaller cache sizes
         *
         * @return builder
         */
        public Builder sort_features() {
            argumentsStringBuilder.append("--sort_features ");
            return this;
        }

        /**
         * Specify the loss function to be used, uses squared by default. Currently available ones are
         * squared, classic, hinge, logistic, quantile and poisson. (=squared)
         *
         * @param loss
         * @return builder
         */
        @Override
        public Builder loss_function(final Loss loss) {
            argumentsStringBuilder.append("--loss_function " + loss + " ");
            return this;
        }

        /**
         * Parameter \tau associated with Quantile loss. Defaults to 0.5 (=0.5)
         *
         * @param tau
         * @return builder
         */
        @Override
        public Builder quantile_tau(final double tau) {
            argumentsStringBuilder.append("--quantile_tau " + tau + " ");
            return this;
        }

        /**
         * l_1 lambda
         *
         * @param l1
         * @return builder
         */
        @Override
        public Builder l1(final double l1) {
            argumentsStringBuilder.append("--l1 " + l1 + " ");
            return this;
        }

        /**
         * l_2 lambda
         *
         * @param l2
         * @return builder
         */
        @Override
        public Builder l2(final double l2) {
            argumentsStringBuilder.append("--l2 " + l2 + " ");
            return this;
        }

        /**
         * use names for labels (multiclass, etc.) rather than integers, argument specified all possible labels, comma-sep,
         * eg "--named_labels Noun,Verb,Adj,Punc"
         *
         * @param labels
         * @return builder
         */
        public Builder named_labels(final String... labels) {
            argumentsStringBuilder.append("--named_labels " +
                    Joiner.on(",").join(Arrays.asList(labels)) + " ");
            return this;
        }

        /**
         * Final regressor
         *
         * @param regressor
         * @return builder
         */
        @Override
        public Builder final_regressor(final Path regressor) {
            argumentsStringBuilder.append("--final_regressor " + regressor + " ");
            return this;
        }

        /**
         * Output human-readable final regressor with numeric features
         *
         * @param model
         * @return builder
         */
        public Builder readable_model(final Path model) {
            argumentsStringBuilder.append("--readable_model " + model + " ");
            return this;
        }

        /**
         * Output human-readable final regressor with feature names.  Computationally expensive.
         *
         * @param model
         * @return builder
         */
        @Override
        public Builder invert_hash(final Path model) {
            argumentsStringBuilder.append("--invert_hash " + model + " ");
            return this;
        }

        /**
         * save extra state so learning can be resumed later with new data
         *
         * @return builder
         */
        public Builder save_resume() {
            argumentsStringBuilder.append("--save_resume ");
            return this;
        }

        /**
         * Save the model after every pass over data
         *
         * @return builder
         */
        public Builder save_per_pass() {
            argumentsStringBuilder.append("--save_per_pass ");
            return this;
        }

        /**
         * Per feature regularization output file
         *
         * @param regularizationFile
         * @return builder
         */
        public Builder output_feature_regularizer_binary(final Path regularizationFile) {
            argumentsStringBuilder.append("--output_feature_regularizer_binary " + regularizationFile + " ");
            return this;
        }

        /**
         * Per feature regularization output file, in text
         *
         * @param regularizationFile
         * @return builder
         */
        public Builder output_feature_regularizer_text(final Path regularizationFile) {
            argumentsStringBuilder.append("--output_feature_regularizer_text " + regularizationFile + " ");
            return this;
        }

        /**
         * User supplied ID embedded into the final regressor
         *
         * @param id
         * @return builder
         */
        public Builder id(final String id) {
            argumentsStringBuilder.append("--id " + id + " ");
            return this;
        }

        /**
         * File to output predictions to
         *
         * @param predictionFile
         * @return builder
         */
        @Override
        public Builder predictions(final Path predictionFile) {
            argumentsStringBuilder.append("--predictions " + predictionFile + " ");
            return this;
        }

        /**
         * File to output unnormalized predictions to
         *
         * @param regularizationFile
         * @return builder
         */
        public Builder raw_predictions(final Path regularizationFile) {
            argumentsStringBuilder.append("--raw_predictions " + regularizationFile + " ");
            return this;
        }

        /**
         * stores feature names and their regressor values.
         * Same dataset must be used for both regressor training and this mode.
         *
         * @param regressor
         * @return builder
         */
        public Builder audit_regressor(final Path regressor) {
            argumentsStringBuilder.append("--audit_regressor " + regressor + " ");
            return this;
        }

        /**
         * k-way bootstrap by online importance resampling
         *
         * @param k
         * @return builder
         */
        public Builder bootstrap(final int k) {
            argumentsStringBuilder.append("--bootstrap " + k + " ");
            return this;
        }

        /**
         * Use learning to search, argument=maximum action id or 0 for LDF
         *
         * @param maxActionID
         * @return builder
         */
        public Builder search(final int maxActionID) {
            argumentsStringBuilder.append("--search " + maxActionID + " ");
            return this;
        }

        /**
         * use experience replay at a specified level
         * [b=classification/regression, m=multiclass, c=cost sensitive] with specified buffer size
         *
         * @param arg
         * @return builder
         */
        public Builder replay_c(final String arg) {
            argumentsStringBuilder.append("--replay_c " + arg + " ");
            return this;
        }

        /**
         * Convert multiclass on <k> classes into a contextual bandit problem
         *
         * @param k
         * @return builder
         */
        public Builder cbify(final int k) {
            argumentsStringBuilder.append("--cbify " + k + " ");
            return this;
        }

        /**
         * Online explore-exploit for a contextual bandit problem with multiline action dependent features
         *
         * @return builder
         */
        public Builder cb_explore_adf() {
            argumentsStringBuilder.append("--cb_explore_adf ");
            return this;
        }

        /**
         * Online explore-exploit for a <k> action contextual bandit problem
         *
         * @param k
         * @return builder
         */
        public Builder cb_explore(final int k) {
            argumentsStringBuilder.append("--cb_explore " + k + " ");
            return this;
        }

        /**
         * Evaluate features as a policies
         *
         * @param
         * @return builder
         */
        public Builder multiworld_test(final String arg) {
            argumentsStringBuilder.append("--multiworld_test " + arg + " ");
            return this;
        }

        /**
         * Do Contextual Bandit learning with multiline action dependent features.
         *
         * @return builder
         */
        public Builder cb_adf() {
            argumentsStringBuilder.append("--cb_adf ");
            return this;
        }

        /**
         * Use contextual bandit learning with <k> costs
         *
         * @param k
         * @return builder
         */
        public Builder cb(final int k) {
            argumentsStringBuilder.append("--cb " + k + " ");
            return this;
        }

        /**
         * Use one-against-all multiclass learning with label dependent features.  Specify singleline or multiline.
         *
         * @param ldf
         * @return builder
         */
        public Builder csoaa_ldf(final LDF ldf) {
            argumentsStringBuilder.append("--csoaa_ldf " + ldf + " ");
            return this;
        }

        /**
         * Use weighted all-pairs multiclass learning with label dependent features.   Specify singleline or multiline.
         *
         * @param ldf
         * @return builder
         */
        public Builder wap_ldf(final LDF ldf) {
            argumentsStringBuilder.append("--wap_ldf " + ldf + " ");
            return this;
        }

        /**
         * Put weights on feature products from namespaces <n1> and <n2>
         *
         * @param arg
         * @return builder
         */
        public Builder interact(final String arg) {
            argumentsStringBuilder.append("--interact " + arg + " ");
            return this;
        }

        /**
         * One-against-all multiclass with <k> costs
         *
         * @param k
         * @return builder
         */
        public Builder csoaa(final int k) {
            argumentsStringBuilder.append("--csoaa " + k + " ");
            return this;
        }

        /**
         * One-against-all multilabel with <k> labels
         *
         * @param k
         * @return builder
         */
        public Builder multilabel_oaa(final int k) {
            argumentsStringBuilder.append("--multilabel_oaa " + k + " ");
            return this;
        }

        /**
         * Use online tree for multiclass
         *
         * @param k
         * @return builder
         */
        public Builder recall_tree(final int k) {
            argumentsStringBuilder.append("--recall_tree " + k + " ");
            return this;
        }

        /**
         * Use online tree for multiclass
         *
         * @param k
         * @return builder
         */
        public Builder log_multi(final int k) {
            argumentsStringBuilder.append("--log_multi " + k + " ");
            return this;
        }

        /**
         * Error correcting tournament with <k> labels
         *
         * @param k
         * @return builder
         */
        public Builder ect(final int k) {
            argumentsStringBuilder.append("--ect " + k + " ");
            return this;
        }

        /**
         * Online boosting with <N> weak learners
         *
         * @param n
         * @return builder
         */
        public Builder boosting(final int n) {
            argumentsStringBuilder.append("--boosting " + n + " ");
            return this;
        }

        /**
         * One-against-all multiclass with <k> labels
         *
         * @param k
         * @return builder
         */
        public Builder oaa(final int k) {
            argumentsStringBuilder.append("--oaa " + k + " ");
            return this;
        }

        /**
         * top k recommendation
         *
         * @param k
         * @return builder
         */
        public Builder top(final int k) {
            argumentsStringBuilder.append("--top " + k + " ");
            return this;
        }

        /**
         * use experience replay at a specified level
         * [b=classification/regression, m=multiclass, c=cost sensitive] with specified buffer size
         *
         * @param arg
         * @return builder
         */
        public Builder replay_m(final String arg) {
            argumentsStringBuilder.append("--replay_m " + arg + " ");
            return this;
        }

        /**
         * report loss as binary classification on -1,1
         *
         * @return builder
         */
        public Builder binary() {
            argumentsStringBuilder.append("--binary ");
            return this;
        }

        /**
         * Specify the link function: identity, logistic, glf1 or poisson  (=identity)
         *
         * @param link
         * @return builder
         */
        @Override
        public Builder link(final Link link) {
            argumentsStringBuilder.append("--link " + link + " ");
            return this;
        }

        /**
         * use stagewise polynomial feature learning
         *
         * @return builder
         */
        public Builder stage_poly() {
            argumentsStringBuilder.append("--stage_poly ");
            return this;
        }

        /**
         * use low rank quadratic features with field aware weights
         *
         * @param firstNamespace  - first char of namespace of ":" for any
         * @param secondNamespace - second char of namespace of ":" for any
         * @param k
         * @return builder
         */
        public Builder lrqfa(final char firstNamespace, final char secondNamespace, final int k) {
            argumentsStringBuilder.append("--lrqfa " + firstNamespace + secondNamespace + k + " ");
            return this;
        }

        /**
         * use low rank quadratic features
         *
         * @param firstNamespace  - first char of namespace of ":" for any
         * @param secondNamespace - second char of namespace of ":" for any
         * @param k
         * @return builder
         */
        @Override
        public Builder lrq(final char firstNamespace, final char secondNamespace, final int k) {
            argumentsStringBuilder.append("--lrq " + firstNamespace + secondNamespace + k + " ");
            return this;
        }


        /**
         * use dropout training for low rank quadratic features
         *
         * @return builder
         */
        @Override
        public Builder lrqdropout() {
            argumentsStringBuilder.append("--lrqdropout ");
            return this;
        }

        /**
         * create link function with polynomial d
         *
         * @param d
         * @return builder
         */
        public Builder autolink(final int d) {
            argumentsStringBuilder.append("--autolink " + d + " ");
            return this;
        }

        /**
         * rank for reduction-based matrix factorization
         *
         * @param rank
         * @return builder
         */
        public Builder new_mf(final int rank) {
            argumentsStringBuilder.append("--new_mf " + rank + " ");
            return this;
        }

        /**
         * Sigmoidal feedforward network with <k> hidden units
         *
         * @param units
         * @return builder
         */
        public Builder nn(final int units) {
            argumentsStringBuilder.append("--nn " + units + " ");
            return this;
        }

        /**
         * Confidence after training
         *
         * @return builder
         */
        public Builder confidence_after_training() {
            argumentsStringBuilder.append("--confidence_after_training ");
            return this;
        }

        /**
         * Get confidence for binary predictions
         *
         * @return builder
         */
        public Builder confidence() {
            argumentsStringBuilder.append("--confidence ");
            return this;
        }

        /**
         * enable active learning with cover
         *
         * @return builder
         */
        public Builder active_cover() {
            argumentsStringBuilder.append("--active_cover ");
            return this;
        }

        /**
         * enable active learning
         *
         * @return builder
         */
        public Builder active() {
            argumentsStringBuilder.append("--active ");
            return this;
        }

        /**
         * use experience replay at a specified level
         * [b=classification/regression, m=multiclass, c=cost sensitive] with specified buffer size
         *
         * @param arg
         * @return builder
         */
        public Builder replay_b(final String arg) {
            argumentsStringBuilder.append("--replay_b " + arg + " ");
            return this;
        }

        /**
         * Online Newton with Oja's Sketch
         *
         * @return builder
         */
        public Builder OjaNewton() {
            argumentsStringBuilder.append("--OjaNewton ");
            return this;
        }

        /**
         * use bfgs optimization
         *
         * @return builder
         */
        public Builder bfgs() {
            argumentsStringBuilder.append("--bfgs ");
            return this;
        }

        /**
         * use conjugate gradient based optimization
         *
         * @return builder
         */
        public Builder conjugate_gradient() {
            argumentsStringBuilder.append("--conjugate_gradient ");
            return this;
        }

        /**
         * Run lda with <int> topics
         *
         * @param topics
         * @return builder
         */
        public Builder lda(final int topics) {
            argumentsStringBuilder.append("--lda " + topics + " ");
            return this;
        }

        /**
         * do no learning
         *
         * @return builder
         */
        public Builder noop() {
            argumentsStringBuilder.append("--noop ");
            return this;
        }

        /**
         * rank for matrix factorization.
         *
         * @param rank
         * @return builder
         */
        public Builder rank(final int rank) {
            argumentsStringBuilder.append("--rank " + rank + " ");
            return this;
        }

        /**
         * Streaming Stochastic Variance Reduced Gradient
         *
         * @return builder
         */
        public Builder svrg() {
            argumentsStringBuilder.append("--svrg ");
            return this;
        }

        /**
         * FTRL: Follow the Proximal Regularized Leader
         *
         * @return builder
         */
        public Builder ftrl() {
            argumentsStringBuilder.append("--ftrl ");
            return this;
        }

        /**
         * FTRL: Parameter-free Stochastic Learning
         *
         * @return builder
         */
        public Builder pistol() {
            argumentsStringBuilder.append("--pistol ");
            return this;
        }

        /**
         * kernel svm
         *
         * @return builder
         */
        public Builder ksvm() {
            argumentsStringBuilder.append("--ksvm ");
            return this;
        }

        /**
         * use regular stochastic gradient descent update.
         *
         * @return builder
         */
        @Override
        public Builder sgd() {
            argumentsStringBuilder.append("--sgd ");
            return this;
        }

        /**
         * use adaptive, individual learning rates.
         *
         * @return builder
         */
        @Override
        public Builder adaptive() {
            argumentsStringBuilder.append("--adaptive ");
            return this;
        }

        /**
         * use safe/importance aware updates.
         *
         * @return builder
         */
        @Override
        public Builder invariant() {
            argumentsStringBuilder.append("--invariant ");
            return this;
        }

        /**
         * use per feature normalized updates
         *
         * @return builder
         */
        @Override
        public Builder normalized() {
            argumentsStringBuilder.append("--normalized ");
            return this;
        }

        /**
         * use per feature normalized updates (=0)
         *
         * @param l2
         * @return builder
         */
        public Builder sparse_l2(final double l2) {
            argumentsStringBuilder.append("--sparse_l2 " + l2 + " ");
            return this;
        }

        /**
         * Example Set
         *
         * @param dataFile
         * @return builder
         */
        @Override
        public Builder data(final Path dataFile) {
            argumentsStringBuilder.append("--data " + dataFile + " ");
            return this;
        }

        /**
         * Use a cache.  The default is <data>.cache
         *
         * @return builder
         */
        public Builder cache() {
            argumentsStringBuilder.append("--cache ");
            return this;
        }

        /**
         * The location(s) of cache_file.
         *
         * @param cacheFile
         * @return builder
         */
        @Override
        public Builder cache_file(final Path cacheFile) {
            argumentsStringBuilder.append("--cache_file " + cacheFile + " ");
            return this;
        }

        /**
         * do not reuse existing cache: create a new one always
         *
         * @return builder
         */
        public Builder kill_cache() {
            argumentsStringBuilder.append("--kill_cache ");
            return this;
        }

        /**
         * use gzip format whenever possible. If a cache file is being created,
         * this option creates a compressed cache file.
         * A mixture of raw-text & compressed inputs are supported with autodetection.
         *
         * @return builder
         */
        public Builder compressed() {
            argumentsStringBuilder.append("--compressed ");
            return this;
        }

        /**
         * Add vowpal wabit argument
         *
         * @param argumentLine
         * @return builder
         */
        public Builder parameter(final String argumentLine) {
            argumentsStringBuilder.append(argumentLine).append(' ');
            return this;
        }

        /**
         * Get command option will be passes to VWLearner
         *
         * @return command options
         */
        public String getCommand() {
            return argumentsStringBuilder.toString() + (verbose ? "" : " --quiet");
        }

        /**
         *
         * @return VWIntLearner object
         */
        @Override
        public VWFloatLearner buildFloatLearner() {
            return (VWFloatLearner) VWLearners.create(getCommand());
        }

        /**
         *
         * @return VWIntLearner object
         */
        public VWIntLearner buildIntLearner() {
            return (VWIntLearner) VWLearners.create(getCommand());
        }

        /**
         *
         * @return VWFloatArrayLearner object
         */
        public VWFloatArrayLearner buildFloatArrayLearner() {
            return (VWFloatArrayLearner) VWLearners.create(getCommand());
        }

        /**
         *
         * @return VWIntArrayLearner object
         */
        public VWIntArrayLearner buildIntArrayLearner() {
            return (VWIntArrayLearner) VWLearners.create(getCommand());
        }
    }
}