/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.style;

import java.awt.Color;
import org.opengis.filter.expression.Literal;
import org.apache.sis.util.ObjectConverters;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ColorTest extends org.geotoolkit.test.TestBase {

    public ColorTest() {
    }

    @Test
    public void testColor(){

        final String str = "#D2787034";
        final Color result = ObjectConverters.convert(str, Color.class);
        assertNotNull(result);

        final Color c = new Color(120,112,52,210);
        final MutableStyleFactory SF = new DefaultStyleFactory();
        final Literal l = SF.literal(c);

        assertTrue(l.getValue() instanceof Color);
        final Color res = l.evaluate(null, Color.class);
        assertEquals(c, res);

    }


}
