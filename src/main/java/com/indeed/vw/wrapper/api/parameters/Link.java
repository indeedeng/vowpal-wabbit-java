package com.indeed.vw.wrapper.api.parameters;

/**
 * Function applied on linear prediction. <p>
 * Part of generalized linear model framework. <p>
 * Also check <a href="https://github.com/JohnLangford/vowpal_wabbit/wiki/Loss-functions">https://github.com/JohnLangford/vowpal_wabbit/wiki/Loss-functions</a> <p>
 *
 */
public enum Link {
    identity, logistic, glf1, poisson
}
