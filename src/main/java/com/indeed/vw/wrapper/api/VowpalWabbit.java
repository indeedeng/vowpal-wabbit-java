package com.indeed.vw.wrapper.api;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.indeed.vw.wrapper.api.parameters.Link;
import com.indeed.vw.wrapper.api.parameters.Loss;
import com.indeed.vw.wrapper.api.parameters.SGDVowpalWabbitBuilder;
import com.indeed.vw.wrapper.learner.VWFloatLearner;
import com.indeed.vw.wrapper.learner.VWIntLearner;
import com.indeed.vw.wrapper.learner.VWLearner;
import com.indeed.vw.wrapper.learner.VWLearners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains factory methods for VWLearner builder object. <p>
 * <p>
 * For better parameters documentation read: <a href="https://github.com/JohnLangford/vowpal_wabbit/wiki/Command-line-arguments">https://github.com/JohnLangford/vowpal_wabbit/wiki/Command-line-arguments</a> <p>
 */
public class VowpalWabbit {
    /**
     * Pass this constant as namespace parameter if you want to set some feature engineering for all namespaces. <p> <p>
     */
    public static final String ANY_NAMESPACE = ":";
    private static final Logger logger = LoggerFactory.getLogger(VowpalWabbit.class);

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
     */
    public static class Builder implements SGDVowpalWabbitBuilder {
        private Builder() {
        }

        private final List<String> argumentsStrings = new ArrayList<>(Arrays.asList("vw"));
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
            addParameter("--random_seed", String.valueOf(seed));
            return this;
        }

        /**
         * size of example ring buffer <p>
         *
         * @param ringSize size of example ring
         * @return builder
         */
        public Builder ringSize(final int ringSize) {
            addParameter("--ring_size", String.valueOf(ringSize));
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
            addParameter("--learning_rate", String.valueOf(learningRate));
            return this;
        }

        /**
         * t power value <p>
         *
         * @param powerT t power value
         * @return builder
         */
        public Builder powerT(final double powerT) {
            addParameter("--power_t", String.valueOf(powerT));
            return this;
        }

        /**
         * Set Decay factor for learning_rate between passes <p>
         *
         * @param decay exponential decay
         * @return builder
         */
        public Builder decayLearningRate(final double decay) {
            addParameter("--decay_learning_rate", String.valueOf(decay));
            return this;
        }

        /**
         * initial t value <p>
         *
         * @param initialT initial t value
         * @return builder
         */
        public Builder initialT(final double initialT) {
            addParameter("--initial_t", String.valueOf(initialT));
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
            addParameter("--feature_mask", featureMask.toString());
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
            addParameter("--initial_regressor", initialRegressor.toString());
            return this;
        }

        /**
         * Set all weights to an initial value of arg. <p>
         *
         * @param weight initial weight value
         * @return builder
         */
        public Builder initialWeight(final double weight) {
            addParameter("--initial_weight", String.valueOf(weight));
            return this;
        }

        /**
         * make initial weights random <p>
         *
         * @param arg random maximum
         * @return builder
         */
        public Builder randomWeights(final double arg) {
            addParameter("--random_weights", String.valueOf(arg));
            return this;
        }

        /**
         * Per feature regularization input file <p>
         *
         * @param regularizationPath path to regularization input file
         * @return builder
         */
        public Builder inputFeatureRegularizer(final Path regularizationPath) {
            addParameter("--input_feature_regularizer", regularizationPath.toString());
            return this;
        }

        /**
         * how to hash the features. Available options: strings, all <p>
         *
         * @param hash hash strategy
         * @return builder
         */
        public Builder hash(final Hash hash) {
            addParameter("--hash", hash.toString());
            return this;
        }

        /**
         * ignore namespace  &lt;arg&gt; <p>
         *
         * @param namespace namespace name
         * @return builder
         */
        public Builder ignore(final String namespace) {
            addParameter("--ignore", String.valueOf(namespace.charAt(0)));
            return this;
        }

        /**
         * keep namespace &lt;arg&gt; <p>
         *
         * @param namespace namespace name
         * @return builder
         */
        public Builder keep(final String namespace) {
            addParameter("--keep", String.valueOf(namespace.charAt(0)));
            return this;
        }

