package com.indeed.vw.wrapper.api;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * OOP wrapper for vowpal-wabbit input format
 * Check https://github.com/JohnLangford/vowpal_wabbit/wiki/Input-format for documentation
 */
public class ExampleBuilder {
    private static final Pattern VW_CONTROL_CHARACTERS = Pattern.compile("[\\s:\\|]+");
    private ExampleBuilder() {
    }

    /**
     *
     * @return example builder
     */
    public static ExampleBuilder create() {
        return new ExampleBuilder();
    }

    /**
     * You can decide in any moment of the training that you want to save the current model in arbitrary file,
     * using a dummy example returned by this method
     *
     * @param modelPath
     * @return save command string
     */
    public static String buildSaveModelOnDemandCommand(@Nonnull final Path modelPath) {
        return "save_" + modelPath;
    }
    /**
     * Builder for namespace
     * Use ExampleBuilder to create an instance
     */
    public static class NamespaceBuilder {
        private final ExampleBuilder exampleBuilder;
        private final String name;
        private Double weight;
        private final StringBuilder features = new StringBuilder();

        private NamespaceBuilder(@Nonnull final ExampleBuilder exampleBuilder, @Nonnull final String name) {
            this.exampleBuilder = exampleBuilder;
            this.name = name;
        }

        /**
         * Namespace weight acts as a global scaling of all the values of the features in this namespace.
         * If value is omitted, the default is 1.
         *
         * @param weight
         * @return builder
         */
        public NamespaceBuilder namespaceWeight(final double weight) {
            Preconditions.checkArgument(Doubles.isFinite(weight),
                    "Incorrect namespace weight: " + weight);
            this.weight = weight;
            return this;
        }
        /**
         * Add a categorical feature to this namespace (e.g. userId, jobId)
         *
         * @param feature - categorical value
         * @return builder
         */
        public NamespaceBuilder addCategoricalFeature(@Nonnull final String feature) {
            Preconditions.checkArgument(!VW_CONTROL_CHARACTERS.matcher(feature).find(),
                    "Bad feature name! " +
                        "Namespace=" + name + " feature=" + feature);
            features.append(feature).append(" ");
            return this;
        }

        /**
         * Add text feature to this namespace (e.g. job description)
         *
         * @param text
         * @return builder
         */
        public NamespaceBuilder addTextAsFeatures(@Nonnull final String text) {
            final String preparedText = VW_CONTROL_CHARACTERS.matcher(text).replaceAll(" ");
            features.append(preparedText).append(" ");
            return this;
        }

        /**
         * Add numerical feature to this namespace (e.g. time series previous values)
         * Also you may use this method to add categorical feature with weight
         * (e.g. bag of words model with tf-idf weights)
         *
         * @param featureName
         * @param numericalValue
         * @return builder
         */
        public NamespaceBuilder addNumericalFeature(@Nonnull final String featureName, final double numericalValue) {
            Preconditions.checkArgument(!VW_CONTROL_CHARACTERS.matcher(featureName).find(),
                    "Bad feature name! " +
                        "Namespace=" + name + " feature=" + featureName + " weight=" + numericalValue);
            Preconditions.checkArgument(Doubles.isFinite(numericalValue),
                    "Feature weight must be finite! " +
                            "Namespace=" + name + " feature=" + featureName + " weight=" + numericalValue);
            features.append(featureName).append(":").append(numericalValue).append(" ");
            return this;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("|");
            sb.append(name);
            if (weight != null) {
                sb.append(":").append(weight);
            }
            sb.append(" ");
            sb.append(features);
            return sb.toString().trim();
        }
    }

    @Nullable
    private String label;

    @Nullable
    private String tag;

    @Nullable
    private Double exampleWeight;

    private final List<NamespaceBuilder> namespaceBuilders = new ArrayList<>();

    /**
     * Label is the real number that we are trying to predict for this example.
     * <b>Important note:</b> when using logistic or hinge loss, the labels need to be from the set {+1,-1}
     *
     * @param label
     * @return
     */
    public ExampleBuilder label(final double label) {
        Preconditions.checkArgument(Doubles.isFinite(label),
                "Incorrect label: " + label);
        this.label = Double.toString(label);
        return this;
    }

    /**
     * If the label is omitted, then no training will be performed with the corresponding example,
     * although VW will still compute a prediction.
     *
     * @return builder
     */
    public ExampleBuilder omitLabel() {
        this.label = null;
        return this;
    }

    /**
     * Advanced option
     * ===============
     * you can use it to pass labels for multi-class classification

     * @return builder
     */
    public ExampleBuilder setLabelString(@Nonnull final String label) {
        this.label = label;
        return this;
    }

    /**
     * Tag is a string that serves as an identifier for the example.
     * It is reported back when predictions are made. It doesn't have to be unique.
     * The default value if it is not provided is the empty string.
     *
     * @param tag
     * @return builder
     */
    public ExampleBuilder exampleTag(@Nonnull final String tag) {
        Preconditions.checkArgument(!VW_CONTROL_CHARACTERS.matcher(tag).find(),
                "Incorrect tag: " + tag);
        this.tag = tag;
        return this;
    }

    /**
     * Importance (importance weight) is a non-negative real number indicating the relative importance
     * of this example over the others. Omitting this gives a default importance of 1 to the example.
     *
     * Notice that you should pass --invariant option in order to properly deal with example importances.
     *
     * @param exampleImportance
     * @return builder
     */
    public ExampleBuilder exampleImportance(final double exampleImportance) {
        Preconditions.checkArgument(Doubles.isFinite(exampleImportance),
                "Incorrect example weight: " + exampleImportance);
        this.exampleWeight = exampleImportance;
        return this;
    }

    public NamespaceBuilder createNamespace(@Nonnull final String namespace) {
        Preconditions.checkArgument(!VW_CONTROL_CHARACTERS.matcher(namespace).find(),
                "Bad namespace name!" +
                        "Namespace=" + namespace);
        final NamespaceBuilder namespaceBuilder = new NamespaceBuilder(this, namespace);
        namespaceBuilders.add(namespaceBuilder);
        return namespaceBuilder;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (label != null) {
            sb.append(label);
        }
        sb.append(" ");
        if (exampleWeight != null) {
            sb.append(exampleWeight);
            sb.append(" ");
        }
        if (tag != null) {
            sb.append("'");
            sb.append(tag);
        }
        sb.append(Joiner.on(" ").join(namespaceBuilders));
        return sb.toString();
    }

    /**
     * Serialize example in vowpal wabbit input format
     *
     * @return string
     */
    public String build() {
        return toString();
    }
}
