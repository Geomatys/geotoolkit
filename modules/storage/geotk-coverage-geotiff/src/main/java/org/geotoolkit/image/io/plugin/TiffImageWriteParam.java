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
package org.geotoolkit.image.io.plugin;

import javax.imageio.ImageWriter;
import org.geotoolkit.image.io.SpatialImageWriteParam;

/**
 * A specific implementation of {@link SpatialImageWriteParam} adapted with 
 * {@link TiffImageWriter} image writer use.
 *
 * @author Remi Marechal (Geomatys).
 */
public class TiffImageWriteParam extends SpatialImageWriteParam {

    public TiffImageWriteParam(ImageWriter writer) {
        super(writer);
        canOffsetTiles      = false;
        canWriteCompressed  = true;
        canWriteProgressive = false;
        canWriteTiles       = true;
        compressionTypes    = new String[]{"LZW", "PackBits"};
    }

    /**
     * {@inheritDoc }.
     */
    @Override
    public void setSourceBands(final int[] sourceBands) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
