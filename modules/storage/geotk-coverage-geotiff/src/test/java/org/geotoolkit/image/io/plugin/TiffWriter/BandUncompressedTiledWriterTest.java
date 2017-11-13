/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2016, Geomatys
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
package org.geotoolkit.image.io.plugin.TiffWriter;

import java.awt.image.BandedSampleModel;
import java.io.IOException;
import javax.imageio.ImageWriteParam;

/**
 * Effectuate all same test than super class with image build with {@link BandedSampleModel}.
 *
 * @author Remi Marechal (Geomatys)
 */
public class BandUncompressedTiledWriterTest extends BandUncompressedTiffWriterTest {

    public BandUncompressedTiledWriterTest() throws IOException {
        writerParam.setTilingMode(ImageWriteParam.MODE_EXPLICIT);

        final int tileWidth  = (random.nextInt(TILE_MAX_RATIO) + 1) * TILE_MIN_SIZE;
        final int tileHeight = (random.nextInt(TILE_MAX_RATIO) + 1) * TILE_MIN_SIZE;
        writerParam.setTiling(tileWidth, tileHeight, 0, 0);
    }
}
