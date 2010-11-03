/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.filter.javascript;

import java.util.List;
import javax.script.ScriptException;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.FilterTestConstants;
import org.geotoolkit.filter.function.javascript.JavaScriptFunctionFactory;
import org.geotoolkit.test.Commons;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.PropertyName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JavaScriptTest {

    public JavaScriptTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void simpleScriptTest() throws ScriptException{

        final FilterFactory ff = FactoryFinder.getFilterFactory(null);

        Expression exp = ff.literal("2 + 3");
        Function jsFunction = ff.function(JavaScriptFunctionFactory.JAVASCRIPT, exp);

        double result = jsFunction.evaluate(null,Number.class).doubleValue();
        assert(result == 5);

        exp = ff.literal("$testInteger * $testDouble");
        jsFunction = ff.function(JavaScriptFunctionFactory.JAVASCRIPT, exp);

        result = jsFunction.evaluate(FilterTestConstants.CANDIDATE_1,Number.class).doubleValue();
        assert(result == 10201);

        exp = ff.literal("x = $testLong - 6*$testFloat;" +
                         "if(x<0) x = 10;" +
                         "x;");
        jsFunction = ff.function(JavaScriptFunctionFactory.JAVASCRIPT, exp);
        Commons.serialize(jsFunction); //test serialize

        List<Expression> exps = jsFunction.getParameters();
        PropertyName property1 = (PropertyName) exps.get(1);
        PropertyName property2 = (PropertyName) exps.get(2);
        assert(property1.getPropertyName().equals("testLong"));
        assert(property2.getPropertyName().equals("testFloat"));


        result = jsFunction.evaluate(FilterTestConstants.CANDIDATE_1,Number.class).doubleValue();
        assert(result == 10);

        
    }


}
