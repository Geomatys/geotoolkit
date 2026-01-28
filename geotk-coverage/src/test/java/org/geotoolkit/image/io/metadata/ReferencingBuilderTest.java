/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.io.metadata;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.util.FactoryException;

import org.geotoolkit.test.referencing.WKT;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.crs.DefaultProjectedCRS;
import org.geotoolkit.referencing.cs.PredefinedCS;
import org.geotoolkit.test.LocaleDependantTestBase;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.util.Utilities;
import org.junit.*;

import static org.junit.Assert.*;
import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Tests the {@link ReferencingBuilder} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final class ReferencingBuilderTest extends LocaleDependantTestBase {
    /**
     * Tests if the two given objects are equal, ignoring metadata.
     */
    private static void assertEqualsIgnoreMetadata(final String message,
            final IdentifiedObject object1, final IdentifiedObject object2)
    {
        assertTrue(message, Utilities.equalsIgnoreMetadata(object1, object2));
    }

    /**
     * Tests the parsing of a Mercator CRS.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testParseProjectedCRS() throws FactoryException {
        /*
         * Following should have been tested by testFormatProjectedCRS()
         */
        final ProjectedCRS originalCRS = (ProjectedCRS) CRS.fromWKT(WKT.PROJCS_MERCATOR);
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.getImageInstance(GEOTK_FORMAT_NAME));
        final ReferencingBuilder builder = new ReferencingBuilder(metadata);
        builder.setCoordinateReferenceSystem(originalCRS);
        /*
         * Following is the purpose of this test suite.
         */
        CoordinateReferenceSystem crs = builder.build();
        assertEquals(DefaultProjectedCRS.class, crs.getClass());
        GeodeticDatum datum = ((ProjectedCRS) crs).getDatum();

        assertSame(originalCRS, crs);
        assertSame(originalCRS.getDatum(), datum);
        assertSame(originalCRS.getCoordinateSystem(), builder.getCoordinateSystem(CoordinateSystem.class));
        assertSame(originalCRS.getDatum(),            builder.getDatum(Datum.class));

        builder.setIgnoreUserObject(true);
        crs = builder.build();
        assertEquals(DefaultProjectedCRS.class, crs.getClass());
        datum = ((ProjectedCRS) crs).getDatum();

        assertNotSame(originalCRS, crs);
        assertNotSame(originalCRS.getCoordinateSystem(), builder.getCoordinateSystem(CoordinateSystem.class));
        assertNotSame(originalCRS.getDatum(),            builder.getDatum(Datum.class));

        assertEqualsIgnoreMetadata("PrimeMeridian", CommonCRS.WGS84.primeMeridian(), datum.getPrimeMeridian());
        assertEqualsIgnoreMetadata("Ellipsoid",     CommonCRS.WGS84.ellipsoid(),     datum.getEllipsoid());
        assertEqualsIgnoreMetadata("CS",            PredefinedCS.PROJECTED, crs.getCoordinateSystem());
    }
}
