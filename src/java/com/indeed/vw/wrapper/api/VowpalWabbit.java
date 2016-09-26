package com.indeed.vw.wrapper.api;

import com.google.common.base.Joiner;
import com.indeed.vw.wrapper.api.parameters.Link;
import com.indeed.vw.wrapper.api.parameters.Loss;
import com.indeed.vw.wrapper.api.parameters.SGDVowpalWabbitBuilder;
import com.indeed.vw.wrapper.learner.VWFloatArrayLearner;
import com.indeed.vw.wrapper.learner.VWFloatLearner;
import com.indeed.vw.wrapper.learner.VWIntArrayLearner;
import com.indeed.vw.wrapper.learner.VWIntLearner;
import com.indeed.vw.wrapper.learner.VWLearners;
import org.apache.log4j.Logger;

import java.nio.file.Path;
import java.util.Arrays;

/**
* This class contains factory methods for VWLearner builder object. <p>
 *
* For better parameters documentation read: <a href="https://github.com/JohnLangford/vowpal_wabbit/wiki/Command-line-arguments">https://github.com/JohnLangford/vowpal_wabbit/wiki/Command-line-arguments</a> <p>
 */
public class VowpalWabbit {
    /**
    * Pass this constant as namespace parameter if you want to set some feature engineering for all namespaces. <p> <p>
     *
     */
    public static final String ANY_NAMESPACE = ":";
    private static final Logger logger = Logger.getLogger(VowpalWabbit.class);

    private VowpalWabbit() {
    }

    public enum Hash {
        strings, all
    }

    public enum LDF {
        singleline, multiline
    }

    /**
     * Create builder for Vowpal Wabbit learner <p>
     *
     * @return SGDVowpalWabbitBuilder
     */
    public static SGDVowpalWabbitBuilder builder() {
        return new Builder();
    }

    /**
     * Create advanced builder for Vowpal Wabbit learner <p>
     *
     * @return SGDVowpalWabbitBuilder
     */
    public static Builder advancedBuilder() {
        return new Builder();
    }

    /**
     * Builder for Vowpal Wabbit learner. <p>
     *
     */
    public static class Builder implements SGDVowpalWabbitBuilder {
        private Builder() {
        }

        private final StringBuilder argumentsStringBuilder = new StringBuilder();
        private boolean verbose = false;

        /**
         * Make vowpal wabbit writing debug and performance information to stderr <p>
         *
         * @return builder
         */
        public Builder verbose() {
            verbose = true;
            return this;
        }

        /**
         * seed random number generator <p>
         *
         * @param seed random generator seed
         * @return builder
         */
        @Override
        public Builder randomSeed(final int seed) {
            argumentsStringBuilder.append("--random_seed " + seed + " ");
            return this;
        }

        /**
         * size of example ring buffer <p>
         *
         * @param ringSize size of example ring
         * @return builder
         */
        public Builder ringSize(final int ringSize) {
            argumentsStringBuilder.append("--ring_size " + ringSize + " ");
            return this;
        }

        /**
         * Set learning rate <p>
         *
         * @param learningRate learningRate learning rate. Must be positive
         * @return builder
         */
        @Override
        public Builder learningRate(final double learningRate) {
            argumentsStringBuilder.append("--learning_rate " + learningRate + " ");
            return this;
        }

        /**
         * t power value <p>
         *
         * @param powerT t power value
         * @return builder
         */
        public Builder powerT(final double powerT) {
            argumentsStringBuilder.append("--power_t " + powerT + " ");
            return this;
        }

        /**
         * Set Decay factor for learning_rate between passes <p>
         *
         * @param decay exponential decay
         * @return builder
         */
        public Builder decayLearningRate(final double decay) {
            argumentsStringBuilder.append("--decay_learning_rate " + decay + " ");
            return this;
        }

        /**
         * initial t value <p>
         *
         * @param initialT initial t value
         * @return builder
         */
        public Builder initialT(final double initialT) {
            argumentsStringBuilder.append("--initial_t " + initialT + " ");
            return this;
        }

