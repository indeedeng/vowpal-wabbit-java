package com.indeed.vw.wrapper.api;

/**
 * Function applied on linear prediction.
 * Part of generalized linear model framework.
 * Also check https://github.com/JohnLangford/vowpal_wabbit/wiki/Loss-functions.
 *
 */
public enum Link {
    identity, logistic, glf1, poisson
}
