/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage;

import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class to parse a world image file.
 * Thoses files only hold 6 numeric values corresponding to the image
 * affine transform.
 *
 * @author Johann Sorel (Geomatys)
 */
public class WorldFileReader {

    private WorldFileReader() {
    }

    /**
     * Parse an input stream and return the contained Affine Transform
     * @param stream : world image file input stream
     * @return AffineTransform stored in the stream
     * @throws java.io.IOException : if stream is not a valid world image transform or numbers are unvalid
     */
    public static AffineTransform parse(InputStream stream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final double[] matrix = new double[6];
        // 0 : x pixel size
        // 1 : x rotation
        // 2 : y rotation
        // 3 : y pixel size
        // 4 : x upper left corner
        // 5 : y upper left corner

        int index = 0;
        String str;
        try {
            while ((str = reader.readLine()) != null && index < 6) {
                str = str.trim();
                if (str.isEmpty()) {
                    continue;
                }
                try {
                    matrix[index] = Double.parseDouble(str);
                } catch (NumberFormatException ex) {
                    throw new IOException("Not a number value in input stream", ex);
                }
                index++;
            }
        } finally {
            reader.close();
        }

        return new AffineTransform(matrix);
    }
}