        /**
         * Use existing regressor to determine which parameters may be updated. <p>
         * If no initialRegressor given, also used for initial weights. <p>
         *
         * @param featureMask path where to read feature mask
         * @return builder
         */
        @Override
        public Builder featureMask(final Path featureMask) {
            argumentsStringBuilder.append("--feature_mask " + featureMask + " ");
            return this;
        }

        /**
         * Initial regressor(s) <p>
         *
         * @param initialRegressor path where to read initial regressor
         * @return builder
         */
        @Override
        public Builder initialRegressor(final Path initialRegressor) {
            argumentsStringBuilder.append("--initial_regressor " + initialRegressor + " ");
            return this;
        }

        /**
         * Set all weights to an initial value of arg. <p>
         *
         * @param weight initial weight value
         * @return builder
         */
        public Builder initialWeight(final double weight) {
            argumentsStringBuilder.append("--initial_weight " + weight + " ");
            return this;
        }

        /**
         * make initial weights random <p>
         *
         * @param arg random maximum
         * @return builder
         */
        public Builder randomWeights(final double arg) {
            argumentsStringBuilder.append("--random_weights " + arg + " ");
            return this;
        }

        /**
         * Per feature regularization input file <p>
         *
         * @param regularizationPath  path to regularization input file
         * @return builder
         */
        public Builder inputFeatureRegularizer(final Path regularizationPath) {
            argumentsStringBuilder.append("--input_feature_regularizer " + regularizationPath + " ");
            return this;
        }

        /**
         * how to hash the features. Available options: strings, all <p>
         *
         * @param hash hash strategy
         * @return builder
         */
        public Builder hash(final Hash hash) {
            argumentsStringBuilder.append("--hash " + hash + " ");
            return this;
        }

        /**
         * ignore namespace  &lt;arg&gt; <p>
         *
         * @param namespace namespace name
         * @return builder
         */
        public Builder ignore(final String namespace) {
            argumentsStringBuilder.append("--ignore " + namespace.charAt(0) + " ");
            return this;
        }

        /**
         * keep namespace &lt;arg&gt; <p>
         *
         * @param namespace namespace name
         * @return builder
         */
        public Builder keep(final String namespace) {
            argumentsStringBuilder.append("--keep " + namespace.charAt(0) + " ");
            return this;
        }

        /**
         * redefine namespaces beginning with characters of string S as namespace N. <p>
         * &lt;arg&gt; shall be in form 'N:=S' where := is operator. Empty N or S are treated as default namespace. <p>
         * Use ':' as a wildcard in S. <p>
         *
         * @param newNamespace new namespace name
         * @param namespaces old namespaces names
         * @return builder
         */
        public Builder redefine(final String newNamespace, final String ... namespaces) {
            final StringBuilder oldNamespaces = new StringBuilder();
            for (final String namespace : namespaces) {
                oldNamespaces.append(namespace.charAt(0));
            }
            argumentsStringBuilder.append("--redefine " +
                    newNamespace + ":=" + oldNamespaces +
                    " ");
            return this;
        }

        /**
         * number of bits in the feature table. <p>
         *
         * It mean feature table will have 2^bitsNum size <p>
         *
         * @param bitsNum bitsNum number of bits in hash feature table.
         * @return builder
         */
        @Override
        public Builder bitPrecision(final int bitsNum) {
            argumentsStringBuilder.append("--bit_precision " + bitsNum + " ");
            return this;
        }

        /**
         * Don't add a constant feature <p>
         *
         * @return builder
         */
        @Override
        public Builder noconstant() {
            argumentsStringBuilder.append("--noconstant ");
            return this;
        }

        /**
         * Set initial value of constant <p>
         *
         * @param initialValue bias initial value
         * @return builder
         */
        @Override
        public Builder constant(final double initialValue) {
            argumentsStringBuilder.append("--constant " + initialValue + " ");
            return this;
        }

        /**
         * Generate N grams. To generate N grams for a single namespace 'foo', arg should be fN. <p>
         *
         * @param namespace namespace name, or ':' for any namespaces
         * @param n size of n-gram.
         * @return builder
         */
        @Override
        public Builder ngram(final String namespace, final int n) {
            if (namespace.equals(ANY_NAMESPACE)) {
                argumentsStringBuilder.append("--ngram " + n + " ");
                return this;
            }
            argumentsStringBuilder.append("--ngram " + namespace.charAt(0) + "" + n + " ");
            return this;
        }