        /**
         * redefine namespaces beginning with characters of string S as namespace N. <p>
         * &lt;arg&gt; shall be in form 'N:=S' where := is operator. Empty N or S are treated as default namespace. <p>
         * Use ':' as a wildcard in S. <p>
         *
         * @param newNamespace new namespace name
         * @param namespaces   old namespaces names
         * @return builder
         */
        public Builder redefine(final String newNamespace, final String... namespaces) {
            final StringBuilder oldNamespaces = new StringBuilder();
            for (final String namespace : namespaces) {
                oldNamespaces.append(namespace.charAt(0));
            }
            addParameter("--redefine", newNamespace + ":=" + oldNamespaces);
            return this;
        }

        /**
         * number of bits in the feature table. <p>
         * <p>
         * It mean feature table will have 2^bitsNum size <p>
         *
         * @param bitsNum bitsNum number of bits in hash feature table.
         * @return builder
         */
        @Override
        public Builder bitPrecision(final int bitsNum) {
            addParameter("--bit_precision", String.valueOf(bitsNum));
            return this;
        }

        /**
         * Don't add a constant feature <p>
         *
         * @return builder
         */
        @Override
        public Builder noconstant() {
            addFlag("--noconstant");
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
            addParameter("--constant", String.valueOf(initialValue));
            return this;
        }

        /**
         * Generate N grams. To generate N grams for a single namespace 'foo', arg should be fN. <p>
         *
         * @param namespace namespace name, or ':' for any namespaces
         * @param n         size of n-gram.
         * @return builder
         */
        @Override
        public Builder ngram(final String namespace, final int n) {
            if (namespace.equals(ANY_NAMESPACE)) {
                addParameter("--ngram", String.valueOf(n));
                return this;
            }
            addParameter("--ngram", namespace.charAt(0) + "" + n);
            return this;
        }

        /**
         * Generate skips in N grams. This in conjunction with the ngram tag can be used to generate generalized n-skip-k-gram. <p>
         * To generate n-skips for a single namespace 'foo', arg should be fN. <p>
         *
         * @param namespace namespace name, or ':' for any namespaces
         * @param n         size of skips n-gram.
         * @return builder
         */
        @Override
        public Builder skips(final String namespace, final int n) {
            if (namespace.equals(ANY_NAMESPACE)) {
                addParameter("--skips", String.valueOf(n));
                return this;
            }
            addParameter("--skips", namespace.charAt(0) + "" + n);
            return this;
        }

        /**
         * limit to N features. To apply to a single namespace 'foo', arg should be fN <p>
         *
         * @param n number of features
         * @return builder
         */
        public Builder featureLimit(final int n) {
            addParameter("--feature_limit", String.valueOf(n));
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
            addParameter("--affix", arg);
            return this;
        }

        /**
         * compute spelling features for a give namespace (use '_' for default namespace) <p>
         *
         * @param namespace namespace
         * @return builder
         */
        public Builder spelling(final String namespace) {
            addParameter("--spelling", String.valueOf(namespace.charAt(0)));
            return this;
        }

        /**
         * read a dictionary for additional features (arg either 'x:file' or just 'file') <p>
         *
         * @param file dictionary path
         * @return builder
         */
        public Builder dictionary(final Path file) {
            addParameter("--dictionary", file.toString());
            return this;
        }

        /**
         * look in this directory for dictionaries; defaults to current directory or env{PATH} <p>
         *
         * @param dir dictionaries directory path
         * @return builder
         */
        public Builder dictionaryPath(final Path dir) {
            addParameter("--dictionary_path", dir.toString());
            return this;
        }

        /**
         * Create feature interactions of any level between namespaces. <p>
         *
         * @param namespaces namspaces
         * @return builder
         */
        public Builder interactions(final String... namespaces) {
            final StringBuilder namespacesChars = new StringBuilder();
            for (final String namespace : namespaces) {
                namespacesChars.append(namespace.charAt(0));
            }
            addParameter("--interactions", namespacesChars.toString());
            return this;
        }

        /**
         * Use permutations instead of combinations for feature interactions of same namespace. <p>
         *
         * @return builder
         */
        public Builder permutations() {
            addFlag("--permutations");
            return this;
        }

        /**
         * Don't remove interactions with duplicate combinations of namespaces. <p>
         * For ex. this is a duplicate: '-q ab -q ba' and a lot more in '-q ::'. <p>
         *
         * @return builder
         */
        public Builder leaveDuplicateInteractions() {
            addFlag("--leave_duplicate_interactions");
            return this;
        }

