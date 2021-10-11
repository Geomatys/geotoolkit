/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.image.io.plugin.TiffReader;

import javax.imageio.ImageWriteParam;
import java.io.IOException;

/**
 * Effectuate all tests from {@link TestTiffImageReaderWriter}
 * without compression and with random tile dimensions.
 *
 * @author Remi Marechal (Geomatys).
 */
public class UncompressedTiledReaderTest extends UncompressedTiffReaderTest {

    public UncompressedTiledReaderTest() throws IOException {
        super();
        writerParam.setTilingMode(ImageWriteParam.MODE_EXPLICIT);
        final int tileWidth  = (random.nextInt(TILE_MAX_RATIO) + 1) * TILE_MIN_SIZE;
        final int tileHeight = (random.nextInt(TILE_MAX_RATIO) + 1) * TILE_MIN_SIZE;

        writerParam.setTiling(tileWidth, tileHeight, 0, 0);
    }
}
