/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
package org.geotoolkit.internal.image.io;

import java.awt.geom.AffineTransform;


/**
 * Utilities methods specific to the GDAL library.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public final class GDAL {
    /**
     * Do not allow instantiation of this class.
     */
    private GDAL() {
    }

    /**
     * Creates an affine transform from the given GDAL GeoTransform coefficients.
     * Those coefficients are not in the usual order expected by matrix, affine
     * transforms or TFW files. The relationship from pixel/line (P,L) coordinates
     * to CRS are:
     *
     * {@preformat math
     *     X = c[0] + P*c[1] + L*c[2];
     *     Y = c[3] + P*c[4] + L*c[5];
     * }
     *
     * @param  c The GDAL coefficients as an array of length 6 (this is not verified).
     * @return The affine transform for the given coefficients.
     */
    public static AffineTransform getGeoTransform(final double... c) {
        return new AffineTransform(c[1], c[4], c[2], c[5], c[0], c[3]);
    }
}
