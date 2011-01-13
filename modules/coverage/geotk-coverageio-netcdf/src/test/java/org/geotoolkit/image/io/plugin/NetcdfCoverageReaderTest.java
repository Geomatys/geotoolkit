/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.image.io.plugin;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.CRS;

import org.junit.*;
import static org.junit.Assert.*;
import org.opengis.referencing.operation.TransformException;


/**
 * Tests {@link NetcdfImageReader} wrapped in a {@link ImageCoverageReader}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.15
 *
 * @since 3.11
 */
public final class NetcdfCoverageReaderTest extends NetcdfTestBase {
    /**
     * Tests a {@link ImageCoverageReader#read} operation.
     *
     * @throws CoverageStoreException If an error occurred while reading the NetCDF file.
     * @throws TransformException Should not occur.
     */
    @Test
    public void testRead() throws CoverageStoreException, TransformException {
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(getTestFile());
        assertArrayEquals(VARIABLE_NAMES, reader.getCoverageNames().toArray());
        final GridCoverage2D coverage = reader.read(0, null);
        assertNotNull(coverage);
        reader.dispose();
        /*
         * Verify the grid coverage.
         */
        CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem();
        assertEquals(4, crs.getCoordinateSystem().getDimension());
        assertTrue(crs instanceof CompoundCRS);
        crs = ((CompoundCRS) crs).getComponents().get(0);
        assertTrue(crs instanceof ProjectedCRS);
        /*
         * Verify the envelope.
         */
        Envelope envelope = coverage.getEnvelope();
        assertEquals(-19987288, envelope.getMinimum(0), 1);
        assertEquals(-13815969, envelope.getMinimum(1), 1);
        envelope = CRS.transform(envelope, DefaultGeographicCRS.SPHERE);
        /*
         * Note: Coriolis data have a 0.25° offset in longitude. This is a known
         * problem of the tested data, not a problem of the Geotk library.
         */
        assertEquals(-179.750, envelope.getMinimum(0), 1E-10);
        assertEquals( 180.250, envelope.getMaximum(0), 1E-10);
        assertEquals( -76.954, envelope.getMinimum(1), 1E-3);
        assertEquals(  77.178, envelope.getMaximum(1), 1E-3);
    }
}
