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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.geotoolkit.test.Assertions.assertSerializedEquals;
import static org.geotoolkit.filter.FilterTestConstants.*;
import org.geotoolkit.filter.function.string.LengthFunction;
import org.geotoolkit.filter.function.string.StringFunctionFactory;
import org.opengis.filter.Expression;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FunctionTest {
    @Test
    public void testFunctionLenght() {
        Expression f = FF.function(StringFunctionFactory.LENGTH, FF.property("."));
        assertInstanceOf(LengthFunction.class, f, "EXPRESSION_VALUE_LENGHT");
        assertSerializedEquals(f); //test serialize
    }
}
