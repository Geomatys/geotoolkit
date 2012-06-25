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
package org.geotoolkit.image.io.plugin;

import java.io.IOException;
import java.awt.geom.Point2D;

import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.wrapper.netcdf.IOTestCase;

import org.geotoolkit.image.io.metadata.SpatialMetadata;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.geometry.Envelope2D;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests reading a Landsat file converted to NetCDF by GDAL.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.19 (derived from 3.16)
 */
public final strictfp class GDALFormatTest extends NetcdfImageReaderTestBase {
    /**
     * Creates a reader and initializes its input to the test file defined in
     * {@link #getTestFile()}. This method is invoked by each tests inherited
     * from the parent class, and by the tests defined in this class.
     */
    @Override
    protected void prepareImageReader(final boolean setInput) throws IOException {
        if (reader == null) {
            reader = new NetcdfImageReader(null);
        }
        if (setInput) {
            reader.setInput(open(IOTestCase.LANDSAT));
        }
    }

    /**
     * Tests the grid geometry (envelope, offset vectors and origin).
     *
     * @throws IOException if an error occurred while reading the file.
     * @throws CoverageStoreException Should never happen.
     */
    @Test
    public void testGridGeometry() throws IOException, CoverageStoreException {
        prepareImageReader(true);
        final NetcdfImageReader reader = (NetcdfImageReader) this.reader;
        final SpatialMetadata metadata = reader.getImageMetadata(0);
        final String asTree = metadata.toString();
        /*
         * Tests only some metadata element (we don't test the full tree).
         * TODO: investigate why we have slight lost of precision in the
         * origin attribute.
         */
        assertTrue(asTree, asTree.contains("origin=\"1054928.89019874")); // RectifiedGridDomain
        assertTrue(asTree, asTree.contains("values=\"30.106"));           // Offset vector 1
        assertTrue(asTree, asTree.contains("values=\"0.0 -30.106"));      // Offset vector 2
        assertTrue(asTree, asTree.contains("name=\"GDA94 / Geoscience Australia Lambert\"")); // CRS
        /*
         * Tests integration with CoverageReader, basically ensuring that it can
         * create the grid geometry and that the envelope is raisonable.
         */
        final ImageCoverageReader cr = new ImageCoverageReader();
        cr.setInput(reader);
        final GridGeometry2D grid = cr.getGridGeometry(0);
        final Envelope2D envelope = grid.getEnvelope2D();
        assertTrue(envelope.contains(new Point2D.Double(1058000, -4220000)));
        assertTrue(grid.getCoordinateReferenceSystem2D() instanceof ProjectedCRS);
        cr.dispose(); // Dispose also the NetcdfImageReader.
    }
}
