/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.image.io;

import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;

import org.geotoolkit.util.Localized;


/**
 * Default parameters for {@link SpatialImageWriter}.
 * This is a place-holder for future developments.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.08
 * @module
 */
public class SpatialImageWriteParam extends ImageWriteParam implements Localized {
    /**
     * Creates a new, initially empty, set of parameters.
     *
     * @param writer The writer for which this parameter block is created, or {@code null}.
     */
    public SpatialImageWriteParam(final ImageWriter writer) {
        super((writer != null) ? writer.getLocale() : null);
    }

    /**
     * Returns a string representation of this block of parameters. This is mostly for debugging
     * purpose and may change in any future version. The current implementation formats the
     * {@linkplain #sourceRegion source region}, {@linkplain #destinationOffset destination offset}
     * and the subsampling values on a single line, with the list of {@linkplain DimensionSlice
     * dimension slices} (if any) on the next lines.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = SpatialImageReadParam.toStringBegining(this,
                sourceRegion, destinationOffset,
                sourceXSubsampling, sourceYSubsampling, sourceBands);
        return SpatialImageReadParam.toStringEnd(buffer, null);
    }
}