        /**
         * Create and use quadratic features <p>
         *
         * @param firstNameSpace  namespace or ":" for any
         * @param secondNamespace namespace or ":" for any
         * @return builder
         */
        @Override
        public Builder quadratic(final String firstNameSpace, final String secondNamespace) {
            addParameter("--quadratic", firstNameSpace.charAt(0) + "" + secondNamespace.charAt(0));
            return this;
        }

        /**
         * Create and use cubic features <p>
         *
         * @param firstNameSpace  namespace or ":" for any
         * @param secondNamespace namespace or ":" for any
         * @param thirdNamespace  namespace or ":" for any
         * @return builder
         */
        @Override
        public Builder cubic(final String firstNameSpace, final String secondNamespace, final String thirdNamespace) {
            addParameter("--cubic", firstNameSpace.charAt(0) + "" + secondNamespace.charAt(0) + "" + thirdNamespace.charAt(0));
            return this;
        }

        /**
         * Ignore label information and just test <p>
         *
         * @return builder
         */
        @Override
        public Builder testonly() {
            addFlag("--testonly");
            return this;
        }

        /**
         * holdout period for test only, default 10 <p>
         *
         * @param holdout holdout period size
         * @return builder
         */
        public Builder holdoutPeriod(final int holdout) {
            addParameter("--holdout_period", String.valueOf(holdout));
            return this;
        }

        /**
         * holdout after n training examples, default off (disables holdoutPeriod) <p>
         *
         * @param n number of examples in hodout
         * @return builder
         */
        public Builder holdoutAfter(final int n) {
            addParameter("--holdout_after", String.valueOf(n));
            return this;
        }

        /**
         * Specify the number of passes tolerated when holdout loss doesn't decrease before early termination, default is 3 <p>
         *
         * @param passes number of passes
         * @return builder
         */
        public Builder earlyTerminate(final int passes) {
            addParameter("--early_terminate", String.valueOf(passes));
            return this;
        }

        /**
         * Number of Training Passes <p>
         *
         * @param passes number of passes
         * @return builder
         */
        public Builder passes(final int passes) {
            addParameter("--passes", String.valueOf(passes));
            return cache();
        }

        /**
         * initial number of examples per pass <p>
         *
         * @param examples number of examples per pass
         * @return builder
         */
        public Builder initialPassLength(final int examples) {
            addParameter("--initial_pass_length", String.valueOf(examples));
            return this;
        }

        /**
         * number of examples to parse <p>
         *
         * @param examples number of examples to parse
         * @return builder
         */
        public Builder examples(final int examples) {
            addParameter("--examples", String.valueOf(examples));
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
            addParameter("--min_prediction", String.valueOf(min));
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
            addParameter("--max_prediction", String.valueOf(max));
            return this;
        }

        private void addParameter(String parameter, String value) {
            addFlag(parameter);
            addFlag(value);
        }

        /**
         * turn this on to disregard order in which features have been defined. This will lead to smaller cache sizes <p>
         *
         * @return builder
         */
        public Builder sortFeatures() {
            addFlag("--sort_features");
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
            addParameter("--loss_function", loss.toString());
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
            addParameter("--quantile_tau", String.valueOf(tau));
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
            addParameter("--l1", String.valueOf(l1));
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
            addParameter("--l2", String.valueOf(l2));
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
            addParameter("--named_labels", Joiner.on(",").join(Arrays.asList(labels)));
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
            addParameter("--final_regressor", regressor.toString());
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
            addParameter("--readable_model", model.toString());
            return this;
        }

        /**
         * Output human-readable final regressor with feature names <p>
         *
         * @param model path where to store readable model
         * @return builder
         */
        @Override
        public SGDVowpalWabbitBuilder invertHash(final Path model) {
            addParameter("--invert_hash",  model.toString());
            return this;
        }

        /**
         * save extra state so learning can be resumed later with new data <p>
         *
         * @return builder
         */
        public Builder saveResume() {
            addFlag("--save_resume");
            return this;
        }

        /**
         * Save the model after every pass over data <p>
         *
         * @return builder
         */
        public Builder savePerPass() {
            addFlag("--save_per_pass");
            return this;
        }

