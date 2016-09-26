package com.indeed.vw.wrapper.api.parameters;

/**
 * Function that measures the discrepancy between the algorithm's prediction and the desired output. <p>
 * Part of generalized linear model framework. <p>
 * Also check <a href="https://github.com/JohnLangford/vowpal_wabbit/wiki/Loss-functions">https://github.com/JohnLangford/vowpal_wabbit/wiki/Loss-functions</a> <p>
 *
 */
public enum Loss {
    squared, classic, hinge, logistic, quantile, poisson
}
