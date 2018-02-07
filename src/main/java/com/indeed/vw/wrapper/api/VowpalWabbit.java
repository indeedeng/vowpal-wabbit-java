package com.indeed.vw.wrapper.api;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
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
import java.util.List;

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

        private final List<String> argumentsStrings = Lists.newArrayList("vw"); // put a dummy program name as the first argument.
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
            argumentsStrings.add("--random_seed");
            argumentsStrings.add(String.valueOf(seed));
            return this;
        }

        /**
         * size of example ring buffer <p>
         *
         * @param ringSize size of example ring
         * @return builder
         */
        public Builder ringSize(final int ringSize) {
            argumentsStrings.add("--ring_size");
            argumentsStrings.add(String.valueOf(ringSize));
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
            argumentsStrings.add("--learning_rate");
            argumentsStrings.add(String.valueOf(learningRate));
            return this;
        }

        /**
         * t power value <p>
         *
         * @param powerT t power value
         * @return builder
         */
        public Builder powerT(final double powerT) {
            argumentsStrings.add("--power_t");
            argumentsStrings.add(String.valueOf(powerT));
            return this;
        }

        /**
         * Set Decay factor for learning_rate between passes <p>
         *
         * @param decay exponential decay
         * @return builder
         */
        public Builder decayLearningRate(final double decay) {
            argumentsStrings.add("--decay_learning_rate");
            argumentsStrings.add(String.valueOf(decay));
            return this;
        }

        /**
         * initial t value <p>
         *
         * @param initialT initial t value
         * @return builder
         */
        public Builder initialT(final double initialT) {
            argumentsStrings.add("--initial_t");
            argumentsStrings.add(String.valueOf(initialT));
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
            argumentsStrings.add("--feature_mask");
            argumentsStrings.add(featureMask.toString());
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
            argumentsStrings.add("--initial_regressor");
            argumentsStrings.add(initialRegressor.toString());
            return this;
        }

        /**
         * Set all weights to an initial value of arg. <p>
         *
         * @param weight initial weight value
         * @return builder
         */
        public Builder initialWeight(final double weight) {
            argumentsStrings.add("--initial_weight");
            argumentsStrings.add(String.valueOf(weight));
            return this;
        }

        /**
         * make initial weights random <p>
         *
         * @param arg random maximum
         * @return builder
         */
        public Builder randomWeights(final double arg) {
            argumentsStrings.add("--random_weights");
            argumentsStrings.add(String.valueOf(arg));
            return this;
        }

        /**
         * Per feature regularization input file <p>
         *
         * @param regularizationPath  path to regularization input file
         * @return builder
         */
        public Builder inputFeatureRegularizer(final Path regularizationPath) {
            argumentsStrings.add("--input_feature_regularizer");
            argumentsStrings.add(regularizationPath.toString());
            return this;
        }

        /**
         * how to hash the features. Available options: strings, all <p>
         *
         * @param hash hash strategy
         * @return builder
         */
        public Builder hash(final Hash hash) {
            argumentsStrings.add("--hash");
            argumentsStrings.add(hash.toString());
            return this;
        }

        /**
         * ignore namespace  &lt;arg&gt; <p>
         *
         * @param namespace namespace name
         * @return builder
         */
        public Builder ignore(final String namespace) {
            argumentsStrings.add("--ignore");
            argumentsStrings.add(String.valueOf(namespace.charAt(0)));
            return this;
        }

        /**
         * keep namespace &lt;arg&gt; <p>
         *
         * @param namespace namespace name
         * @return builder
         */
        public Builder keep(final String namespace) {
            argumentsStrings.add("--keep");
            argumentsStrings.add(String.valueOf(namespace.charAt(0)));
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
            argumentsStrings.add("--redefine");
            argumentsStrings.add(newNamespace + ":=" + oldNamespaces);
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
            argumentsStrings.add("--bit_precision");
            argumentsStrings.add(String.valueOf(bitsNum));
            return this;
        }

        /**
         * Don't add a constant feature <p>
         *
         * @return builder
         */
        @Override
        public Builder noconstant() {
            argumentsStrings.add("--noconstant");
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
            argumentsStrings.add("--constant");
            argumentsStrings.add(String.valueOf(initialValue));
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
                argumentsStrings.add("--ngram");
                argumentsStrings.add(String.valueOf(n));
                return this;
            }
            argumentsStrings.add("--ngram");
            argumentsStrings.add(namespace.charAt(0) + "" + n);
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
                argumentsStrings.add("--skips");
                argumentsStrings.add(String.valueOf(n));
                return this;
            }
            argumentsStrings.add("--skips");
            argumentsStrings.add(namespace.charAt(0) + "" + n);
            return this;
        }

        /**
         * limit to N features. To apply to a single namespace 'foo', arg should be fN <p>
         *
         * @param n number of features
         * @return builder
         */
        public Builder featureLimit(final int n) {
            argumentsStrings.add("--feature_limit");
            argumentsStrings.add(String.valueOf(n));
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
            argumentsStrings.add("--affix");
            argumentsStrings.add(arg);
            return this;
        }

        /**
         * compute spelling features for a give namespace (use '_' for default namespace) <p>
         *
         * @param namespace namespace
         * @return builder
         */
        public Builder spelling(final String namespace) {
            argumentsStrings.add("--spelling");
            argumentsStrings.add(String.valueOf(namespace.charAt(0)));
            return this;
        }

        /**
         * read a dictionary for additional features (arg either 'x:file' or just 'file') <p>
         *
         * @param file dictionary path
         * @return builder
         */
        public Builder dictionary(final Path file) {
            argumentsStrings.add("--dictionary");
            argumentsStrings.add(file.toString());
            return this;
        }

        /**
         * look in this directory for dictionaries; defaults to current directory or env{PATH} <p>
         *
         * @param dir dictionaries directory path
         * @return builder
         */
        public Builder dictionaryPath(final Path dir) {
            argumentsStrings.add("--dictionary_path");
            argumentsStrings.add(dir.toString());
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
            argumentsStrings.add("--interactions");
            argumentsStrings.add(namespacesChars.toString());
            return this;
        }

        /**
         * Use permutations instead of combinations for feature interactions of same namespace. <p>
         *
         * @return builder
         */
        public Builder permutations() {
            argumentsStrings.add("--permutations");
            return this;
        }

        /**
         * Don't remove interactions with duplicate combinations of namespaces. <p>
         * For ex. this is a duplicate: '-q ab -q ba' and a lot more in '-q ::'. <p>
         *
         * @return builder
         */
        public Builder leaveDuplicateInteractions() {
            argumentsStrings.add("--leave_duplicate_interactions");
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
            argumentsStrings.add("--quadratic");
            argumentsStrings.add(firstNameSpace.charAt(0) + "" + secondNamespace.charAt(0));
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
            argumentsStrings.add("--cubic");
            argumentsStrings.add(firstNameSpace.charAt(0) + "" + secondNamespace.charAt(0) + "" + thirdNamespace.charAt(0));
            return this;
        }

        /**
         * Ignore label information and just test <p>
         *
         * @return builder
         */
        @Override
        public Builder testonly() {
            argumentsStrings.add("--testonly");
            return this;
        }

        /**
         * holdout period for test only, default 10 <p>
         *
         * @param holdout holdout period size
         * @return builder
         */
        public Builder holdoutPeriod(final int holdout) {
            argumentsStrings.add("--holdout_period");
            argumentsStrings.add(String.valueOf(holdout));
            return this;
        }

        /**
         * holdout after n training examples, default off (disables holdoutPeriod) <p>
         *
         * @param n number of examples in hodout
         * @return builder
         */
        public Builder holdoutAfter(final int n) {
            argumentsStrings.add("--holdout_after");
            argumentsStrings.add(String.valueOf(n));
            return this;
        }

        /**
         * Specify the number of passes tolerated when holdout loss doesn't decrease before early termination, default is 3 <p>
         *
         * @param passes number of passes
         * @return builder
         */
        public Builder earlyTerminate(final int passes) {
            argumentsStrings.add("--early_terminate");
            argumentsStrings.add(String.valueOf(passes));
            return this;
        }

        /**
         * Number of Training Passes <p>
         *
         * @param passes number of passes
         * @return builder
         */
        public Builder passes(final int passes) {
            argumentsStrings.add("--passes");
            argumentsStrings.add(String.valueOf(passes));
            return cache();
        }

        /**
         * initial number of examples per pass <p>
         *
         * @param examples number of examples per pass
         * @return builder
         */
        public Builder initialPassLength(final int examples) {
            argumentsStrings.add("--initial_pass_length");
            argumentsStrings.add(String.valueOf(examples));
            return this;
        }

        /**
         * number of examples to parse <p>
         *
         * @param examples number of examples to parse
         * @return builder
         */
        public Builder examples(final int examples) {
            argumentsStrings.add("--examples");
            argumentsStrings.add(String.valueOf(examples));
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
            argumentsStrings.add("--min_prediction");
            argumentsStrings.add(String.valueOf(min));
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
            argumentsStrings.add("--max_prediction");
            argumentsStrings.add(String.valueOf(max));
            return this;
        }

        /**
         * turn this on to disregard order in which features have been defined. This will lead to smaller cache sizes <p>
         *
         * @return builder
         */
        public Builder sortFeatures() {
            argumentsStrings.add("--sort_features");
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
            argumentsStrings.add("--loss_function");
            argumentsStrings.add(loss.toString());
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
            argumentsStrings.add("--quantile_tau");
            argumentsStrings.add(String.valueOf(tau));
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
            argumentsStrings.add("--l1");
            argumentsStrings.add(String.valueOf(l1));
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
            argumentsStrings.add("--l2");
            argumentsStrings.add(String.valueOf(l2));
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
            argumentsStrings.add("--named_labels");
            argumentsStrings.add(Joiner.on(",").join(Arrays.asList(labels)));
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
            argumentsStrings.add("--final_regressor");
            argumentsStrings.add(regressor.toString());
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
            argumentsStrings.add("--readable_model");
            argumentsStrings.add(model.toString());
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
            argumentsStrings.add("--invert_hash");
            argumentsStrings.add(model.toString());
            return this;
        }

        /**
         * save extra state so learning can be resumed later with new data <p>
         *
         * @return builder
         */
        public Builder saveResume() {
            argumentsStrings.add("--save_resume");
            return this;
        }

        /**
         * Save the model after every pass over data <p>
         *
         * @return builder
         */
        public Builder savePerPass() {
            argumentsStrings.add("--save_per_pass");
            return this;
        }

        /**
         * Per feature regularization output file <p>
         *
         * @param regularizationFile path where to store regularization output file
         * @return builder
         */
        public Builder outputFeatureRegularizerBinary(final Path regularizationFile) {
            argumentsStrings.add("--output_feature_regularizer_binary");
            argumentsStrings.add(regularizationFile.toString());
            return this;
        }

        /**
         * Per feature regularization output file, in text <p>
         *
         * @param regularizationFile path where to store regularization output file
         * @return builder
         */
        public Builder outputFeatureRegularizerText(final Path regularizationFile) {
            argumentsStrings.add("--output_feature_regularizer_text");
            argumentsStrings.add(regularizationFile.toString());
            return this;
        }

        /**
         * User supplied ID embedded into the final regressor <p>
         *
         * @param id model id
         * @return builder
         */
        public Builder id(final String id) {
            argumentsStrings.add("--id");
            argumentsStrings.add(id);
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
            argumentsStrings.add("--audit_regressor");
            argumentsStrings.add(regressor.toString());
            return this;
        }

        /**
         * k-way bootstrap by online importance resampling <p>
         *
         * @param k number of bootstrap resamples
         * @return builder
         */
        public Builder bootstrap(final int k) {
            argumentsStrings.add("--bootstrap");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * Use learning to search, argument=maximum action id or 0 for LDF <p>
         *
         * @param maxActionID max action id
         * @return builder
         */
        public Builder search(final int maxActionID) {
            argumentsStrings.add("--search");
            argumentsStrings.add(String.valueOf(maxActionID));
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
            argumentsStrings.add("--replay_c");
            argumentsStrings.add(arg);
            return this;
        }

        /**
         * Convert multiclass on &lt;k&gt; classes into a contextual bandit problem <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder cbify(final int k) {
            argumentsStrings.add("--cbify");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * Online explore-exploit for a contextual bandit problem with multiline action dependent features <p>
         *
         * @return builder
         */
        public Builder cbExploreAdf() {
            argumentsStrings.add("--cb_explore_adf");
            return this;
        }

        /**
         * Online explore-exploit for a &lt;k&gt; action contextual bandit problem <p>
         *
         * @param k number of actions
         * @return builder
         */
        public Builder cbExplore(final int k) {
            argumentsStrings.add("--cb_explore");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * Evaluate features as a policies <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder multiworldTest(final String arg) {
            argumentsStrings.add("--multiworld_test");
            argumentsStrings.add(arg);
            return this;
        }

        /**
         * Do Contextual Bandit learning with multiline action dependent features. <p>
         *
         * @return builder
         */
        public Builder cbAdf() {
            argumentsStrings.add("--cb_adf");
            return this;
        }

        /**
         * Use contextual bandit learning with &lt;k&gt; costs <p>
         *
         * @param k number of costs
         * @return builder
         */
        public Builder cb(final int k) {
            argumentsStrings.add("--cb");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * Use one-against-all multiclass learning with label dependent features.  Specify singleline or multiline. <p>
         *
         * @param ldf ldf
         * @return builder
         */
        public Builder csoaaLdf(final LDF ldf) {
            argumentsStrings.add("--csoaa_ldf");
            argumentsStrings.add(ldf.toString());
            return this;
        }

        /**
         * Use weighted all-pairs multiclass learning with label dependent features.   Specify singleline or multiline. <p>
         *
         * @param ldf ldf
         * @return builder
         */
        public Builder wapLdf(final LDF ldf) {
            argumentsStrings.add("--wap_ldf");
            argumentsStrings.add(ldf.toString());
            return this;
        }

        /**
         * Put weights on feature products from namespaces &lt;n1&gt; and &lt;n2&gt; <p>
         *
         * @param arg argument
         * @return builder
         */
        public Builder interact(final String arg) {
            argumentsStrings.add("--interact");
            argumentsStrings.add(arg);
            return this;
        }

        /**
         * One-against-all multiclass with &lt;k&gt; costs <p>
         *
         * @param k number of costs
         * @return builder
         */
        public Builder csoaa(final int k) {
            argumentsStrings.add("--csoaa");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * One-against-all multilabel with &lt;k&gt; labels <p>
         *
         * @param k number of labels
         * @return builder
         */
        public Builder multilabelOaa(final int k) {
            argumentsStrings.add("--multilabel_oaa");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * Use online tree for multiclass <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder recallTree(final int k) {
            argumentsStrings.add("--recall_tree");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * Use online tree for multiclass <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder logMulti(final int k) {
            argumentsStrings.add("--log_multi");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * Error correcting tournament with &lt;k&gt; labels <p>
         *
         * @param k number of labels
         * @return builder
         */
        public Builder ect(final int k) {
            argumentsStrings.add("--ect");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * Online boosting with &lt;N&gt; weak learners <p>
         *
         * @param n number of weak learners
         * @return builder
         */
        public Builder boosting(final int n) {
            argumentsStrings.add("--boosting");
            argumentsStrings.add(String.valueOf(n));
            return this;
        }

        /**
         * One-against-all multiclass with &lt;k&gt; labels <p>
         *
         * @param k number of classes
         * @return builder
         */
        public Builder oaa(final int k) {
            argumentsStrings.add("--oaa");
            argumentsStrings.add(String.valueOf(k));
            return this;
        }

        /**
         * top k recommendation <p>
         *
         * @param k number of top recomendations
         * @return builder
         */
        public Builder top(final int k) {
            argumentsStrings.add("--top");
            argumentsStrings.add(String.valueOf(k));
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
            argumentsStrings.add("--replay_m");
            argumentsStrings.add(arg);
            return this;
        }

        /**
         * report loss as binary classification on -1,1 <p>
         *
         * @return builder
         */
        public Builder binary() {
            argumentsStrings.add("--binary");
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
            argumentsStrings.add("--link");
            argumentsStrings.add(link.toString());
            return this;
        }

        /**
         * use stagewise polynomial feature learning <p>
         *
         * @return builder
         */
        public Builder stagePoly() {
            argumentsStrings.add("--stage_poly");
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
            argumentsStrings.add("--lrqfa");
            argumentsStrings.add(firstNamespace.charAt(0) + "" + secondNamespace.charAt(0) + "" + k);
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
            argumentsStrings.add("--lrq");
            argumentsStrings.add(firstNamespace.charAt(0) + "" + secondNamespace.charAt(0) + "" + k);
            return this;
        }


        /**
         * use dropout training for low rank quadratic features <p>
         *
         * @return builder
         */
        public Builder lrqdropout() {
            argumentsStrings.add("--lrqdropout");
            return this;
        }

        /**
         * create link function with polynomial d <p>
         *
         * @param d polynomial degree
         * @return builder
         */
        public Builder autolink(final int d) {
            argumentsStrings.add("--autolink");
            argumentsStrings.add(String.valueOf(d));
            return this;
        }

        /**
         * rank for reduction-based matrix factorization <p>
         *
         * @param rank rank
         * @return builder
         */
        public Builder newMf(final int rank) {
            argumentsStrings.add("--new_mf");
            argumentsStrings.add(String.valueOf(rank));
            return this;
        }

        /**
         * Sigmoidal feedforward network with &lt;k&gt; hidden units <p>
         *
         * @param units number of hidden units
         * @return builder
         */
        public Builder nn(final int units) {
            argumentsStrings.add("--nn");
            argumentsStrings.add(String.valueOf(units));
            return this;
        }

        /**
         * Confidence after training <p>
         *
         * @return builder
         */
        public Builder confidenceAfterTraining() {
            argumentsStrings.add("--confidence_after_training");
            return this;
        }

        /**
         * Get confidence for binary predictions <p>
         *
         * @return builder
         */
        public Builder confidence() {
            argumentsStrings.add("--confidence");
            return this;
        }

        /**
         * enable active learning with cover <p>
         *
         * @return builder
         */
        public Builder activeCover() {
            argumentsStrings.add("--active_cover");
            return this;
        }

        /**
         * enable active learning <p>
         *
         * @return builder
         */
        public Builder active() {
            argumentsStrings.add("--active");
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
            argumentsStrings.add("--replay_b");
            argumentsStrings.add(arg);
            return this;
        }

        /**
         * Online Newton with Oja's Sketch <p>
         *
         * @return builder
         */
        public Builder ojaNewton() {
            argumentsStrings.add("--OjaNewton");
            return this;
        }

        /**
         * use bfgs optimization <p>
         *
         * @return builder
         */
        public Builder bfgs() {
            argumentsStrings.add("--bfgs");
            return this;
        }

        /**
         * use conjugate gradient based optimization <p>
         *
         * @return builder
         */
        public Builder conjugateGradient() {
            argumentsStrings.add("--conjugate_gradient");
            return this;
        }

        /**
         * Run lda with &lt;int&gt; topics <p>
         *
         * @param topics number of lda topics
         * @return builder
         */
        public Builder lda(final int topics) {
            argumentsStrings.add("--lda");
            argumentsStrings.add(String.valueOf(topics));
            return this;
        }

        /**
         * do no learning <p>
         *
         * @return builder
         */
        public Builder noop() {
            argumentsStrings.add("--noop");
            return this;
        }

        /**
         * rank for matrix factorization. <p>
         *
         * @param rank rank for matrix factorization
         * @return builder
         */
        public Builder rank(final int rank) {
            argumentsStrings.add("--rank");
            argumentsStrings.add(String.valueOf(rank));
            return this;
        }

        /**
         * Streaming Stochastic Variance Reduced Gradient <p>
         *
         * @return builder
         */
        public Builder svrg() {
            argumentsStrings.add("--svrg");
            return this;
        }

        /**
         * FTRL: Follow the Proximal Regularized Leader <p>
         *
         * @return builder
         */
        @Override
        public Builder ftrl() {
            argumentsStrings.add("--ftrl");
            return this;
        }

        /**
         * FTRL: Parameter-free Stochastic Learning <p>
         *
         * @return builder
         */
        public Builder pistol() {
            argumentsStrings.add("--pistol");
            return this;
        }

        /**
         * kernel svm <p>
         *
         * @return builder
         */
        public Builder ksvm() {
            argumentsStrings.add("--ksvm");
            return this;
        }

        /**
         * use regular stochastic gradient descent update. <p>
         *
         * @return builder
         */
        @Override
        public Builder sgd() {
            argumentsStrings.add("--sgd");
            return this;
        }

        /**
         * use adaptive, individual learning rates. <p>
         *
         * @return builder
         */
        @Override
        public Builder adaptive() {
            argumentsStrings.add("--adaptive");
            return this;
        }

        /**
         * use safe/importance aware updates. <p>
         *
         * @return builder
         */
        @Override
        public Builder invariant() {
            argumentsStrings.add("--invariant");
            return this;
        }

        /**
         * use per feature normalized updates <p>
         *
         * @return builder
         */
        @Override
        public Builder normalized() {
            argumentsStrings.add("--normalized");
            return this;
        }

        /**
         * use per feature normalized updates (=0) <p>
         *
         * @param l2 l2 regularization. Must be not negative
         * @return builder
         */
        public Builder sparseL2(final double l2) {
            argumentsStrings.add("--sparse_l2");
            argumentsStrings.add(String.valueOf(l2));
            return this;
        }

        /**
         * Use a cache.  The default is &lt;data&gt;.cache <p>
         *
         * @return builder
         */
        public Builder cache() {
            argumentsStrings.add("--cache");
            return this;
        }

        /**
         * The location(s) of cacheFile. <p>
         *
         * @param cacheFile path to cache file
         * @return builder
         */
        public Builder cacheFile(final Path cacheFile) {
            argumentsStrings.add("--cache_file");
            argumentsStrings.add(cacheFile.toString());
            return this;
        }

        /**
         * do not reuse existing cache: create a new one always <p>
         *
         * @return builder
         */
        public Builder killCache() {
            argumentsStrings.add("--kill_cache");
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
            argumentsStrings.add("--compressed");
            return this;
        }

        /**
         * Add vowpal wabbit argument <p>
         *
         * @param argumentLine parameter line
         * @return builder
         */
        public Builder parameter(final String argumentLine) {
            argumentsStrings.add(argumentLine);
            return this;
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
            final List<String> args = Lists.newArrayList(argumentsStrings);
            if (!verbose) {
                args.add("--quiet");
            }
            return Joiner.on(" ").join(args.subList(1, args.size()));
        }

        /**
         *
         * @return VWIntLearner object
         */
        @Override
        public VWFloatLearner buildFloatLearner() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWFloatLearner) VWLearners.create(getCommandArguments());
        }

        /**
         *
         * @return VWIntLearner object
         */
        @Override
        public VWIntLearner buildIntLearner() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWIntLearner) VWLearners.create(getCommandArguments());
        }

        /**
         *
         * @return VWFloatArrayLearner object
         */
        public VWFloatArrayLearner buildFloatArrayLearner() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWFloatArrayLearner) VWLearners.create(getCommandArguments());
        }

        /**
         *
         * @return VWIntArrayLearner object
         */
        public VWIntArrayLearner buildIntArrayLearner() {
            logger.info("Vowpal wabbit command: " + getCommand());
            return (VWIntArrayLearner) VWLearners.create(getCommandArguments());
        }
    }
}