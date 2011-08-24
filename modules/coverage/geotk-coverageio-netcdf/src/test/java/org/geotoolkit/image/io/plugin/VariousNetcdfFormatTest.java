/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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

import java.net.URI;
import java.util.List;
import java.io.File;
import java.io.IOException;

import org.geotoolkit.test.image.ImageTestBase;
import org.geotoolkit.image.io.metadata.SpatialMetadata;

import org.junit.*;
import static org.geotoolkit.test.Assert.*;


/**
 * Tests {@link NetcdfImageReader} with various formats.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19 (derived from 3.16)
 */
public final strictfp class VariousNetcdfFormatTest extends ImageTestBase {
    /**
     * Creates a new test suite.
     */
    public VariousNetcdfFormatTest() {
        super(NetcdfImageReader.class);
    }

    /**
     * Returns the filename of the given aggregated URI. We omit the parent
     * directory because they are platform-dependent.
     */
    private static String[] filenames(final List<URI> aggregated) {
        assertNotNull("Expected aggregated NetCDF files.", aggregated);
        final String[] filenames = new String[aggregated.size()];
        for (int i=0; i<filenames.length; i++) {
            filenames[i] = new File(aggregated.get(i).getPath()).getName();
        }
        return filenames;
    }

    /**
     * Tests reading a NcML file.
     *
     * @throws IOException if an error occurred while reading the file.
     */
    @Test
    public void testNcML() throws IOException {
        final NetcdfImageReader reader = new NetcdfImageReader(null);
        reader.setInput(getLocallyInstalledFile(NetcdfTestBase.DIRECTORY + "Aggregation.ncml"));
        assertEquals("Unexpected number of variables.",  4, reader.getNumImages(true));
        assertEquals("Expected only 1 band by default.", 1, reader.getNumBands(0));
        assertArrayEquals("Expected the names of the variables found in the NcML file.",
                new String[] { // Note that "pct_variance" variables are renamed in the NcML file.
                    "temperature", "temperature_pct_variance",
                    "salinity",    "salinity_pct_variance"},
                reader.getImageNames().toArray());
        /*
         * Test the paths to the file components for the "temperature" variable.
         */
        assertArrayEquals(new String[] {
                "OA_RTQCGL01_20070606_FLD_TEMP.nc",
                "OA_RTQCGL01_20070613_FLD_TEMP.nc",
                "OA_RTQCGL01_20070620_FLD_TEMP.nc"
            }, filenames(reader.getAggregatedFiles(0)));
        /*
         * Test the paths to the file components for the "salinity_pct_variance" variable.
         */
        assertArrayEquals(new String[] {
                "OA_RTQCGL01_20070606_FLD_PSAL.nc",
                "OA_RTQCGL01_20070613_FLD_PSAL.nc",
                "OA_RTQCGL01_20070620_FLD_PSAL.nc"
            }, filenames(reader.getAggregatedFiles(3)));
        reader.dispose();
    }

    /**
     * Tests reading a Landsat file converted to NetCDF by GDAL.
     *
     * @throws IOException if an error occurred while reading the file.
     */
    @Test
    public void testLandsat() throws IOException {
        final NetcdfImageReader reader = new NetcdfImageReader(null);
        reader.setInput(getLocallyInstalledFile("LANDSAT/dem20_r500_3112.nc"));
        final SpatialMetadata metadata = reader.getImageMetadata(0);
        // System.out.println(metadata);
        reader.dispose();
    }
}
