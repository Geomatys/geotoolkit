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

import java.util.Locale;
import java.io.Writer;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import javax.imageio.metadata.IIOMetadataFormat;
import org.opengis.metadata.Identifier;

import org.junit.*;
import static org.junit.Assert.*;
import static javax.imageio.metadata.IIOMetadataFormat.*;


/**
 * Tests {@link SpatialMetadataFormat}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.05
 *
 * @since 3.04
 */
public final class SpatialMetadataFormatTest {
    /**
     * Tests the elements in the image metadata format instance.
     */
    @Test
    public void testImageElements() {
        final IIOMetadataFormat format = SpatialMetadataFormat.IMAGE;
        assertEquals(Identifier.class, format.getObjectClass("processingLevelCode"));
    }

    /**
     * Tests the attributes in the image metadata format instance.
     */
    @Test
    public void testImageAttributes() {
        final IIOMetadataFormat format = SpatialMetadataFormat.IMAGE;
        assertEquals(DATATYPE_BOOLEAN,    format.getAttributeDataType("ImageDescription", "cameraCalibrationInformationAvailable"));
        assertEquals(DATATYPE_DOUBLE,     format.getAttributeDataType("ImageDescription", "cloudCoverPercentage"));
        assertEquals(CHILD_POLICY_REPEAT, format.getChildPolicy("dimensions"));
    }

    /**
     * Tests the descriptions in the image metadata format instance.
     */
    @Test
    public void testImageDescriptions() {
        final IIOMetadataFormat format = SpatialMetadataFormat.IMAGE;
        assertEquals("Image distributor's code that identifies the level of radiometric and geometric processing that has been applied.",
                format.getElementDescription("processingLevelCode", Locale.ENGLISH));
        assertEquals("Area of the dataset obscured by clouds, expressed as a percentage of the spatial extent.",
                format.getAttributeDescription("ImageDescription", "cloudCoverPercentage", Locale.ENGLISH));
    }

    /**
     * Tests the {@link SpatialMetadataFormat#toString()} method.
     * This is also used for producing the tree to copy in the javadoc.
     *
     * @throws IOException If an I/O error occured while writting the tree to disk.
     *         This is normally not enabled.
     */
    @Test
    public void testToString() throws IOException {
        final String stream = SpatialMetadataFormat.STREAM.toString();
        final String image  = SpatialMetadataFormat.IMAGE .toString();
        assertTrue(stream.length() != 0); // Dummy check. The real interresting part is the write to a file.
        assertTrue(image .length() != 0);
        if (false) {
            final Writer out = new OutputStreamWriter(new FileOutputStream("SpatialMetadataFormat.txt"), "UTF-8");
            out.write(stream);
            out.write(System.getProperty("line.separator", "\n"));
            out.write(image);
            out.close();
        }
    }
}
