/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.internal.jaxb.gco;

import java.net.URISyntaxException;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Test {@link Measure}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.17
 *
 * @since 3.10
 */
public final strictfp class MeasureTest {
    /**
     * Tests the {@link Measure#setUOM(String)}.
     *
     * @throws URISyntaxException Should not happen.
     */
    @Test
    public void testSetUOM() throws URISyntaxException {
        final Measure measure = new Measure();
        measure.setUOM("http://schemas.opengis.net/iso/19139/20070417/resources/uom/gmxUom.xml#m");
        assertEquals(SI.METRE, measure.unit);
        assertEquals("http://schemas.opengis.net/iso/19139/20070417/resources/uom/gmxUom.xml#xpointer(//*[@gml:id='m'])", measure.getUOM());

        measure.unit = null;
        measure.setUOM("../uom/ML_gmxUom.xsd#xpointer(//*[@gml:id='deg'])");
        assertEquals(NonSI.DEGREE_ANGLE, measure.unit);
        assertEquals("http://schemas.opengis.net/iso/19139/20070417/resources/uom/gmxUom.xml#xpointer(//*[@gml:id='deg'])", measure.getUOM());

        measure.unit = null;
        measure.setUOM("http://my.big.org/units/kg");
        assertEquals(SI.KILOGRAM, measure.unit);
        assertEquals("http://schemas.opengis.net/iso/19139/20070417/resources/uom/gmxUom.xml#xpointer(//*[@gml:id='kg'])", measure.getUOM());
    }
}
