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

package org.geotoolkit.filter.groovy;

import java.util.List;
import javax.script.ScriptException;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.FilterTestConstants;
import org.geotoolkit.filter.function.groovy.GroovyFunctionFactory;
import static org.geotoolkit.test.Assert.*;

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
public class GroovyTest extends org.geotoolkit.test.TestBase {

    public GroovyTest() {
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

        Expression exp = ff.literal("return 2 + 3");
        Function gvFunction = ff.function(GroovyFunctionFactory.GROOVY, exp);

        double result = gvFunction.evaluate(null,Number.class).doubleValue();
        assert(result == 5);

        exp = ff.literal("return $testInteger * $testDouble");
        gvFunction = ff.function(GroovyFunctionFactory.GROOVY, exp);

        result = gvFunction.evaluate(FilterTestConstants.CANDIDATE_1,Number.class).doubleValue();
        assert(result == 10201);

        exp = ff.literal("x = $testLong - 6*$testFloat;" +
                         "if(x<0) x = 10;" +
                         "return x;");
        gvFunction = ff.function(GroovyFunctionFactory.GROOVY, exp);
        assertSerializedEquals(gvFunction); //test serialize

        List<Expression> exps = gvFunction.getParameters();
        PropertyName property1 = (PropertyName) exps.get(1);
        PropertyName property2 = (PropertyName) exps.get(2);
        assert(property1.getPropertyName().equals("testLong"));
        assert(property2.getPropertyName().equals("testFloat"));


        result = gvFunction.evaluate(FilterTestConstants.CANDIDATE_1,Number.class).doubleValue();
        assert(result == 10);


    }

    @Test
    public void complexeScriptTest() throws ScriptException{

        final FilterFactory ff = FactoryFinder.getFilterFactory(null);

        Expression exp = ff.literal(
                  "x = $testString.split(\" \")[2];\n"
                + "y = upperAndReplace(x);\n"
                + "return y;\n"
                + "\n"
                + "def upperAndReplace(str){\n"
                + " str = str.replaceAll(\"a\",\"e\");\n"
                + " str = str.toUpperCase();\n"
                + " return str;\n"
                + "}\n");
        Function gvFunction = ff.function(GroovyFunctionFactory.GROOVY, exp);
        assertSerializedEquals(gvFunction); //test serialize

        List<Expression> exps = gvFunction.getParameters();
        PropertyName property1 = (PropertyName) exps.get(1);
        assert(property1.getPropertyName().equals("testString"));


        Object result = gvFunction.evaluate(FilterTestConstants.CANDIDATE_1);
        assert("DETE".equals(result));
    }

}