        /**
         * Generate skips in N grams. This in conjunction with the ngram tag can be used to generate generalized n-skip-k-gram. <p>
         * To generate n-skips for a single namespace 'foo', arg should be fN. <p>
         *
         * @param namespace namespace name, or ':' for any namespaces
         * @param n size of skips n-gram.
         * @return builder
         */
        @Override
        public Builder skips(final String namespace, final int n) {
            if (namespace.equals(ANY_NAMESPACE)) {
                argumentsStringBuilder.append("--skips " + n + " ");
                return this;
            }
            argumentsStringBuilder.append("--skips " + namespace.charAt(0) + "" + n + " ");
            return this;
        }

        /**
         * limit to N features. To apply to a single namespace 'foo', arg should be fN <p>
         *
         * @param n number of features
         * @return builder
         */
        public Builder featureLimit(final int n) {
            argumentsStringBuilder.append("--feature_limit " + n + " ");
            return this;
        }

        /**
         * generate prefixes/suffixes of features; argument '+2a,-3b,+1' <p>
         * means generate 2-char prefixes for namespace a, 3-char suffixes for b and 1 char <p>
         * prefixes for default namespace <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder affix(final String arg) {
            argumentsStringBuilder.append("--affix " + arg + " ");
            return this;
        }

        /**
         * compute spelling features for a give namespace (use '_' for default namespace) <p>
         *
         * @param namespace namespace
         * @return builder
         */
        public Builder spelling(final String namespace) {
            argumentsStringBuilder.append("--spelling " + namespace.charAt(0) + " ");
            return this;
        }

        /**
         * read a dictionary for additional features (arg either 'x:file' or just 'file') <p>
         *
         * @param file dictionary path
         * @return builder
         */
        public Builder dictionary(final Path file) {
            argumentsStringBuilder.append("--dictionary " + file + " ");
            return this;
        }

        /**
         * look in this directory for dictionaries; defaults to current directory or env{PATH} <p>
         *
         * @param dir dictionaries directory path
         * @return builder
         */
        public Builder dictionaryPath(final Path dir) {
            argumentsStringBuilder.append("--dictionary_path " + dir + " ");
            return this;
        }

        /**
         * Create feature interactions of any level between namespaces. <p>
         *
         * @param namespaces namspaces
         * @return builder
         */
        public Builder interactions(final String ... namespaces) {
            final StringBuilder namespacesChars = new StringBuilder();
            for (final String namespace : namespaces) {
                namespacesChars.append(namespace.charAt(0));
            }
            argumentsStringBuilder.append("--interactions " +
                    namespacesChars + " ");
            return this;
        }

        /**
         * Use permutations instead of combinations for feature interactions of same namespace. <p>
         *
         * @return builder
         */
        public Builder permutations() {
            argumentsStringBuilder.append("--permutations ");
            return this;
        }

        /**
         * Don't remove interactions with duplicate combinations of namespaces. <p>
         * For ex. this is a duplicate: '-q ab -q ba' and a lot more in '-q ::'. <p>
         *
         * @return builder
         */
        public Builder leaveDuplicateInteractions() {
            argumentsStringBuilder.append("--leave_duplicate_interactions ");
            return this;
        }

        /**
         * Create and use quadratic features <p>
         *
         * @param firstNameSpace  namespace or ":" for any
         * @param secondNamespace  namespace or ":" for any
         * @return builder
         */
        @Override
        public Builder quadratic(final String firstNameSpace, final String secondNamespace) {
            argumentsStringBuilder.append("--quadratic " + firstNameSpace.charAt(0) + "" + secondNamespace.charAt(0) + " ");
            return this;
        }

        /**
         * Create and use cubic features <p>
         *
         * @param firstNameSpace   namespace or ":" for any
         * @param secondNamespace  namespace or ":" for any
         * @param thirdNamespace   namespace or ":" for any
         * @return builder
         */
        @Override
        public Builder cubic(final String firstNameSpace, final String secondNamespace, final String thirdNamespace) {
            argumentsStringBuilder.append("--cubic " + firstNameSpace.charAt(0) +
                    "" + secondNamespace.charAt(0) +
                    "" + thirdNamespace.charAt(0) + " ");
            return this;
        }

