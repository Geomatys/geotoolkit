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
import org.apache.sis.internal.system.DefaultFactories;
import static org.apache.sis.test.Assert.assertSerializedEquals;
import org.geotoolkit.filter.FilterTestConstants;
import org.geotoolkit.filter.function.lua.LuaFunctionFactory;
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
 * @module
 */
public class LuaTest extends org.geotoolkit.test.TestBase {

    public LuaTest() {
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

        final FilterFactory ff = DefaultFactories.forBuildin(FilterFactory.class);

        Expression exp = ff.literal("return 2 + 3");
        Function luaFunction = ff.function(LuaFunctionFactory.LUA, exp);

        double result = luaFunction.evaluate(null,Number.class).doubleValue();
        assert(result == 5);

        exp = ff.literal("return _testInteger * _testDouble");
        luaFunction = ff.function(LuaFunctionFactory.LUA, exp);

        result = luaFunction.evaluate(FilterTestConstants.CANDIDATE_1,Number.class).doubleValue();
        assert(result == 10201);

        exp = ff.literal("x = _testLong - 6 * _testFloat\n" +
                         "if ( x < 0 ) then \n" +
                         "x = 10\n" +
                         "end\n" +
                         "return x\n");
        luaFunction = ff.function(LuaFunctionFactory.LUA, exp);
        assertSerializedEquals(luaFunction); //test serialize

        List<Expression> exps = luaFunction.getParameters();
        PropertyName property1 = (PropertyName) exps.get(1);
        PropertyName property2 = (PropertyName) exps.get(2);
        assert(property1.getPropertyName().equals("testLong"));
        assert(property2.getPropertyName().equals("testFloat"));


        result = luaFunction.evaluate(FilterTestConstants.CANDIDATE_1, Number.class).doubleValue();
        assert(result == 10);

    }

}