        /**
         * Per feature regularization output file <p>
         *
         * @param regularizationFile path where to store regularization output file
         * @return builder
         */
        public Builder outputFeatureRegularizerBinary(final Path regularizationFile) {
            addParameter("--output_feature_regularizer_binary", regularizationFile.toString());
            return this;
        }

        /**
         * Per feature regularization output file, in text <p>
         *
         * @param regularizationFile path where to store regularization output file
         * @return builder
         */
        public Builder outputFeatureRegularizerText(final Path regularizationFile) {
            addParameter("--output_feature_regularizer_text", regularizationFile.toString());
            return this;
        }

        /**
         * User supplied ID embedded into the final regressor <p>
         *
         * @param id model id
         * @return builder
         */
        public Builder id(final String id) {
            addParameter("--id", id);
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
            addParameter("--audit_regressor", regressor.toString());
            return this;
        }

        /**
         * k-way bootstrap by online importance resampling <p>
         *
         * @param k number of bootstrap resamples
         * @return builder
         */
        public Builder bootstrap(final int k) {
            addParameter("--bootstrap", String.valueOf(k));
            return this;
        }

        /**
         * Use learning to search, argument=maximum action id or 0 for LDF <p>
         *
         * @param maxActionID max action id
         * @return builder
         */
        public Builder search(final int maxActionID) {
            addParameter("--search", String.valueOf(maxActionID));
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
            addParameter("--replay_c", arg);
            return this;
        }

        /**
         * Convert multiclass on &lt;k&gt; classes into a contextual bandit problem <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder cbify(final int k) {
            addParameter("--cbify", String.valueOf(k));
            return this;
        }

        /**
         * Online explore-exploit for a contextual bandit problem with multiline action dependent features <p>
         *
         * @return builder
         */
        public Builder cbExploreAdf() {
            addFlag("--cb_explore_adf");
            return this;
        }

        /**
         * Online explore-exploit for a &lt;k&gt; action contextual bandit problem <p>
         *
         * @param k number of actions
         * @return builder
         */
        public Builder cbExplore(final int k) {
            addParameter("--cb_explore", String.valueOf(k));
            return this;
        }

        /**
         * Evaluate features as a policies <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder multiworldTest(final String arg) {
            addParameter("--multiworld_test", arg);
            return this;
        }

        /**
         * Do Contextual Bandit learning with multiline action dependent features. <p>
         *
         * @return builder
         */
        public Builder cbAdf() {
            addFlag("--cb_adf");
            return this;
        }

        /**
         * Use contextual bandit learning with &lt;k&gt; costs <p>
         *
         * @param k number of costs
         * @return builder
         */
        public Builder cb(final int k) {
            addParameter("--cb", String.valueOf(k));
            return this;
        }

        /**
         * Use one-against-all multiclass learning with label dependent features.  Specify singleline or multiline. <p>
         *
         * @param ldf ldf
         * @return builder
         */
        public Builder csoaaLdf(final LDF ldf) {
            addParameter("--csoaa_ldf", ldf.toString());
            return this;
        }

        /**
         * Use weighted all-pairs multiclass learning with label dependent features.   Specify singleline or multiline. <p>
         *
         * @param ldf ldf
         * @return builder
         */
        public Builder wapLdf(final LDF ldf) {
            addParameter("--wap_ldf", ldf.toString());
            return this;
        }

        /**
         * Put weights on feature products from namespaces &lt;n1&gt; and &lt;n2&gt; <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder interact(final String arg) {
            addParameter("--interact", arg);
            return this;
        }

        /**
         * One-against-all multiclass with &lt;k&gt; costs <p>
         *
         * @param k number of costs
         * @return builder
         */
        public Builder csoaa(final int k) {
            addParameter("--csoaa", String.valueOf(k));
            return this;
        }

        /**
         * One-against-all multilabel with &lt;k&gt; labels <p>
         *
         * @param k number of labels
         * @return builder
         */
        public Builder multilabelOaa(final int k) {
            addParameter("--multilabel_oaa", String.valueOf(k));
            return this;
        }

        /**
         * Use online tree for multiclass <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder recallTree(final int k) {
            addParameter("--recall_tree", String.valueOf(k));
            return this;
        }

        /**
         * Use online tree for multiclass <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder logMulti(final int k) {
            addParameter("--log_multi", String.valueOf(k));
            return this;
        }

        /**
         * Error correcting tournament with &lt;k&gt; labels <p>
         *
         * @param k number of labels
         * @return builder
         */
        public Builder ect(final int k) {
            addParameter("--ect", String.valueOf(k));
            return this;
        }

