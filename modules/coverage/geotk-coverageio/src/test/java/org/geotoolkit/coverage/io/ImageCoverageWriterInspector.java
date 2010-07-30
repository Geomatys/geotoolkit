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
package org.geotoolkit.coverage.io;

import java.io.IOException;
import javax.imageio.IIOParam;
import javax.imageio.ImageWriter;
import java.awt.image.RenderedImage;

import org.opengis.referencing.operation.MathTransform2D;

import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.referencing.operation.transform.LinearTransform;


/**
 * An {@link ImageCoverageWriter} which retains some intermediate computation during the
 * writing process.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 */
final class ImageCoverageWriterInspector extends ImageCoverageWriter {
    /**
     * The caller method.
     */
    private final String caller;

    /**
     * The format name, or {@code null} for auto-detect.
     */
    private final String format;

    /**
     * The difference between the requested grid and the actual grid which is used
     * for fetching the pixel values to write.
     */
    private MathTransform2D differenceTransform;

    /**
     * Creates a new instance.
     */
    ImageCoverageWriterInspector(final String caller) {
        this(caller, null);
    }

    /**
     * Creates a new instance which will force the usage of the given format.
     */
    ImageCoverageWriterInspector(final String caller, final String format) {
        this.caller = caller;
        this.format = format;
    }

    /**
     * If the format is unspecified, uses the format given at construction time.
     */
    @Override
    protected ImageWriter createImageWriter(String formatName,
            final Object output, final RenderedImage image) throws IOException
    {
        if (formatName == null) {
            formatName = format;
        }
        return super.createImageWriter(formatName, output, image);
    }

    /**
     * Delegates to the default implementation and stores the result.
     */
    @Override
    protected MathTransform2D geodeticToPixelCoordinates(final GridGeometry2D gridGeometry,
            final GridCoverageStoreParam geodeticParam, final IIOParam pixelParam)
            throws CoverageStoreException
    {
        final MathTransform2D tr = super.geodeticToPixelCoordinates(gridGeometry, geodeticParam, pixelParam);
        differenceTransform = tr;
        return tr;
    }

    /**
     * Returns {@code true} if the write operation matched the user request
     * without the need for a resampling operation.
     */
    boolean getReadMatchesRequest() {
        return isIdentity(differenceTransform);
    }

    /**
     * Returns debugging information.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder("differenceTransform in ");
        buffer.append(caller).append(":\n");
        Object print = differenceTransform;
        if (print instanceof LinearTransform) {
            print = ((LinearTransform) differenceTransform).getMatrix();
        }
        return buffer.append(print).append('\n').toString();
    }
}
