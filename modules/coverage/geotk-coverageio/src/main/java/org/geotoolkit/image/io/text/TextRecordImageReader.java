/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2010, Open Source Geospatial Foundation (OSGeo)
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

import javax.imageio.spi.ImageReaderSpi;


/**
 * @deprecated Moved to the {@link org.geotoolkit.image.io.plugin} package.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.06
 *
 * @since 1.2
 * @module
 */
@Deprecated
public class TextRecordImageReader extends org.geotoolkit.image.io.plugin.TextRecordImageReader {
    /**
     * Constructs a new image reader.
     *
     * @param provider The {@link ImageReaderSpi} that is constructing this object, or {@code null}.
     */
    public TextRecordImageReader(final Spi provider) {
        super(provider);
    }
}
