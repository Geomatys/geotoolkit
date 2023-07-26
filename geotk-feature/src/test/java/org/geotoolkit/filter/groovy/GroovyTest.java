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
import org.geotoolkit.filter.FilterTestConstants;
import org.geotoolkit.filter.function.groovy.GroovyFunctionFactory;
import org.geotoolkit.filter.FilterUtilities;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Expression;
import org.opengis.filter.ValueReference;

import static org.junit.Assert.*;
import static org.geotoolkit.test.Assertions.assertSerializedEquals;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GroovyTest {
    @Test
    public void simpleScriptTest() throws ScriptException{
        final FilterFactory ff = FilterUtilities.FF;

        Expression exp = ff.literal("return 2 + 3");
        Expression gvFunction = ff.function(GroovyFunctionFactory.GROOVY, exp);

        double result = ((Number) gvFunction.apply(null)).doubleValue();
        assertTrue(result == 5);

        exp = ff.literal("return $testInteger * $testDouble");
        gvFunction = ff.function(GroovyFunctionFactory.GROOVY, exp);

        result = ((Number) gvFunction.apply(FilterTestConstants.CANDIDATE_1)).doubleValue();
        assertTrue(result == 10201);

        exp = ff.literal("x = $testLong - 6*$testFloat;" +
                         "if(x<0) x = 10;" +
                         "return x;");
        gvFunction = ff.function(GroovyFunctionFactory.GROOVY, exp);
        assertSerializedEquals(gvFunction); //test serialize

        List<Expression> exps = gvFunction.getParameters();
        ValueReference property1 = (ValueReference) exps.get(1);
        ValueReference property2 = (ValueReference) exps.get(2);
        assertTrue(property1.getXPath().equals("testLong"));
        assertTrue(property2.getXPath().equals("testFloat"));


        result = ((Number) gvFunction.apply(FilterTestConstants.CANDIDATE_1)).doubleValue();
        assertTrue(result == 10);
    }

    @Test
    public void complexeScriptTest() throws ScriptException{
        final FilterFactory ff = FilterUtilities.FF;

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
        Expression gvFunction = ff.function(GroovyFunctionFactory.GROOVY, exp);
        assertSerializedEquals(gvFunction); //test serialize

        List<Expression> exps = gvFunction.getParameters();
        ValueReference property1 = (ValueReference) exps.get(1);
        assertTrue(property1.getXPath().equals("testString"));

        Object result = gvFunction.apply(FilterTestConstants.CANDIDATE_1);
        assertTrue("DETE".equals(result));
    }
}
