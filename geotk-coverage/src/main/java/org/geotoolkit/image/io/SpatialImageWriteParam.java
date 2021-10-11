/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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

import java.util.logging.LogRecord;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;

import org.geotoolkit.internal.image.io.Warnings;


/**
 * Default parameters for {@link SpatialImageWriter}.
 * This is a place-holder for future developments.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.08
 * @module
 */
public class SpatialImageWriteParam extends ImageWriteParam implements WarningProducer {
    /**
     * The image writer for which this {@code SpatialImageWriteParam} instance
     * has been created, or {@code null} if unknown.
     *
     * @since 3.15
     */
    protected final ImageWriter writer;

    /**
     * Creates a new, initially empty, set of parameters.
     *
     * @param writer The writer for which this parameter block is created, or {@code null}.
     */
    public SpatialImageWriteParam(final ImageWriter writer) {
        super((writer != null) ? writer.getLocale() : null);
        this.writer = writer;
    }

    /**
     * Invoked when a warning occurred. The default implementation
     * {@linkplain SpatialImageWriter#warningOccurred forwards the warning to the image writer}
     * given at construction time if possible, or logs the warning otherwise.
     */
    @Override
    public boolean warningOccurred(final LogRecord record) {
        return Warnings.log(writer, record);
    }

    /**
     * Returns a string representation of this block of parameters. The default implementation
     * formats the {@linkplain #sourceRegion source region}, subsampling values,
     * {@linkplain #sourceBands source bands} and {@linkplain #destinationOffset destination offset}
     * on a single line, completed by the list of {@linkplain DimensionSlice dimension slices}
     * (if any) on the next lines.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = SpatialImageReadParam.toStringBegining(this);
        return SpatialImageReadParam.toStringEnd(buffer, null);
    }
}
