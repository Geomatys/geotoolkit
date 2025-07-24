/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.dggs.healpix;

/**
 * String zone identifier from https://www.ivoa.net/documents/MOC/20190215/WD-MOC-1.1-20190215.pdf
 * Section 2.3.1 FITS serialization
 *
 * @author Johann Sorel (Geomatys)
 */
final class FitsSerialization {

    /**
     * @param order depth order, starting at 1.
     * @param pixel pixel number
     * @return unique cell identifier
     */
    public static long getHash(int order, long pixel) {
        if (order < 1) throw new IllegalArgumentException("Order must be at least 1");
        return (long)(4 * (Math.pow(4, order))) + pixel;
    }

    /**
     * @param hash
     * @return order starting at 1.
     */
    public static int getOrder(long hash) {
        return (int) (log2(hash / 4) / 2);
    }

    /**
     * @param hash
     * @return pixel number starting at 0.
     */
    public static long getPixel(long hash) {
        final int order = getOrder(hash);
        return hash - 4l * (long)(Math.pow(4, order));
    }

    private static double log2(long x) {
        return Math.log(x) / Math.log(2);
    }
}
