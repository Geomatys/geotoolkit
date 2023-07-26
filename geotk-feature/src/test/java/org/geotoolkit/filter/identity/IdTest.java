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
package org.geotoolkit.filter.identity;

import org.junit.Test;
import org.opengis.filter.ResourceId;

import static org.junit.Assert.*;
import static org.geotoolkit.test.Assertions.assertSerializedEquals;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class IdTest {
    @Test
    public void testFeatureId() {
        String strid = "testFeatureType.1";

        ResourceId id1 = FF.resourceId(strid);
        ResourceId id2 = FF.resourceId(strid);
        assertSerializedEquals(id1); //test serialize

        assertEquals(strid, id1.getIdentifier());
        assertEquals(id1, id2);
    }
}
