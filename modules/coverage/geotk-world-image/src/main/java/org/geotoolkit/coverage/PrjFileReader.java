/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.geotoolkit.referencing.CRS;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Utility class to parse a projection file.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 *
 * @deprecated Replaced by {@link org.geotoolkit.io.wkt.PrjFiles}
 */
@Deprecated
public class PrjFileReader {

    private PrjFileReader(){
    }

    /**
     * Parse an input stream and return the contained Coordinate Reference System
     * @param stream : prj file input stream
     * @return CoordinateReferenceSystem stored in the stream
     * @throws java.io.IOException : if stream is not a valid projection or reading error
     */
    public static CoordinateReferenceSystem parse(InputStream stream)throws IOException, FactoryException{
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        final StringBuilder sb = new StringBuilder();

        String str;
        try {
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
        } finally {
            reader.close();
        }

        return CRS.parseWKT(sb.toString());
    }

}
