package com.indeed.vw.wrapper.api;

import com.indeed.vw.wrapper.api.ExampleBuilder.NamespaceBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ExampleBuilderTest {

    @Test
    public void testBuildExample1() {
        ExampleBuilder example1 = ExampleBuilder.create()
                .label(3.2)
                .exampleImportance(2)
                .exampleTag("wewerewf");
        example1.createNamespace("states")
                .namespaceWeight(2.5)
                .addCategoricalFeature("tx")
                .addCategoricalFeature("ca");
        example1.createNamespace("job_description")
                .addTextAsFeatures(
                        "||We are Korea’s largest online: retailer\n and our\n goal is\n to create ");
        example1.createNamespace("statistcs")
                .addNumericalFeature("min", -2.2)
                .addNumericalFeature("median", 0.9)
                .addNumericalFeature("max", 4);
        String expected = "1.0 2.0 'wewerewf" +
                "|states:2.5 tx ca " +
                "|job_description  We are Korea’s largest online retailer and our goal is to create " +
                "|statistcs min:-2.2 median:0.9 max:4.0\n";
        assertEquals(expected, example1.toString());
    }

    @Test
    public void testBuildExample2() {
        final ExampleBuilder exampleBuilder = ExampleBuilder.create();
        exampleBuilder.label(-1);
        exampleBuilder
                .createNamespace("user_id")
                .addCategoricalFeature("uid123");
        exampleBuilder
                .createNamespace("ad_id")
                .addCategoricalFeature("ad123");
        exampleBuilder
                .createNamespace("query")
                .addTextAsFeatures("query word and so on");
        System.out.println(exampleBuilder);
        assertEquals(
                "-1.0 |user_id uid123 |ad_id ad123 |query query word and so on",
                exampleBuilder.toString());
    }
}