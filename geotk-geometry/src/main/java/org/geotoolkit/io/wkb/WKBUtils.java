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

package org.geotoolkit.io.wkb;

import org.locationtech.jts.geom.Geometry;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.locationtech.jts.io.WKBWriter;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WKBUtils {

    private WKBUtils(){
    }

    /**
     * Write a geometry in wkb with it's srid.
     * the first 5 bytes store the srid using the sridgenerator.
     * all remaining bytes are the geometry in wkb.
     *
     * @param geom : geometry to write
     * @return byte array
     */
    public static byte[] toWKBwithSRID(final Geometry geom){
        final byte[] wkb = new WKBWriter(2).write(geom);
        final int srid = geom.getSRID();
        final byte[] crs = SRIDGenerator.toBytes(srid, Version.V1);
        final byte[] compact = new byte[wkb.length+crs.length];

        System.arraycopy(crs, 0, compact, 0, crs.length);
        System.arraycopy(wkb, 0, compact, crs.length, wkb.length);

        return compact;
    }

}
