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
import org.geotoolkit.filter.FilterTestConstants;
import org.geotoolkit.filter.function.javascript.JavaScriptFunctionFactory;
import org.geotoolkit.filter.FilterUtilities;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Expression;
import org.opengis.filter.ValueReference;

import static org.junit.Assert.*;
import static org.apache.sis.test.Assertions.assertSerializedEquals;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JavaScriptTest extends org.geotoolkit.test.TestBase {
    @Test
    public void simpleScriptTest() throws ScriptException {
        final FilterFactory ff = FilterUtilities.FF;

        Expression exp = ff.literal("2 + 3");
        Expression jsFunction = ff.function(JavaScriptFunctionFactory.JAVASCRIPT, exp);

        double result = ((Number) jsFunction.apply(null)).doubleValue();
        assertTrue(result == 5);

        exp = ff.literal("$testInteger * $testDouble");
        jsFunction = ff.function(JavaScriptFunctionFactory.JAVASCRIPT, exp);

        result = ((Number) jsFunction.apply(FilterTestConstants.CANDIDATE_1)).doubleValue();
        assertTrue(result == 10201);

        exp = ff.literal("x = $testLong - 6*$testFloat;" +
                         "if(x<0) x = 10;" +
                         "x;");
        jsFunction = ff.function(JavaScriptFunctionFactory.JAVASCRIPT, exp);
        assertSerializedEquals(jsFunction); //test serialize

        List<Expression> exps = jsFunction.getParameters();
        ValueReference property1 = (ValueReference) exps.get(1);
        ValueReference property2 = (ValueReference) exps.get(2);
        assertTrue(property1.getXPath().equals("testLong"));
        assertTrue(property2.getXPath().equals("testFloat"));

        result = ((Number) jsFunction.apply(FilterTestConstants.CANDIDATE_1)).doubleValue();
        assertTrue(result == 10);
    }
}
