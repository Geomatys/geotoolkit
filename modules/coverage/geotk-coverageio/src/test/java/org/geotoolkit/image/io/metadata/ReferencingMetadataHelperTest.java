/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.FactoryException;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.internal.image.io.CRSAccessor;
import org.geotoolkit.internal.image.io.CRSAccessorTest;
import org.geotoolkit.test.Depend;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link ReferencingMetadataHelper} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
@Depend(CRSAccessorTest.class)
public final class ReferencingMetadataHelperTest {
    /**
     * Tests if the two given objects are equal, ignoring metadata.
     */
    private static void assertEqualsIgnoreMetadata(final String message,
            final IdentifiedObject object1, final IdentifiedObject object2)
    {
        assertTrue(message, CRS.equalsIgnoreMetadata(object1, object2));
    }

    /**
     * Tests the parsing of the WGS84 CRS.
     *
     * @throws FactoryException Should not happen.
     */
    @Test
    public void testGeographicCRS() throws FactoryException {
        /*
         * Following should have been tested by CRSAccessorTest.testGeographicCRS()
         */
        final SpatialMetadata metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final CRSAccessor accessor = new CRSAccessor(metadata);
        accessor.setCRS(DefaultGeographicCRS.WGS84);
        /*
         * Following is the purpose of this test suite.
         */
        final ReferencingMetadataHelper helper = new ReferencingMetadataHelper(metadata);
        final CoordinateReferenceSystem crs = helper.getCoordinateReferenceSystem();
        assertNotSame("Should have created a new CRS.", DefaultGeographicCRS.WGS84, crs);
        assertEquals(DefaultGeographicCRS.class, crs.getClass());
        final GeodeticDatum datum = ((GeographicCRS) crs).getDatum();
        assertEqualsIgnoreMetadata("PrimeMeridian", DefaultPrimeMeridian.GREENWICH,   datum.getPrimeMeridian());
        assertEqualsIgnoreMetadata("Ellipsoid",     DefaultEllipsoid    .WGS84,       datum.getEllipsoid());
        assertEqualsIgnoreMetadata("Datum",         DefaultGeodeticDatum.WGS84,       datum);
        assertEqualsIgnoreMetadata("CS",            DefaultEllipsoidalCS.GEODETIC_2D, crs.getCoordinateSystem());
        assertEqualsIgnoreMetadata("CRS",           DefaultGeographicCRS.WGS84,       crs);
    }
}
