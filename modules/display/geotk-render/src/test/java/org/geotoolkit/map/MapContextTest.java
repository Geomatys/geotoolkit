/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.map;


import junit.framework.TestCase;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.test.referencing.WKT;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

import java.io.IOException;

/**
 * Test MapContext creation and behavior.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class MapContextTest extends TestCase {

    @Test
    public void testContextCreation() throws FactoryException {

        try {
            MapBuilder.createContext(null);
            fail("Creating MapContext with null CRS should raise an error");
        }catch(Exception ex){
            //ok
        }

        //default MapContext
        MapContext context = MapBuilder.createContext();
        assertNotNull(context);
        assertTrue(CRS.equalsIgnoreMetadata(CommonCRS.WGS84.defaultGeographic(), context.getCoordinateReferenceSystem()));


        // WGS72 MapContext
        final CoordinateReferenceSystem wgs72 = CommonCRS.WGS72.defaultGeographic();
        context = MapBuilder.createContext(wgs72);
        assertNotNull(context);
        assertTrue(CRS.equalsIgnoreMetadata(wgs72, context.getCoordinateReferenceSystem()));
    }

    @Test
    public void testMapContextBounds() throws FactoryException, IOException {

        final CoordinateReferenceSystem wgs72 = CommonCRS.WGS72.defaultGeographic();
        MutableStyle defaultStyle = new DefaultStyleFactory().style();

        //Test layers in same CRS
        MapContext context = MapBuilder.createContext();
        GeneralEnvelope envWGS84 = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        envWGS84.setRange(0, -10, 10);
        envWGS84.setRange(1, -10, 10);
        context.layers().add(new MockMapLayer(defaultStyle, envWGS84, true));

        GeneralEnvelope envWGS842 = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        envWGS842.setRange(0, 10, 20);
        envWGS842.setRange(1, 10, 20);
        context.layers().add(new MockMapLayer(defaultStyle, envWGS842, false));

        //all layer
        Envelope ctxBounds = context.getBounds(false);
        assertNotNull(ctxBounds);
        GeneralEnvelope expected = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        expected.setRange(0, -10, 20);
        expected.setRange(1, -10, 20);
        assertTrue(expected.equals(ctxBounds, 0.00001, true));

        //only visible
        ctxBounds = context.getBounds(true);
        assertNotNull(ctxBounds);
        expected = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        expected.setRange(0, -10, 10);
        expected.setRange(1, -10, 10);
        assertTrue(expected.equals(ctxBounds, 0.00001, true));


        // test layer in different CRS than Context
        context = MapBuilder.createContext();

        CoordinateReferenceSystem mercator = CRS.parseWKT(WKT.PROJCS_MERCATOR);
        GeneralEnvelope env1 = new GeneralEnvelope(mercator);
        env1.setRange(0, -10000, 10000);
        env1.setRange(1, -10000, 10000);
        context.layers().add(new MockMapLayer(defaultStyle, env1, true));

        CoordinateReferenceSystem lambert = CRS.parseWKT(WKT.PROJCS_LAMBERT_CONIC_NAD83);
        GeneralEnvelope env2 = new GeneralEnvelope(lambert);
        env2.setRange(0, -10, 10);
        env2.setRange(1, -10, 10);
        context.layers().add(new MockMapLayer(defaultStyle, env2, true));

        ctxBounds = context.getBounds(true);
        assertNotNull(ctxBounds);
        expected = new GeneralEnvelope(CommonCRS.WGS84.defaultGeographic());
        expected.setRange(0, -73.651, -0.090);
        expected.setRange(1, -0.089, 34.244);
        assertTrue(expected.equals(ctxBounds, 0.01, true));

        // empty layer bounds equals to CRS validity domain
        context = MapBuilder.createContext();
        ctxBounds = context.getBounds(true);
        assertNotNull(ctxBounds);
        expected = new GeneralEnvelope(CRS.getEnvelope(CommonCRS.WGS84.defaultGeographic()));
        assertTrue(expected.equals(ctxBounds, 0.0000001, true));

    }

    private class MockMapLayer extends AbstractMapLayer {

        private Envelope bounds;

        protected MockMapLayer(MutableStyle style, Envelope bounds, boolean visible) {
            super(style);
            this.bounds = bounds;
            this.visible =visible;
        }

        @Override
        public Envelope getBounds() {
            return bounds;
        }
    }


}
