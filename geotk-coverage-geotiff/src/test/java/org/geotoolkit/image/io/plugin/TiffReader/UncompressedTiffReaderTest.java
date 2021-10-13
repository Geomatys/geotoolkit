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

import java.io.IOException;

/**
 * Effectuate all tests from {@link TestTiffImageReaderWriter} without compression.
 *
 * @author Remi Marechal (Geomatys).
 */
public strictfp class UncompressedTiffReaderTest extends TestTiffImageReader {

    public UncompressedTiffReaderTest() throws IOException {
        super(null); //-- if no compression is setted the default implementation works with strips
    }
}
