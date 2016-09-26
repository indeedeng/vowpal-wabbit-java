package com.indeed.vw.wrapper.api;

/**
 * Function that measures the discrepancy between the algorithm's prediction and the desired output.
 * Part of generalized linear model framework.
 * Also check https://github.com/JohnLangford/vowpal_wabbit/wiki/Loss-functions.
 *
 */
public enum Loss {
    squared, classic, hinge, logistic, quantile, poisson
}
