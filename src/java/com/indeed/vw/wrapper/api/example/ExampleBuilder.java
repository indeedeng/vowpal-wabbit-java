package com.indeed.vw.wrapper.api.example;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * OOP wrapper for vowpal-wabbit input format. <p>
 *
 * Check <a href="https://github.com/JohnLangford/vowpal_wabbit/wiki/Input-format">https://github.com/JohnLangford/vowpal_wabbit/wiki/Input-format</a> for format documentation. <p>
 */
public class ExampleBuilder {

    private static final Pattern VW_CONTROL_CHARACTERS = Pattern.compile("[\\s:\\|]+");

    private final boolean doNotCheckNamespaces;

    @Nullable
    private String label;

    @Nullable
    private String tag;

    @Nullable
    private Double exampleWeight;

    private final List<NamespaceBuilder> namespaceBuilders = new ArrayList<>();

    private final Set<Character> namespaceFirstCharacters = new HashSet<>();

    private ExampleBuilder(final boolean doNotCheckNamespaces) {
        this.doNotCheckNamespaces = doNotCheckNamespaces;
    }

    /**
     * Create method <p>
     *
     * @return example builder
     */
    public static ExampleBuilder create() {
        return new ExampleBuilder(false);
    }

    /**
     * By default each namespace should start with unique character. <p>
     * If you want to have multiple namespaces starting with same character use this method. <p>
     *
     * @return example builder
     */
    public static ExampleBuilder createAndDoNotCheckNamespace() {
        return new ExampleBuilder(true);
    }

    /**
     * Builder for namespace. <p>
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
         * Namespace weight acts as a global scaling of all the values of the features in this namespace. <p>
         * If value is omitted, the default is 1. <p>
         *
         * @param weight namespace weight
         * @return builder
         */
        public NamespaceBuilder namespaceWeight(final double weight) {
            Preconditions.checkArgument(Doubles.isFinite(weight),
                    "Incorrect namespace weight: " + weight);
            this.weight = weight;
            return this;
        }

        /**
         * Add a categorical feature to this namespace (e.g. userId, jobId) <p>
         *
         * @param feature categorical feature
         * @return builder
         */
        public NamespaceBuilder addCategoricalFeature(@Nonnull final Object feature) {
            Preconditions.checkArgument(!VW_CONTROL_CHARACTERS.matcher(feature.toString()).find(),
                    "Bad feature name! " +
                        "Namespace=" + name + " feature=" + feature);
            features.append(feature).append(" ");
            return this;
        }

        /**
         * Add a categorical feature to this namespace (e.g. userId, jobId) <p>
         *
         * @param subNamespace sub namespace - e.g. namespace=person, subNamespace=gender, feature=Female
         * @param categoricalValue - categorical value
         * @return builder
         */
        public NamespaceBuilder addCategoricalFeature(@Nonnull final String subNamespace, @Nonnull final Object categoricalValue) {
            final String feature = subNamespace + "=" + categoricalValue;
            Preconditions.checkArgument(!VW_CONTROL_CHARACTERS.matcher(feature).find(),
                    "Bad feature name! " +
                            "Namespace=" + name + " feature=" + feature);
            features.append(feature).append(" ");
            return this;
        }

        /**
         * Add text feature to this namespace (e.g. job description) <p>
         *
         * @param text raw text
         * @return builder
         */
        public NamespaceBuilder addTextAsFeatures(@Nonnull final String text) {
            final String preparedText = VW_CONTROL_CHARACTERS.matcher(text).replaceAll(" ");
            features.append(preparedText).append(" ");
            return this;
        }

        /**
         * Add numerical feature to this namespace (e.g. time series previous values) <p>
         * Also you may use this method to add categorical feature with weight <p>
         * (e.g. bag of words model with tf-idf weights) <p>
         *
         * @param featureName name of numerical feature (e.g. ctr)
         * @param numericalValue value of numerical feature (e.g. 0.032)
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

    /**
     * Label is the real number that we are trying to predict for this example. <p>
     * <b>Important note:</b> when using logistic or hinge loss, the labels need to be from the set {+1,-1} <p>
     *
     * @param label example numerical label
     * @return builder
     */
    public ExampleBuilder label(final double label) {
        Preconditions.checkArgument(Doubles.isFinite(label),
                "Incorrect label: " + label);
        this.label = Double.toString(label);
        return this;
    }

