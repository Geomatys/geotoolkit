/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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

package org.geotoolkit.filter.lua;

import java.util.List;
import javax.script.ScriptException;

import org.geotoolkit.filter.FilterTestConstants;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.filter.function.lua.LuaFunctionFactory;
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
public class LuaTest {
    @Test
    public void simpleScriptTest() throws ScriptException{

        final FilterFactory ff = FilterUtilities.FF;

        Expression exp = ff.literal("return 2 + 3");
        Expression luaFunction = ff.function(LuaFunctionFactory.LUA, exp);

        double result = ((Number) luaFunction.apply(null)).doubleValue();
        assertTrue(result == 5);

        exp = ff.literal("return _testInteger * _testDouble");
        luaFunction = ff.function(LuaFunctionFactory.LUA, exp);

        result = ((Number) luaFunction.apply(FilterTestConstants.CANDIDATE_1)).doubleValue();
        assertTrue(result == 10201);

        exp = ff.literal("x = _testLong - 6 * _testFloat\n" +
                         "if ( x < 0 ) then \n" +
                         "x = 10\n" +
                         "end\n" +
                         "return x\n");
        luaFunction = ff.function(LuaFunctionFactory.LUA, exp);
        assertSerializedEquals(luaFunction); //test serialize

        List<Expression> exps = luaFunction.getParameters();
        ValueReference property1 = (ValueReference) exps.get(1);
        ValueReference property2 = (ValueReference) exps.get(2);
        assertTrue(property1.getXPath().equals("testLong"));
        assertTrue(property2.getXPath().equals("testFloat"));

        result = ((Number) luaFunction.apply(FilterTestConstants.CANDIDATE_1)).doubleValue();
        assertTrue(result == 10);
    }
}
