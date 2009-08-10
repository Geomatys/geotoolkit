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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;

/**
 *
 * @author Johann Sorel (Geomatys)
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
    public static byte[] toWKBwithSRID(Geometry geom){
        final byte[] wkb = new WKBWriter(2).write(geom);
        final int srid = geom.getSRID();
        final byte[] crs = SRIDGenerator.toBytes(srid, Version.V1);
        final byte[] compact = new byte[wkb.length+crs.length];

        int i=0;
        for(;i<crs.length;i++){
            compact[i] = crs[i];
        }
        for(int j=0; j<wkb.length; i++,j++){
            compact[i] = wkb[j];
        }

        return compact;
    }

}