        /**
         * Ignore label information and just test <p>
         *
         * @return builder
         */
        @Override
        public Builder testonly() {
            argumentsStringBuilder.append("--testonly ");
            return this;
        }

        /**
         * holdout period for test only, default 10 <p>
         *
         * @param holdout holdout period size
         * @return builder
         */
        public Builder holdoutPeriod(final int holdout) {
            argumentsStringBuilder.append("--holdout_period " + holdout + " ");
            return this;
        }

        /**
         * holdout after n training examples, default off (disables holdoutPeriod) <p>
         *
         * @param n number of examples in hodout
         * @return builder
         */
        public Builder holdoutAfter(final int n) {
            argumentsStringBuilder.append("--holdout_after " + n + " ");
            return this;
        }

        /**
         * Specify the number of passes tolerated when holdout loss doesn't decrease before early termination, default is 3 <p>
         *
         * @param passes number of passes
         * @return builder
         */
        public Builder earlyTerminate(final int passes) {
            argumentsStringBuilder.append("--early_terminate " + passes + " ");
            return this;
        }

        /**
         * Number of Training Passes <p>
         *
         * @param passes number of passes
         * @return builder
         */
        public Builder passes(final int passes) {
            argumentsStringBuilder.append("--passes " + passes + " ");
            return cache();
        }

        /**
         * initial number of examples per pass <p>
         *
         * @param examples number of examples per pass
         * @return builder
         */
        public Builder initialPassLength(final int examples) {
            argumentsStringBuilder.append("--initial_pass_length " + examples + " ");
            return this;
        }

        /**
         * number of examples to parse <p>
         *
         * @param examples number of examples to parse
         * @return builder
         */
        public Builder examples(final int examples) {
            argumentsStringBuilder.append("--examples " + examples + " ");
            return this;
        }

        /**
         * Smallest prediction to output <p>
         *
         * @param min minimum prediction, including
         * @return builder
         */
        @Override
        public Builder minPrediction(final double min) {
            argumentsStringBuilder.append("--min_prediction " + min + " ");
            return this;
        }

        /**
         * Largest prediction to output <p>
         *
         * @param max maximum prediction, including
         * @return builder
         */
        @Override
        public Builder maxPrediction(final double max) {
            argumentsStringBuilder.append("--max_prediction " + max + " ");
            return this;
        }

        /**
         * turn this on to disregard order in which features have been defined. This will lead to smaller cache sizes <p>
         *
         * @return builder
         */
        public Builder sortFeatures() {
            argumentsStringBuilder.append("--sort_features ");
            return this;
        }

        /**
         * Specify the loss function to be used, uses squared by default. Currently available ones are <p>
         * squared, classic, hinge, logistic, quantile and poisson. (=squared) <p>
         *
         * @param loss loss function
         * @return builder
         */
        @Override
        public Builder lossFunction(final Loss loss) {
            argumentsStringBuilder.append("--loss_function " + loss + " ");
            return this;
        }

        /**
         * Parameter \tau associated with Quantile loss. Defaults to 0.5 (=0.5) <p>
         *
         * @param tau tau parameter
         * @return builder
         */
        @Override
        public Builder quantileTau(final double tau) {
            argumentsStringBuilder.append("--quantile_tau " + tau + " ");
            return this;
        }

        /**
         * l_1 lambda <p>
         *
         * @param l1 l1 regularization. Must be not negative
         * @return builder
         */
        @Override
        public Builder l1(final double l1) {
            argumentsStringBuilder.append("--l1 " + l1 + " ");
            return this;
        }

        /**
         * l_2 lambda <p>
         *
         * @param l2 l2 regularization. Must be not negative
         * @return builder
         */
        @Override
        public Builder l2(final double l2) {
            argumentsStringBuilder.append("--l2 " + l2 + " ");
            return this;
        }

