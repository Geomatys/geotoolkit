/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.image.io.mosaic;

import java.awt.geom.AffineTransform;
import java.io.IOException;

import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.Commons;
import org.geotoolkit.test.TestData;
import org.geotoolkit.test.image.ImageTestBase;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * Tests the {@link TileManagerFactory} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.18
 *
 * @since 3.18
 */
@DependsOn(TileManagerTest.class)
public final strictfp class TileManagerFactoryTest extends ImageTestBase {
    /**
     * Creates a new test suite.
     */
    public TileManagerFactoryTest() {
        super(TileManagerFactory.class);
    }

    /**
     * Creates a tile manager by scanning the content of a directory.
     *
     * @throws IOException If an error occurred while reading a PGW file.
     */
    @Test
    public void testCreateFromDirectory() throws IOException {
        final TileManager[] managers = TileManagerFactory.DEFAULT.create(TestData.file(this, null));
        assertEquals(1, managers.length);
        final AffineTransform gridToCRS = new AffineTransform(1, 0, 0, -1, -180, 90);
        for (final Tile tile : managers[0].getTiles()) {
            assertEquals(gridToCRS, tile.getGridToCRS());
        }
        /*
         * Reads the image. This allow a visual check ensuring that the tiles
         * are properly assembled.
         */
        final MosaicImageReader reader = new MosaicImageReader();
        reader.setInput(managers);
        image = reader.read(0);
        reader.dispose();
        assertEquals(1800014439L, Commons.checksum(image));
        showCurrentImage("testCreateFromDirectory()");
    }
}
