/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory;

import java.util.Map;
import java.util.Collections;

import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.test.Depend;
import org.geotoolkit.test.crs.WKT;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests {@link ReferencingFactoryContainer}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
@Depend(ReferencingObjectFactoryTest.class)
public final class ReferencingFactoryContainerTest {
    /**
     * Tests {@link ReferencingFactoryContainer#toGeodetic3D}.
     *
     * @throws FactoryException Should never happen.
     */
    @Test
    public void testToGeodetic3D() throws FactoryException {
        final ReferencingFactoryContainer factories = ReferencingFactoryContainer.instance(null);
        final CRSFactory crsFactory = factories.getCRSFactory();
        CoordinateReferenceSystem crs = crsFactory.createFromWKT(WKT.PROJCS_LAMBERT_CONIC_NTF);
        crs = crsFactory.createCompoundCRS(name("NTF 3D"), crs, DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT);
        crs = crsFactory.createCompoundCRS(name("NTF 4D"), crs, DefaultTemporalCRS.MODIFIED_JULIAN);
        final CoordinateReferenceSystem result = factories.toGeodetic3D((CompoundCRS) crs);
        assertNotSame("Expected a new CRS.", crs, result);
//      System.out.println(result);
    }

    /**
     * Returns the given name in a map, for convenience.
     */
    private static Map<String,String> name(final String name) {
        return Collections.singletonMap(CoordinateReferenceSystem.NAME_KEY, name);
    }
}