        /**
         * use names for labels (multiclass, etc.) rather than integers, argument specified all possible labels, comma-sep, <p>
         * eg "--namedLabels Noun,Verb,Adj,Punc" <p>
         *
         * @param labels labels
         * @return builder
         */
        public Builder namedLabels(final String... labels) {
            argumentsStringBuilder.append("--named_labels " +
                    Joiner.on(",").join(Arrays.asList(labels)) + " ");
            return this;
        }

        /**
         * Final regressor <p>
         *
         * @param regressor path where to store final regressor
         * @return builder
         */
        @Override
        public Builder finalRegressor(final Path regressor) {
            argumentsStringBuilder.append("--final_regressor " + regressor + " ");
            return this;
        }

        /**
         * Output human-readable final regressor with numeric features <p>
         *
         * @param model path where to store readable model
         * @return builder
         */
        @Override
        public Builder readableModel(final Path model) {
            argumentsStringBuilder.append("--readable_model " + model + " ");
            return this;
        }

        /**
         * save extra state so learning can be resumed later with new data <p>
         *
         * @return builder
         */
        public Builder saveResume() {
            argumentsStringBuilder.append("--save_resume ");
            return this;
        }

        /**
         * Save the model after every pass over data <p>
         *
         * @return builder
         */
        public Builder savePerPass() {
            argumentsStringBuilder.append("--save_per_pass ");
            return this;
        }

        /**
         * Per feature regularization output file <p>
         *
         * @param regularizationFile path where to store regularization output file
         * @return builder
         */
        public Builder outputFeatureRegularizerBinary(final Path regularizationFile) {
            argumentsStringBuilder.append("--output_feature_regularizer_binary " + regularizationFile + " ");
            return this;
        }

        /**
         * Per feature regularization output file, in text <p>
         *
         * @param regularizationFile path where to store regularization output file
         * @return builder
         */
        public Builder outputFeatureRegularizerText(final Path regularizationFile) {
            argumentsStringBuilder.append("--output_feature_regularizer_text " + regularizationFile + " ");
            return this;
        }

        /**
         * User supplied ID embedded into the final regressor <p>
         *
         * @param id model id
         * @return builder
         */
        public Builder id(final String id) {
            argumentsStringBuilder.append("--id " + id + " ");
            return this;
        }

        /**
         * stores feature names and their regressor values. <p>
         * Same dataset must be used for both regressor training and this mode. <p>
         *
         * @param regressor path where to read regressor for audit
         * @return builder
         */
        public Builder auditRegressor(final Path regressor) {
            argumentsStringBuilder.append("--audit_regressor " + regressor + " ");
            return this;
        }

        /**
         * k-way bootstrap by online importance resampling <p>
         *
         * @param k number of bootstrap resamples
         * @return builder
         */
        public Builder bootstrap(final int k) {
            argumentsStringBuilder.append("--bootstrap " + k + " ");
            return this;
        }

        /**
         * Use learning to search, argument=maximum action id or 0 for LDF <p>
         *
         * @param maxActionID max action id
         * @return builder
         */
        public Builder search(final int maxActionID) {
            argumentsStringBuilder.append("--search " + maxActionID + " ");
            return this;
        }

        /**
         * use experience replay at a specified level <p>
         * [b=classification/regression, m=multiclass, c=cost sensitive] with specified buffer size <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder replayC(final String arg) {
            argumentsStringBuilder.append("--replay_c " + arg + " ");
            return this;
        }

        /**
         * Convert multiclass on &lt;k&gt; classes into a contextual bandit problem <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder cbify(final int k) {
            argumentsStringBuilder.append("--cbify " + k + " ");
            return this;
        }

        /**
         * Online explore-exploit for a contextual bandit problem with multiline action dependent features <p>
         *
         * @return builder
         */
        public Builder cbExploreAdf() {
            argumentsStringBuilder.append("--cb_explore_adf ");
            return this;
        }

        /**
         * Online explore-exploit for a &lt;k&gt; action contextual bandit problem <p>
         *
         * @param k number of actions
         * @return builder
         */
        public Builder cbExplore(final int k) {
            argumentsStringBuilder.append("--cb_explore " + k + " ");
            return this;
        }

