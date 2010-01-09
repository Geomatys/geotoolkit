/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.image.io.text;

import java.io.IOException;

import org.geotoolkit.test.TestData;


/**
 * Tests {@link AsciiGridReader} backed by a RAW file. The header is like a normal ASCII grid file,
 * but the data will be read from the binary file instead than the ASCII file. For ensuring that,
 * we use and ASCII file without data.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 */
public final class RawGridReaderTest extends AsciiGridReaderTest {
    /**
     * Creates a reader.
     */
    @Override
    protected AsciiGridReader createImageReader() throws IOException {
        AsciiGridReader.Spi spi = new AsciiGridReader.Spi();
        final AsciiGridReader reader = new AsciiGridReader(spi);
        reader.setInput(TestData.file(this, "grid-with-raw.asc"));
        return reader;
    }
}
