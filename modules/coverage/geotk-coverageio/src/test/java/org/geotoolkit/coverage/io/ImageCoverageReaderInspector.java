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
package org.geotoolkit.coverage.io;

import javax.imageio.IIOParam;
import org.opengis.referencing.operation.MathTransform2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.apache.sis.referencing.operation.transform.LinearTransform;


/**
 * An {@link ImageCoverageReader} which retains some intermediate computation during the
 * reading process.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.14
 */
final strictfp class ImageCoverageReaderInspector extends ImageCoverageReader {
    /**
     * The caller method.
     */
    private final String caller;

    /**
     * The difference between the requested grid and the actual grid which has been read.
     */
    private MathTransform2D differenceTransform;

    /**
     * Creates a new instance.
     */
    ImageCoverageReaderInspector(final String caller) {
        ignoreGridTransforms = false;
        this.caller = caller;
    }

    /**
     * Delegates to the default implementation and stores the result.
     */
    @Override
    protected MathTransform2D geodeticToPixelCoordinates(final GridGeometry2D gridGeometry,
            final GridCoverageStoreParam geodeticParam, final IIOParam pixelParam,
            final boolean isNetcdfHack) // TODO: DEPRECATED: to be removed in Apache SIS.
            throws CoverageStoreException
    {
        final MathTransform2D tr = super.geodeticToPixelCoordinates(gridGeometry, geodeticParam, pixelParam, isNetcdfHack);
        differenceTransform = tr;
        return tr;
    }

    /**
     * Returns {@code true} if the read operation matched the user request.
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