        /**
         * Evaluate features as a policies <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder multiworldTest(final String arg) {
            argumentsStringBuilder.append("--multiworld_test " + arg + " ");
            return this;
        }

        /**
         * Do Contextual Bandit learning with multiline action dependent features. <p>
         *
         * @return builder
         */
        public Builder cbAdf() {
            argumentsStringBuilder.append("--cb_adf ");
            return this;
        }

        /**
         * Use contextual bandit learning with &lt;k&gt; costs <p>
         *
         * @param k number of costs
         * @return builder
         */
        public Builder cb(final int k) {
            argumentsStringBuilder.append("--cb " + k + " ");
            return this;
        }

        /**
         * Use one-against-all multiclass learning with label dependent features.  Specify singleline or multiline. <p>
         *
         * @param ldf ldf
         * @return builder
         */
        public Builder csoaaLdf(final LDF ldf) {
            argumentsStringBuilder.append("--csoaa_ldf " + ldf + " ");
            return this;
        }

        /**
         * Use weighted all-pairs multiclass learning with label dependent features.   Specify singleline or multiline. <p>
         *
         * @param ldf ldf
         * @return builder
         */
        public Builder wapLdf(final LDF ldf) {
            argumentsStringBuilder.append("--wap_ldf " + ldf + " ");
            return this;
        }

        /**
         * Put weights on feature products from namespaces &lt;n1&gt; and &lt;n2&gt; <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder interact(final String arg) {
            argumentsStringBuilder.append("--interact " + arg + " ");
            return this;
        }

        /**
         * One-against-all multiclass with &lt;k&gt; costs <p>
         *
         * @param k number of costs
         * @return builder
         */
        public Builder csoaa(final int k) {
            argumentsStringBuilder.append("--csoaa " + k + " ");
            return this;
        }

        /**
         * One-against-all multilabel with &lt;k&gt; labels <p>
         *
         * @param k number of labels
         * @return builder
         */
        public Builder multilabelOaa(final int k) {
            argumentsStringBuilder.append("--multilabel_oaa " + k + " ");
            return this;
        }

        /**
         * Use online tree for multiclass <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder recallTree(final int k) {
            argumentsStringBuilder.append("--recall_tree " + k + " ");
            return this;
        }

        /**
         * Use online tree for multiclass <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder logMulti(final int k) {
            argumentsStringBuilder.append("--log_multi " + k + " ");
            return this;
        }

        /**
         * Error correcting tournament with &lt;k&gt; labels <p>
         *
         * @param k number of labels
         * @return builder
         */
        public Builder ect(final int k) {
            argumentsStringBuilder.append("--ect " + k + " ");
            return this;
        }

        /**
         * Online boosting with &lt;N&gt; weak learners <p>
         *
         * @param n number of weak learners
         * @return builder
         */
        public Builder boosting(final int n) {
            argumentsStringBuilder.append("--boosting " + n + " ");
            return this;
        }

        /**
         * One-against-all multiclass with &lt;k&gt; labels <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder oaa(final int k) {
            argumentsStringBuilder.append("--oaa " + k + " ");
            return this;
        }

        /**
         * top k recommendation <p>
         *
         * @param k number of top recomendations
         * @return builder
         */
        public Builder top(final int k) {
            argumentsStringBuilder.append("--top " + k + " ");
            return this;
        }

        /**
         * use experience replay at a specified level <p>
         * [b=classification/regression, m=multiclass, c=cost sensitive] with specified buffer size <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder replayM(final String arg) {
            argumentsStringBuilder.append("--replay_m " + arg + " ");
            return this;
        }

        /**
         * report loss as binary classification on -1,1 <p>
         *
         * @return builder
         */
        public Builder binary() {
            argumentsStringBuilder.append("--binary ");
            return this;
        }

        /**
         * Specify the link function: identity, logistic, glf1 or poisson  (=identity) <p>
         *
         * @param link link function
         * @return builder
         */
        @Override
        public Builder link(final Link link) {
            argumentsStringBuilder.append("--link " + link + " ");
            return this;
        }