        /**
         * Online boosting with &lt;N&gt; weak learners <p>
         *
         * @param n number of weak learners
         * @return builder
         */
        public Builder boosting(final int n) {
            addParameter("--boosting", String.valueOf(n));
            return this;
        }

        /**
         * One-against-all multiclass with &lt;k&gt; labels <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder oaa(final int k) {
            addParameter("--oaa", String.valueOf(k));
            return this;
        }

        /**
         * top k recommendation <p>
         *
         * @param k number of top recomendations
         * @return builder
         */
        public Builder top(final int k) {
            addParameter("--top", String.valueOf(k));
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
            addParameter("--replay_m", arg);
            return this;
        }

        /**
         * report loss as binary classification on -1,1 <p>
         *
         * @return builder
         */
        public Builder binary() {
            addFlag("--binary");
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
            addParameter("--link", link.toString());
            return this;
        }

        /**
         * use stagewise polynomial feature learning <p>
         *
         * @return builder
         */
        public Builder stagePoly() {
            addFlag("--stage_poly");
            return this;
        }

        /**
         * use low rank quadratic features with field aware weights <p>
         *
         * @param firstNamespace  - namespace or ":" for any
         * @param secondNamespace - namespace or ":" for any
         * @param k               factorized matrices width
         * @return builder
         */
        @Override
        public Builder lrqfa(final String firstNamespace, final String secondNamespace, final int k) {
            addParameter("--lrqfa", firstNamespace.charAt(0) + "" + secondNamespace.charAt(0) + "" + k);
            return this;
        }

        /**
         * use low rank quadratic features <p>
         *
         * @param firstNamespace  - namespace or ":" for any
         * @param secondNamespace - namespace or ":" for any
         * @param k               factorized matrices width
         * @return builder
         */
        public Builder lrq(final String firstNamespace, final String secondNamespace, final int k) {
            addParameter("--lrq", firstNamespace.charAt(0) + "" + secondNamespace.charAt(0) + "" + k);
            return this;
        }


        /**
         * use dropout training for low rank quadratic features <p>
         *
         * @return builder
         */
        public Builder lrqdropout() {
            addFlag("--lrqdropout");
            return this;
        }

        /**
         * create link function with polynomial d <p>
         *
         * @param d polynomial degree
         * @return builder
         */
        public Builder autolink(final int d) {
            addParameter("--autolink", String.valueOf(d));
            return this;
        }

        /**
         * rank for reduction-based matrix factorization <p>
         *
         * @param rank rank
         * @return builder
         */
        public Builder newMf(final int rank) {
            addParameter("--new_mf", String.valueOf(rank));
            return this;
        }

        /**
         * Sigmoidal feedforward network with &lt;k&gt; hidden units <p>
         *
         * @param units number of hidden units
         * @return builder
         */
        public Builder nn(final int units) {
            addParameter("--nn", String.valueOf(units));
            return this;
        }

        /**
         * Confidence after training <p>
         *
         * @return builder
         */
        public Builder confidenceAfterTraining() {
            addFlag("--confidence_after_training");
            return this;
        }

        /**
         * Get confidence for binary predictions <p>
         *
         * @return builder
         */
        public Builder confidence() {
            addFlag("--confidence");
            return this;
        }

        /**
         * enable active learning with cover <p>
         *
         * @return builder
         */
        public Builder activeCover() {
            addFlag("--active_cover");
            return this;
        }

        /**
         * enable active learning <p>
         *
         * @return builder
         */
        public Builder active() {
            addFlag("--active");
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
            addParameter("--replay_b", arg);
            return this;
        }

        /**
         * Online Newton with Oja's Sketch <p>
         *
         * @return builder
         */
        public Builder ojaNewton() {
            addFlag("--OjaNewton");
            return this;
        }

        /**
         * use bfgs optimization <p>
         *
         * @return builder
         */
        public Builder bfgs() {
            addFlag("--bfgs");
            return this;
        }

        /**
         * use conjugate gradient based optimization <p>
         *
         * @return builder
         */
        public Builder conjugateGradient() {
            addFlag("--conjugate_gradient");
            return this;
        }

