/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.function;


import org.junit.Test;

import org.opengis.filter.expression.Function;

import static org.geotoolkit.test.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;
import org.geotoolkit.filter.function.string.LengthFunction;
import org.geotoolkit.filter.function.string.StringFunctionFactory;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FunctionTest extends org.geotoolkit.test.TestBase {


    public FunctionTest() {

    }

    @Test
    public void testFunctionLenght() {

        Function f = FF.function(StringFunctionFactory.LENGTH, FF.property("."));
        assertInstanceOf("EXPRESSION_VALUE_LENGHT", LengthFunction.class, f);
        assertSerializedEquals(f); //test serialize

    }


}
