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

import org.opengis.metadata.content.ImageDescription;
import org.geotoolkit.test.Depend;
import org.junit.*;

import static org.junit.Assert.*;


/**
 * Tests {@link MetadataProxy}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.06
 *
 * @since 3.06
 */
@Depend(MetadataAccessorTest.class)
public final class MetadataProxyTest {
    /**
     * Tests the proxy with some properties defined under the {@code "ImageDescription"} node
     * without children.
     */
    @Test
    public void testImageDescription() {
        final SpatialMetadata  metadata = new SpatialMetadata(SpatialMetadataFormat.IMAGE);
        final MetadataAccessor accessor = new MetadataAccessor(metadata, "ImageDescription", null);
        final ImageDescription proxy    = MetadataProxy.newProxyInstance(ImageDescription.class, accessor, -1);
        /*
         * Test the properties before they are defined.
         */
        assertNull(proxy.getImagingCondition());
        assertNull(proxy.getCloudCoverPercentage());
        /*
         * Now define the properties and test again.
         */
        accessor.setAttributeAsString("imagingCondition", "cloud");
        accessor.setAttributeAsDouble("cloudCoverPercentage", 20);
        assertEquals(20.0, proxy.getCloudCoverPercentage(), 0.0);
    }
}
