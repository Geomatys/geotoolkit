/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import ucar.nc2.NetcdfFile;
import ucar.nc2.ncml.NcMLReader;

import org.opengis.metadata.Metadata;
import org.opengis.metadata.spatial.Dimension;
import org.opengis.metadata.spatial.GridSpatialRepresentation;

import org.geotoolkit.test.TestData;
import org.geotoolkit.image.io.plugin.NetcdfImageReader;

import org.junit.*;
import static org.junit.Assert.*;
import static org.geotoolkit.test.Commons.getSingleton;


/**
 * Tests using the {@link NetcdfISO} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class NetcdfISOTest {
    /**
     * Tests a file that contains THREDDS metadata.
     *
     * @throws IOException If the test file can not be read.
     */
    @Test
    public void testTHREDDS() throws IOException {
        final Metadata metadata;
        try (InputStream in = TestData.openStream(NetcdfImageReader.class, "thredds.ncml")) {
            final NetcdfFile file = NcMLReader.readNcML(in, null);
            final NetcdfISO ncISO = new NetcdfISO(file, null);
            metadata = ncISO.createMetadata();
            file.close();
        }
        assertEquals("crm_v1", metadata.getFileIdentifier());
        assertEquals("David Neufeld", getSingleton(metadata.getContacts()).getIndividualName());

        final GridSpatialRepresentation spatial = (GridSpatialRepresentation) getSingleton(metadata.getSpatialRepresentationInfo());
        final List<? extends Dimension> axis = spatial.getAxisDimensionProperties();
        assertEquals(Integer.valueOf(2), spatial.getNumberOfDimensions());
        assertEquals(2, axis.size());
        assertEquals(Integer.valueOf(19201), axis.get(0).getDimensionSize());
        assertEquals(Integer.valueOf( 9601), axis.get(1).getDimensionSize());
        assertEquals(Double .valueOf(8.332899328159992E-4), axis.get(0).getResolution());
        assertEquals(Double .valueOf(8.332465368190813E-4), axis.get(1).getResolution());
    }
}
