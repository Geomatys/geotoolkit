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

import javax.imageio.metadata.IIOMetadataFormat;

import org.junit.*;
import static org.junit.Assert.*;
import static javax.imageio.metadata.IIOMetadataFormat.*;


/**
 * Tests {@link SpatialMetadataFormat}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.04
 *
 * @since 3.04
 */
public final class SpatialMetadataFormatTest {
    /**
     * Tests the default instance.
     */
    @Test
    public void testDefaultInstance() {
        final IIOMetadataFormat format = SpatialMetadataFormat.INSTANCE;
        assertEquals(DATATYPE_BOOLEAN, format.getAttributeDataType("ImageDescription", "cameraCalibrationInformationAvailable"));
        assertEquals(DATATYPE_DOUBLE,  format.getAttributeDataType("ImageDescription", "cloudCoverPercentage"));
        assertEquals(CHILD_POLICY_REPEAT, format.getChildPolicy("dimensions"));
    }
}
