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
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;


/**
 * @deprecated Moved to the {@link org.geotoolkit.image.io.plugin} package.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
 *
 * @since 3.07
 * @module
 */
@Deprecated
public class WorldFileImageReader extends org.geotoolkit.image.io.plugin.WorldFileImageReader {
    /**
     * Constructs a new image reader. The provider argument is mandatory for this constructor.
     * If the provider is unknown, use the next constructor below instead.
     *
     * @param  provider The {@link ImageReaderSpi} that is constructing this object.
     * @throws IOException If an error occured while creating the {@linkplain #main main} reader.
     */
    public WorldFileImageReader(final Spi provider) throws IOException {
        super(provider);
    }

    /**
     * Constructs a new image reader wrapping the given reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     * @param main The reader to use for reading the pixel values.
     */
    public WorldFileImageReader(final Spi provider, final ImageReader main) {
        super(provider, main);
    }
}