    /**
     * This is convenient method to set label when you use logistic or hinge loss you. <p>
     * This label will convert labels to be from the set {+1,-1}. <p>
     *
     * @param binaryLabel example binary label
     * @return builder
     */
    public ExampleBuilder binaryLabel(final boolean binaryLabel) {
        this.label = binaryLabel ? "1" : "-1";
        return this;
    }

    /**
     * Get example label <p>
     *
     * @return label
     */
    public double getLabelAsDouble() {
        Preconditions.checkNotNull(label, "This example doesn't have a label! Set it using label() method.");
        return Double.parseDouble(label);
    }
    /**
     * Get example tag <p>
     *
     * @return tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * If the label is omitted, then no training will be performed with the corresponding example, <p>
     * although VW will still compute a prediction. <p>
     *
     * @return builder
     */
    public ExampleBuilder omitLabel() {
        this.label = null;
        return this;
    }

    /**
     * You can use it to pass labels for multi-class classification. <p>
     *
     * @param label label
     * @return builder
     */
    public ExampleBuilder setLabelString(@Nonnull final String label) {
        this.label = label;
        return this;
    }

    /**
     * Tag is a string that serves as an identifier for the example. <p>
     * It is reported back when predictions are made. It doesn't have to be unique. <p>
     * The default value if it is not provided is the empty string. <p>
     *
     * @param tag example tag
     * @return builder
     */
    public ExampleBuilder exampleTag(@Nonnull final String tag) {
        Preconditions.checkArgument(!VW_CONTROL_CHARACTERS.matcher(tag).find(),
                "Incorrect tag: " + tag);
        this.tag = tag;
        return this;
    }

    /**
     * Importance (importance weight) is a non-negative real number indicating the relative importance <p>
     * of this example over the others. Omitting this gives a default importance of 1 to the example. <p>
     *
     * Notice that you should pass -- invariant option in order to properly deal with example importance. <p>
     *
     * @param exampleImportance example importance. Must be not negative
     * @return builder
     */
    public ExampleBuilder exampleImportance(final double exampleImportance) {
        Preconditions.checkArgument(Doubles.isFinite(exampleImportance),
                "Incorrect example weight: " + exampleImportance);
        this.exampleWeight = exampleImportance;
        return this;
    }

    public NamespaceBuilder createNamespace(@Nonnull final String namespace) {
        Preconditions.checkArgument(!namespace.isEmpty(), "Namespace should not be empty!");
        Preconditions.checkArgument(!VW_CONTROL_CHARACTERS.matcher(namespace).find(),
                "Bad namespace name!" +
                        "Namespace=" + namespace);
        Preconditions.checkArgument(doNotCheckNamespaces || !namespaceFirstCharacters.contains(namespace.charAt(0)),
                        "Please use a unique first character for each namespace. \n" +
                        "This is necessary because vowpal wabbit options like '--keep', '--quadratic', '--cubic' \n" +
                        "only look at the first character of the namespace for performance reasons.\n" +
                        "If you want to have multiple namespaces that start with same character - " +
                        "create ExampleBuilder instance using ExampleBuilder.createAndDoNotCheckNamespace() method.\n" +
                        "These namespaces start with same character: " + namespace + ", " +
                                namespaceThatStartWithSameCharacter(namespace));
        final NamespaceBuilder namespaceBuilder = new NamespaceBuilder(this, namespace);
        namespaceBuilders.add(namespaceBuilder);
        namespaceFirstCharacters.add(namespace.charAt(0));
        return namespaceBuilder;
    }

    private String namespaceThatStartWithSameCharacter(final String namespace) {
        for (final NamespaceBuilder anotherNameSpace : namespaceBuilders) {
            if (namespace.charAt(0) == anotherNameSpace.name.charAt(0)) {
                return anotherNameSpace.name;
            }
        }
        return "";
    }

    /**
     * Build vowpal wabbit example string <p>
     *
     * @return vowpal wabbit example string
     */
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
}
