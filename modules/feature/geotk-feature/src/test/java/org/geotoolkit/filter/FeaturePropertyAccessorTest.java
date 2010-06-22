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
package org.geotoolkit.filter;

import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.geotoolkit.filter.accessor.Accessors;
import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.accessor.Accessors;
import org.geotoolkit.filter.accessor.PropertyAccessor;
import org.junit.Test;
import org.opengis.feature.Feature;

import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class FeaturePropertyAccessorTest {

    public FeaturePropertyAccessorTest() {
    }

    @Test
    public void testAccessor() {

        //test a simple attribut------------------------------------------------
        PropertyAccessor accessor = Accessors.getAccessor(Feature.class, "testGeometry", null);
        assertNotNull(accessor);
        Geometry geom = (Geometry) accessor.get(FEATURE_1, "testGeometry", Geometry.class);
        assertEquals(geom, FEATURE_1.getDefaultGeometryProperty().getValue());

        //test id---------------------------------------------------------------
        accessor = Accessors.getAccessor(Feature.class, "@id", null);
        assertNotNull(accessor);
        Object id = accessor.get(FEATURE_1, "@id", null);
        assertEquals(id, FEATURE_1.getIdentifier().getID());

        //test xpath index------------------------------------------------------
        accessor = Accessors.getAccessor(Feature.class, "*[10]", null);
        assertNotNull(accessor);
        Object att = accessor.get(FEATURE_1, "*[10]", null);
        assertEquals(att, "test string data");
        assertEquals(att, FEATURE_1.getProperty("testString").getValue());

        //test a geometry name with accents-------------------------------------
        accessor = Accessors.getAccessor(Feature.class, "attribut.Géométrie", null);
        assertNotNull(accessor);
        att = accessor.get(FEATURE_1, "attribut.Géométrie", null);
        assertEquals(att, "POINT(45,32)");
        assertEquals(att, FEATURE_1.getProperty("attribut.Géométrie").getValue());

    }

}