        /**
         * Run lda with &lt;int&gt; topics <p>
         *
         * @param topics number of lda topics
         * @return builder
         */
        public Builder lda(final int topics) {
            addParameter("--lda", String.valueOf(topics));
            return this;
        }

        /**
         * do no learning <p>
         *
         * @return builder
         */
        public Builder noop() {
            addFlag("--noop");
            return this;
        }

        /**
         * rank for matrix factorization. <p>
         *
         * @param rank rank for matrix factorization
         * @return builder
         */
        public Builder rank(final int rank) {
            addParameter("--rank", String.valueOf(rank));
            return this;
        }

        /**
         * Streaming Stochastic Variance Reduced Gradient <p>
         *
         * @return builder
         */
        public Builder svrg() {
            addFlag("--svrg");
            return this;
        }

        /**
         * FTRL: Follow the Proximal Regularized Leader <p>
         *
         * @return builder
         */
        @Override
        public Builder ftrl() {
            addFlag("--ftrl");
            return this;
        }

        /**
         * FTRL: Parameter-free Stochastic Learning <p>
         *
         * @return builder
         */
        public Builder pistol() {
            addFlag("--pistol");
            return this;
        }

        /**
         * kernel svm <p>
         *
         * @return builder
         */
        public Builder ksvm() {
            addFlag("--ksvm");
            return this;
        }

        /**
         * use regular stochastic gradient descent update. <p>
         *
         * @return builder
         */
        @Override
        public Builder sgd() {
            addFlag("--sgd");
            return this;
        }

        /**
         * use adaptive, individual learning rates. <p>
         *
         * @return builder
         */
        @Override
        public Builder adaptive() {
            addFlag("--adaptive");
            return this;
        }

        /**
         * use safe/importance aware updates. <p>
         *
         * @return builder
         */
        @Override
        public Builder invariant() {
            addFlag("--invariant");
            return this;
        }

        /**
         * use per feature normalized updates <p>
         *
         * @return builder
         */
        @Override
        public Builder normalized() {
            addFlag("--normalized");
            return this;
        }

        /**
         * use per feature normalized updates (=0) <p>
         *
         * @param l2 l2 regularization. Must be not negative
         * @return builder
         */
        public Builder sparseL2(final double l2) {
            addParameter("--sparse_l2", String.valueOf(l2));
            return this;
        }

        /**
         * Use a cache.  The default is &lt;data&gt;.cache <p>
         *
         * @return builder
         */
        public Builder cache() {
            addFlag("--cache");
            return this;
        }

        /**
         * The location(s) of cacheFile. <p>
         *
         * @param cacheFile path to cache file
         * @return builder
         */
        public Builder cacheFile(final Path cacheFile) {
            addParameter("--cache_file", cacheFile.toString());
            return this;
        }

        /**
         * do not reuse existing cache: create a new one always <p>
         *
         * @return builder
         */
        public Builder killCache() {
            addFlag("--kill_cache");
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
            addFlag("--compressed");
            return this;
        }

        /**
         * Add vowpal wabbit argument <p>
         *
         * @param argumentLine parameter line
         * @return builder
         */
        public Builder parameter(final String argumentLine) {
            addFlag(argumentLine);
            return this;
        }

        private void addFlag(String argumentLine) {
            argumentsStrings.add(argumentLine);
        }

        /**
         * Get command arguments will be passes to VWLearner <p>
         *
         * @return command line arguments
         */
        public List<String> getCommandArguments() {
            final List<String> args = Lists.newArrayList(argumentsStrings);
            if (!verbose) {
                args.add("--quiet");
            }
            return args;
        }

        /**
         * Get command option will be passes to VWLearner <p>
         *
         * @return command options
         */
        public String getCommand() {
            final List<String> args = getCommandArguments();
            return Joiner.on(" ").join(args.subList(1, args.size()));
        }

        /**
         * @return VWIntArrayLearner object
         */
        public VWIntLearner buildIntLearner() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWIntLearner) VWLearners.create(getCommandArguments());
        }

        /**
         * @return VWIntLearner object
         */
        @Override
        public VWFloatLearner buildFloatLearner() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWFloatLearner) VWLearners.create(getCommandArguments());
        }

        @Override
        public <T extends VWLearner> T build() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return VWLearners.create(getCommandArguments());
        }
    }
}