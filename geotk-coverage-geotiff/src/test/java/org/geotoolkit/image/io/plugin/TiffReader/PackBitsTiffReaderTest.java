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

import com.sun.media.imageio.plugins.tiff.TIFFImageWriteParam;

import java.io.IOException;

/**
 * Improve tests from {@link TestTiffImageReaderWriter} for reading action,
 * with PackBits compression.
 *
 * @author Remi Marechal (Geomatys).
 * @see TIFFImageWriteParam#compressionTypes
 */
public strictfp class PackBitsTiffReaderTest extends TestTiffImageReader {

    public PackBitsTiffReaderTest() throws IOException {
        super("PackBits");
    }
}