        /**
         * use stagewise polynomial feature learning <p>
         *
         * @return builder
         */
        public Builder stagePoly() {
            argumentsStringBuilder.append("--stage_poly ");
            return this;
        }

        /**
         * use low rank quadratic features with field aware weights <p>
         *
         * @param firstNamespace  - namespace or ":" for any
         * @param secondNamespace - namespace or ":" for any
         * @param k factorized matrices width
         * @return builder
         */
        @Override
        public Builder lrqfa(final String firstNamespace, final String secondNamespace, final int k) {
            argumentsStringBuilder.append("--lrqfa " + firstNamespace.charAt(0) + "" +
                    secondNamespace.charAt(0) + "" + k + " ");
            return this;
        }

        /**
         * use low rank quadratic features <p>
         *
         * @param firstNamespace  - namespace or ":" for any
         * @param secondNamespace - namespace or ":" for any
         * @param k factorized matrices width
         * @return builder
         */
        public Builder lrq(final String firstNamespace, final String secondNamespace, final int k) {
            argumentsStringBuilder.append("--lrq " + firstNamespace.charAt(0) + "" +
                    secondNamespace.charAt(0) + "" + k + " ");
            return this;
        }


        /**
         * use dropout training for low rank quadratic features <p>
         *
         * @return builder
         */
        public Builder lrqdropout() {
            argumentsStringBuilder.append("--lrqdropout ");
            return this;
        }

        /**
         * create link function with polynomial d <p>
         *
         * @param d polynomial degree
         * @return builder
         */
        public Builder autolink(final int d) {
            argumentsStringBuilder.append("--autolink " + d + " ");
            return this;
        }

        /**
         * rank for reduction-based matrix factorization <p>
         *
         * @param rank rank
         * @return builder
         */
        public Builder newMf(final int rank) {
            argumentsStringBuilder.append("--new_mf " + rank + " ");
            return this;
        }

        /**
         * Sigmoidal feedforward network with &lt;k&gt; hidden units <p>
         *
         * @param units number of hidden units
         * @return builder
         */
        public Builder nn(final int units) {
            argumentsStringBuilder.append("--nn " + units + " ");
            return this;
        }

        /**
         * Confidence after training <p>
         *
         * @return builder
         */
        public Builder confidenceAfterTraining() {
            argumentsStringBuilder.append("--confidence_after_training ");
            return this;
        }

        /**
         * Get confidence for binary predictions <p>
         *
         * @return builder
         */
        public Builder confidence() {
            argumentsStringBuilder.append("--confidence ");
            return this;
        }

        /**
         * enable active learning with cover <p>
         *
         * @return builder
         */
        public Builder activeCover() {
            argumentsStringBuilder.append("--active_cover ");
            return this;
        }

        /**
         * enable active learning <p>
         *
         * @return builder
         */
        public Builder active() {
            argumentsStringBuilder.append("--active ");
            return this;
        }

        /**
         * use experience replay at a specified level <p>
         * [b=classification/regression, m=multiclass, c=cost sensitive] with specified buffer size <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder replayB(final String arg) {
            argumentsStringBuilder.append("--replay_b " + arg + " ");
            return this;
        }

        /**
         * Online Newton with Oja's Sketch <p>
         *
         * @return builder
         */
        public Builder ojaNewton() {
            argumentsStringBuilder.append("--OjaNewton ");
            return this;
        }

        /**
         * use bfgs optimization <p>
         *
         * @return builder
         */
        public Builder bfgs() {
            argumentsStringBuilder.append("--bfgs ");
            return this;
        }

        /**
         * use conjugate gradient based optimization <p>
         *
         * @return builder
         */
        public Builder conjugateGradient() {
            argumentsStringBuilder.append("--conjugate_gradient ");
            return this;
        }

        /**
         * Run lda with &lt;int&gt; topics <p>
         *
         * @param topics number of lda topics
         * @return builder
         */
        public Builder lda(final int topics) {
            argumentsStringBuilder.append("--lda " + topics + " ");
            return this;
        }

        /**
         * do no learning <p>
         *
         * @return builder
         */
        public Builder noop() {
            argumentsStringBuilder.append("--noop ");
            return this;
        }

