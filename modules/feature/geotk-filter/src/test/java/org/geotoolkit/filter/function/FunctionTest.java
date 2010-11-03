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


import org.geotoolkit.test.Commons;
import org.geotoolkit.filter.function.other.LengthFunction;
import org.geotoolkit.filter.function.other.OtherFunctionFactory;
import org.junit.Test;

import org.opengis.filter.expression.Function;
import org.opengis.filter.spatial.BBOX;

import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;


/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FunctionTest {

    
    public FunctionTest() {

    }

    @Test
    public void testFunctionLenght() {

        Function f = FF.function(OtherFunctionFactory.EXPRESSION_VALUE_LENGHT, FF.property("."));
        assertTrue(f instanceof LengthFunction);
        Commons.serialize(f); //test serialize

    }

    
}