        /**
         * rank for matrix factorization. <p>
         *
         * @param rank rank for matrix factorization
         * @return builder
         */
        public Builder rank(final int rank) {
            argumentsStringBuilder.append("--rank " + rank + " ");
            return this;
        }

        /**
         * Streaming Stochastic Variance Reduced Gradient <p>
         *
         * @return builder
         */
        public Builder svrg() {
            argumentsStringBuilder.append("--svrg ");
            return this;
        }

        /**
         * FTRL: Follow the Proximal Regularized Leader <p>
         *
         * @return builder
         */
        @Override
        public Builder ftrl() {
            argumentsStringBuilder.append("--ftrl ");
            return this;
        }

        /**
         * FTRL: Parameter-free Stochastic Learning <p>
         *
         * @return builder
         */
        public Builder pistol() {
            argumentsStringBuilder.append("--pistol ");
            return this;
        }

        /**
         * kernel svm <p>
         *
         * @return builder
         */
        public Builder ksvm() {
            argumentsStringBuilder.append("--ksvm ");
            return this;
        }

        /**
         * use regular stochastic gradient descent update. <p>
         *
         * @return builder
         */
        @Override
        public Builder sgd() {
            argumentsStringBuilder.append("--sgd ");
            return this;
        }

        /**
         * use adaptive, individual learning rates. <p>
         *
         * @return builder
         */
        @Override
        public Builder adaptive() {
            argumentsStringBuilder.append("--adaptive ");
            return this;
        }

        /**
         * use safe/importance aware updates. <p>
         *
         * @return builder
         */
        @Override
        public Builder invariant() {
            argumentsStringBuilder.append("--invariant ");
            return this;
        }

        /**
         * use per feature normalized updates <p>
         *
         * @return builder
         */
        @Override
        public Builder normalized() {
            argumentsStringBuilder.append("--normalized ");
            return this;
        }

        /**
         * use per feature normalized updates (=0) <p>
         *
         * @param l2 l2 regularization. Must be not negative
         * @return builder
         */
        public Builder sparseL2(final double l2) {
            argumentsStringBuilder.append("--sparse_l2 " + l2 + " ");
            return this;
        }

        /**
         * Use a cache.  The default is &lt;data&gt;.cache <p>
         *
         * @return builder
         */
        public Builder cache() {
            argumentsStringBuilder.append("--cache ");
            return this;
        }

        /**
         * The location(s) of cacheFile. <p>
         *
         * @param cacheFile path to cache file
         * @return builder
         */
        public Builder cacheFile(final Path cacheFile) {
            argumentsStringBuilder.append("--cache_file " + cacheFile + " ");
            return this;
        }

        /**
         * do not reuse existing cache: create a new one always <p>
         *
         * @return builder
         */
        public Builder killCache() {
            argumentsStringBuilder.append("--kill_cache ");
            return this;
        }

        /**
         * use gzip format whenever possible. If a cache file is being created, <p>
         * this option creates a compressed cache file. <p>
         * A mixture of raw-text and compressed inputs are supported with autodetection. <p>
         *
         * @return builder
         */
        public Builder compressed() {
            argumentsStringBuilder.append("--compressed ");
            return this;
        }

        /**
         * Add vowpal wabit argument <p>
         *
         * @param argumentLine parameter line
         * @return builder
         */
        public Builder parameter(final String argumentLine) {
            argumentsStringBuilder.append(argumentLine).append(' ');
            return this;
        }

        /**
         * Get command option will be passes to VWLearner <p>
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
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWFloatLearner) VWLearners.create(getCommand());
        }

        /**
         *
         * @return VWIntLearner object
         */
        public VWIntLearner buildIntLearner() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWIntLearner) VWLearners.create(getCommand());
        }

        /**
         *
         * @return VWFloatArrayLearner object
         */
        public VWFloatArrayLearner buildFloatArrayLearner() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWFloatArrayLearner) VWLearners.create(getCommand());
        }

        /**
         *
         * @return VWIntArrayLearner object
         */
        public VWIntArrayLearner buildIntArrayLearner() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWIntArrayLearner) VWLearners.create(getCommand());
        }
    }
}